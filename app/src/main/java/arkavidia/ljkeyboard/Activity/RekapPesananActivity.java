package arkavidia.ljkeyboard.Activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import arkavidia.ljkeyboard.Database.SqliteDbHelper;
import arkavidia.ljkeyboard.Model.Firebase.Spreadsheet;
import arkavidia.ljkeyboard.Model.Sqlite.RekapPesanan;
import arkavidia.ljkeyboard.R;
import arkavidia.ljkeyboard.TaskCompleted;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class RekapPesananActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, TaskCompleted {

    private static final String TAG = "RekapPesananActivity";
    private static final String INFORMASI_TOKO = "informasi-toko";
    private static final String SPREADSHEET = "spreadsheet";
    private static final String SPREADSHEET_ID = "spreadsheetId";
    private static final String SPREADSHEET_URL = "spreadsheetUrl";
    private static final String CONTENT_DIALOG_YES_NO = "Upload rekap pesanan ke google spreadsheets?";

    private Toolbar toolbar;
    private TableLayout tableLayout;
    private TableRow tableRowHeader;
    private Button btnUploadRekapPesananKeFirebase;

    private Dialog dialogYesNo;
    private TextView txtContentDialogYesNo;
    private Button btnYesDialogYesNo, btnNoDialogYesNo;

    private SqliteDbHelper sqliteDbHelper;

    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY,
            SheetsScopes.DRIVE };

    private String spreadsheetsId;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap_pesanan);

        dialogYesNo = new Dialog(RekapPesananActivity.this);
        dialogYesNo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogYesNo.setContentView(R.layout.dialog_yes_no);
        dialogYesNo.setCancelable(false);
        txtContentDialogYesNo = dialogYesNo.findViewById(R.id.txtContent);
        txtContentDialogYesNo.setText(CONTENT_DIALOG_YES_NO);
        btnYesDialogYesNo = dialogYesNo.findViewById(R.id.btnYes);
        btnYesDialogYesNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogYesNo.dismiss();
                getResultsFromGoogleSpreadsheetsApi();
            }
        });
        btnNoDialogYesNo = dialogYesNo.findViewById(R.id.btnNo);
        btnNoDialogYesNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogYesNo.dismiss();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        databaseReference.child(INFORMASI_TOKO).child(user.getUid()).child(SPREADSHEET).child(SPREADSHEET_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                spreadsheetsId = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        sqliteDbHelper = new SqliteDbHelper(getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tableLayout = findViewById(R.id.tableLayoutRekapPesanan);
        tableLayout.removeAllViews();
        tableRowHeader = new TableRow(RekapPesananActivity.this);
        tableRowHeader.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        initiateTableRowHeader();

        initiateTableRowContent();

        btnUploadRekapPesananKeFirebase = findViewById(R.id.btnUploadToSpreadsheets);
        btnUploadRekapPesananKeFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogYesNo.show();
            }
        });
    }

    private void initiateTableRowHeader(){
        TextView namaCustHeader = new TextView(RekapPesananActivity.this);
        namaCustHeader.setBackgroundResource(R.drawable.table_rekap_pesanan_cell);
        namaCustHeader.setTextColor(getResources().getColor(R.color.black));
        namaCustHeader.setPadding(10,5,10,5);
        namaCustHeader.setText("Nama customer");
        namaCustHeader.setTypeface(Typeface.DEFAULT_BOLD);
        tableRowHeader.addView(namaCustHeader);

        TextView noTelpHeader = new TextView(RekapPesananActivity.this);
        noTelpHeader.setBackgroundResource(R.drawable.table_rekap_pesanan_cell);
        noTelpHeader.setTextColor(getResources().getColor(R.color.black));
        noTelpHeader.setPadding(10,5,10,5);
        noTelpHeader.setText("Nomor telepon");
        noTelpHeader.setTypeface(Typeface.DEFAULT_BOLD);
        tableRowHeader.addView(noTelpHeader);

        TextView produkYangDibeliHeader = new TextView(RekapPesananActivity.this);
        produkYangDibeliHeader.setBackgroundResource(R.drawable.table_rekap_pesanan_cell);
        produkYangDibeliHeader.setTextColor(getResources().getColor(R.color.black));
        produkYangDibeliHeader.setPadding(10,5,10,5);
        produkYangDibeliHeader.setText("Produk yang dibeli");
        produkYangDibeliHeader.setTypeface(Typeface.DEFAULT_BOLD);
        tableRowHeader.addView(produkYangDibeliHeader);

        TextView quantityHeader = new TextView(RekapPesananActivity.this);
        quantityHeader.setBackgroundResource(R.drawable.table_rekap_pesanan_cell);
        quantityHeader.setTextColor(getResources().getColor(R.color.black));
        quantityHeader.setPadding(10,5,10,5);
        quantityHeader.setText("Quantity");
        quantityHeader.setTypeface(Typeface.DEFAULT_BOLD);
        tableRowHeader.addView(quantityHeader);

        TextView totalHargaHeader = new TextView(RekapPesananActivity.this);
        totalHargaHeader.setBackgroundResource(R.drawable.table_rekap_pesanan_cell);
        totalHargaHeader.setTextColor(getResources().getColor(R.color.black));
        totalHargaHeader.setPadding(10,5,10,5);
        totalHargaHeader.setText("Total harga");
        totalHargaHeader.setTypeface(Typeface.DEFAULT_BOLD);
        tableRowHeader.addView(totalHargaHeader);

        TextView bankTujuanHeader = new TextView(RekapPesananActivity.this);
        bankTujuanHeader.setBackgroundResource(R.drawable.table_rekap_pesanan_cell);
        bankTujuanHeader.setTextColor(getResources().getColor(R.color.black));
        bankTujuanHeader.setPadding(10,5,10,5);
        bankTujuanHeader.setText("Bank tujuan");
        bankTujuanHeader.setTypeface(Typeface.DEFAULT_BOLD);
        tableRowHeader.addView(bankTujuanHeader);

        TextView logistikHeader = new TextView(RekapPesananActivity.this);
        logistikHeader.setBackgroundResource(R.drawable.table_rekap_pesanan_cell);
        logistikHeader.setTextColor(getResources().getColor(R.color.black));
        logistikHeader.setPadding(10,5,10,5);
        logistikHeader.setText("Logistik");
        logistikHeader.setTypeface(Typeface.DEFAULT_BOLD);
        tableRowHeader.addView(logistikHeader);

        TextView ongkirHeader = new TextView(RekapPesananActivity.this);
        ongkirHeader.setBackgroundResource(R.drawable.table_rekap_pesanan_cell);
        ongkirHeader.setTextColor(getResources().getColor(R.color.black));
        ongkirHeader.setPadding(10,5,10,5);
        ongkirHeader.setText("Ongkir");
        ongkirHeader.setTypeface(Typeface.DEFAULT_BOLD);
        tableRowHeader.addView(ongkirHeader);

        tableLayout.addView(tableRowHeader);
    }

    private void initiateTableRowContent(){
        List<RekapPesanan> rekapPesananList = getRekapPesananListFromSqlite();
        for (RekapPesanan item:rekapPesananList
                ) {
            TableRow tableRowContent = new TableRow(RekapPesananActivity.this);
            TextView namaCust = new TextView(RekapPesananActivity.this);
            namaCust.setBackgroundResource(R.drawable.table_rekap_pesanan_cell);
            namaCust.setTextColor(getResources().getColor(R.color.black));
            namaCust.setPadding(10,0,10,0);
            namaCust.setText(item.getCustomer().getNama());
            tableRowContent.addView(namaCust);

            TextView noTelp = new TextView(RekapPesananActivity.this);
            noTelp.setBackgroundResource(R.drawable.table_rekap_pesanan_cell);
            noTelp.setTextColor(getResources().getColor(R.color.black));
            noTelp.setPadding(10,0,10,0);
            noTelp.setText(item.getCustomer().getNomorTelepon());
            tableRowContent.addView(noTelp);

            TextView produkYangDibeli = new TextView(RekapPesananActivity.this);
            produkYangDibeli.setBackgroundResource(R.drawable.table_rekap_pesanan_cell);
            produkYangDibeli.setTextColor(getResources().getColor(R.color.black));
            produkYangDibeli.setPadding(10,0,10,0);
            produkYangDibeli.setText(item.getProduk());
            tableRowContent.addView(produkYangDibeli);

            TextView quantity = new TextView(RekapPesananActivity.this);
            quantity.setBackgroundResource(R.drawable.table_rekap_pesanan_cell);
            quantity.setTextColor(getResources().getColor(R.color.black));
            quantity.setPadding(5,0,5,0);
            quantity.setText(String.valueOf(item.getQuantity()));
            tableRowContent.addView(quantity);

            TextView totalHarga = new TextView(RekapPesananActivity.this);
            totalHarga.setBackgroundResource(R.drawable.table_rekap_pesanan_cell);
            totalHarga.setTextColor(getResources().getColor(R.color.black));
            totalHarga.setPadding(10,0,10,0);
            totalHarga.setText(String.valueOf(item.getTotalHargaProduk()));
            tableRowContent.addView(totalHarga);

            TextView bankTujuan = new TextView(RekapPesananActivity.this);
            bankTujuan.setBackgroundResource(R.drawable.table_rekap_pesanan_cell);
            bankTujuan.setTextColor(getResources().getColor(R.color.black));
            bankTujuan.setPadding(10,0,10,0);
            bankTujuan.setText(item.getBank());
            tableRowContent.addView(bankTujuan);

            TextView logistik = new TextView(RekapPesananActivity.this);
            logistik.setBackgroundResource(R.drawable.table_rekap_pesanan_cell);
            logistik.setTextColor(getResources().getColor(R.color.black));
            logistik.setPadding(10,0,10,0);
            logistik.setText(item.getKurirLogistik());
            tableRowContent.addView(logistik);

            TextView ongkir = new TextView(RekapPesananActivity.this);
            ongkir.setBackgroundResource(R.drawable.table_rekap_pesanan_cell);
            ongkir.setTextColor(getResources().getColor(R.color.black));
            ongkir.setPadding(10,0,10,0);
            ongkir.setText(String.valueOf(item.getOngkir()));
            tableRowContent.addView(ongkir);

            tableLayout.addView(tableRowContent);
        }
    }

    private List<RekapPesanan> getRekapPesananListFromSqlite(){
        return sqliteDbHelper.getRekapPesananFromSqlite();
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
            Log.i(TAG, "Spreadsheet ID : " + spreadsheetsId);
            new RekapPesananActivity.MakeRequestTask(mCredential, spreadsheetsId, getRekapPesananListFromSqlite()).execute();
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
                RekapPesananActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void onTaskComplete(Spreadsheet spreadsheet) {
        Log.i(TAG, "Upload succeed!");
        sqliteDbHelper.deleteAllRecordsFromTableRekapPesanan();
        tableLayout.removeAllViews();
        initiateTableRowHeader();
        initiateTableRowContent();
        Toast.makeText(this, "Upload to google sheet completed!", Toast.LENGTH_SHORT).show();
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, Spreadsheet> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;
        private TaskCompleted mCallback;
        private ProgressDialog progressDialog;
        String spreadsheetId;
        List<RekapPesanan> rekapPesananList;

        MakeRequestTask(GoogleAccountCredential credential,
                        String spreadsheetId,
                        List<RekapPesanan> rekapPesananList) {
            this.spreadsheetId = spreadsheetId;
            this.rekapPesananList = rekapPesananList;
            progressDialog = new ProgressDialog(RekapPesananActivity.this);
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("LJ KEYBOARD")
                    .build();
            this.mCallback = (TaskCompleted) RekapPesananActivity.this;
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected Spreadsheet doInBackground(Void... params) {
            if(spreadsheetId!=null){
                try {
                    int numRows = getNumRows(spreadsheetId);
                    Log.i(TAG, "num Rows : " + String.valueOf(numRows));
                    appendColumn(numRows, spreadsheetId, rekapPesananList);
                } catch (Exception e) {
                    mLastError = e;
                    cancel(true);
                    return null;
                }
            } else{
                Toast.makeText(RekapPesananActivity.this, "Please try again...", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        private int getNumRows(String spreadsheetId) throws IOException {
            String range = "A:A";
            Log.i(TAG, "Spreadsheet ID : " + spreadsheetId);
            ValueRange result = this.mService.spreadsheets().values().get(spreadsheetId, range).execute();
            int numRows = result.getValues() != null ? result.getValues().size() : 0;
            return numRows;
        }

        private void appendColumn(int numRows, String spreadsheetId, List<RekapPesanan> rekapPesananList) throws IOException {
            progressDialog.show();
            Log.i(TAG, "Spreadsheet ID : " + spreadsheetId);
            String range;
            for (int i=0; i<rekapPesananList.size(); i++){
                range = "A" + String.valueOf(numRows+1) + ":" + "H" + String.valueOf(numRows+1);
                Log.i(TAG, "Range : " + range);
                List<Object> objects = new ArrayList<>();
                for (RekapPesanan item:rekapPesananList
                     ) {
                    objects.add(item.getCustomer().getNama());
                    objects.add(item.getCustomer().getNomorTelepon());
                    objects.add(item.getProduk());
                    objects.add(item.getQuantity());
                    objects.add(item.getTotalHargaProduk());
                    objects.add(item.getBank());
                    objects.add(item.getKurirLogistik());
                    objects.add(item.getOngkir());
                }
                List<List<Object>> values = new ArrayList<>();
                values.add(objects);
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
                numRows++;
            }
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
        protected void onPostExecute(Spreadsheet result) {
            mCallback.onTaskComplete(result);
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
