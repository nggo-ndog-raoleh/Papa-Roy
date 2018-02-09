package arkavidia.ljkeyboard.Activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import arkavidia.ljkeyboard.Activity.InformasiToko.InformasiTokoActivity;
import arkavidia.ljkeyboard.Activity.TemplateChat.MenuUtamaTemplateChatActivity;
import arkavidia.ljkeyboard.Api.Retrofit.ApiUtil.RajaOngkirApiUtil;
import arkavidia.ljkeyboard.Api.Retrofit.Service.RajaOngkirService;
import arkavidia.ljkeyboard.Model.Firebase.AkunBank;
import arkavidia.ljkeyboard.Model.Firebase.InformasiToko;
import arkavidia.ljkeyboard.Model.Firebase.Produk;
import arkavidia.ljkeyboard.Model.Firebase.TemplateChat;
import arkavidia.ljkeyboard.R;
import arkavidia.ljkeyboard.TaskCompleted;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeScreen extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, TaskCompleted {

    private static final String TAG = "HomeScreen.class";
    private static final String INFORMASI_TOKO = "informasi-toko";
    private static final String SPREADSHEET = "spreadsheet";
    private static final String SPREADSHEET_ID = "spreadsheetId";
    private static final String SPREADSHEET_URL = "spreadsheetUrl";

    Toolbar toolbar;
    AlertDialog.Builder alertDialog;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    private ProgressDialog progressDialog;
    private Dialog dialog;
    private Button btnBuatSpreadsheet;

    private CardView cardViewTemplateChat, cardViewInformasiToko, cardViewRekapPesanan;

    RajaOngkirService rajaOngkirService;

    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY,
                                             SheetsScopes.DRIVE };

    public HomeScreen() throws IOException, GeneralSecurityException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        alertDialog = new AlertDialog.Builder(HomeScreen.this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = getCurrentLoggedInUser();

        progressDialog = new ProgressDialog(HomeScreen.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        dialog = new Dialog(HomeScreen.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_spreadsheet);
        dialog.setCancelable(false);
        btnBuatSpreadsheet = dialog.findViewById(R.id.btnBuatSpreadsheet);
        btnBuatSpreadsheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getResultsFromGoogleSpreadsheetsApi();
            }
        });

        if(isDeviceOnline()) {
            try {
                progressDialog.setMessage("Checking spreadsheets...");
                progressDialog.show();
                databaseReference.child(INFORMASI_TOKO).child(user.getUid()).child(SPREADSHEET).child(SPREADSHEET_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String spreadsheetId = dataSnapshot.getValue(String.class);
                        if (spreadsheetId.equals("belum ada")) {
                            progressDialog.dismiss();
                            dialog.show();
                        } else {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } catch (Exception ex){
                Log.i(TAG, ex.toString());
            }
        } else {
            Toast.makeText(this, "Please connect to internet!", Toast.LENGTH_SHORT).show();
        }
        rajaOngkirService = RajaOngkirApiUtil.getRajaOngkirService();

        cardViewTemplateChat = findViewById(R.id.cardViewTemplateChat);
        cardViewTemplateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenuUtamaTemplateChatActivity.class);
                startActivity(intent);
            }
        });

        cardViewInformasiToko = findViewById(R.id.cardViewInformasiToko);
        cardViewInformasiToko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InformasiTokoActivity.class);
                startActivity(intent);
            }
        });

        cardViewRekapPesanan = findViewById(R.id.cardViewRekapPesanan);
        cardViewRekapPesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RekapPesananActivity.class);
                startActivity(intent);
            }
        });

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_menu:
                alertDialog.setMessage("Are you sure to logout?");
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.signOut();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.create();
                alertDialog.show();
                break;
            default:
                break;
        }
        return true;
    }

    /**
    * method ini dieksekusi saat ada button click mengenai CREATE SPREADSHEET
    **/
    private void getResultsFromGoogleSpreadsheetsApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Log.e(TAG, "No network connection");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromGoogleSpreadsheetsApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Log.i(TAG, "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromGoogleSpreadsheetsApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromGoogleSpreadsheetsApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromGoogleSpreadsheetsApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }
    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }
    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                HomeScreen.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private FirebaseUser getCurrentLoggedInUser(){
        return firebaseAuth.getCurrentUser();
    }

    @Override
    public void onTaskComplete(arkavidia.ljkeyboard.Model.Firebase.Spreadsheet spreadsheet) {
        progressDialog.setMessage("Creating spreadsheets...");
        progressDialog.show();
        databaseReference.child(INFORMASI_TOKO).child(user.getUid()).child(SPREADSHEET).setValue(spreadsheet).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(HomeScreen.this, "Creating spreadsheet success!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(HomeScreen.this, "Creating spreasheet failed : " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, arkavidia.ljkeyboard.Model.Firebase.Spreadsheet> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Drive driveService = null;
        private Exception mLastError = null;
        private TaskCompleted mCallback;
        private ProgressDialog progressDialog;

        MakeRequestTask(GoogleAccountCredential credential) {
            progressDialog = new ProgressDialog(HomeScreen.this);
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("LJ KEYBOARD")
                    .build();
            driveService = new Drive.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("LJ KEYBOARD")
                    .build();
            this.mCallback = (TaskCompleted) HomeScreen.this;
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected arkavidia.ljkeyboard.Model.Firebase.Spreadsheet doInBackground(Void... params) {
            try {
                return createNewSpreadsheet();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private arkavidia.ljkeyboard.Model.Firebase.Spreadsheet createNewSpreadsheet() throws Exception {
            SpreadsheetProperties properties = new SpreadsheetProperties();
            properties.setTitle(user.getEmail());

            Spreadsheet content = new Spreadsheet();
            content.setProperties(properties);

            Sheets.Spreadsheets.Create request = this.mService.spreadsheets().create(content);
            Spreadsheet response = request.execute();
            String spreadsheetId = response.getSpreadsheetId();
            setPermission(driveService, spreadsheetId);
            String spreadsheetUrl = response.getSpreadsheetUrl();
            arkavidia.ljkeyboard.Model.Firebase.Spreadsheet spreadsheet = arkavidia.ljkeyboard.Model.Firebase.Spreadsheet.builder()
                    .spreadsheetId(spreadsheetId)
                    .spreadsheetUrl(spreadsheetUrl)
                    .build();
            appendColumnHeader(spreadsheetId);
            return spreadsheet;
        }

        private Permission setPermission(Drive service, String fileId) throws Exception{
            Permission newPermission = new Permission();
            newPermission.setType("anyone");
            newPermission.setRole("writer");
            return service.permissions().create(fileId, newPermission).execute();
        }

        private void appendColumnHeader(String spreadsheetId) throws IOException {
            String range = "A1:H1";
            List<Object> columnHeader = new ArrayList<>();
            columnHeader.add("Nama customer");
            columnHeader.add("Nomor telepon");
            columnHeader.add("Produk yang dibeli");
            columnHeader.add("Quantity");
            columnHeader.add("Total harga");
            columnHeader.add("Bank tujuan");
            columnHeader.add("Logistik");
            columnHeader.add("Ongkir");
            List<List<Object>> values = new ArrayList<>();
            values.add(columnHeader);
            ValueRange valueRange = new ValueRange();
            valueRange.setValues(values);
            Sheets.Spreadsheets.Values.Update request =
                    this.mService
                    .spreadsheets()
                    .values()
                            .update(spreadsheetId,
                                    range,
                                   valueRange)
                    .setValueInputOption("RAW");
            UpdateValuesResponse response = request.execute();
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
            Log.i(TAG, "AsyncTask started...");
        }

        @Override
        protected void onPostExecute(arkavidia.ljkeyboard.Model.Firebase.Spreadsheet result) {
            if (result == null) {
                Log.i(TAG, "No results returned...");
            } else {
                mCallback.onTaskComplete(result);
            }
            progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            HomeScreen.REQUEST_AUTHORIZATION);
                } else {
                    Log.e(TAG, "Error occured : " + mLastError.getMessage());
                }
            } else {
                Log.i(TAG, "Cancelled");
            }
        }
    }

}
