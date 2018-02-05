package arkavidia.ljkeyboard.Activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import arkavidia.ljkeyboard.Api.Retrofit.ApiUtil.RajaOngkirApiUtil;
import arkavidia.ljkeyboard.Api.Retrofit.Service.RajaOngkirService;
import arkavidia.ljkeyboard.Database.SqliteDbHelper;
import arkavidia.ljkeyboard.Model.Firebase.InformasiToko;
import arkavidia.ljkeyboard.Model.Firebase.TemplateChat;
import arkavidia.ljkeyboard.Model.Retrofit.City.RajaOngkirResponse;
import arkavidia.ljkeyboard.Model.Retrofit.City.Result;
import arkavidia.ljkeyboard.Model.Sqlite.City;
import arkavidia.ljkeyboard.R;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterTokoBaruActivity extends AppCompatActivity {
    private static final String TAG = "RegisterTokoBaru";
    private static final String API_KEY = "f669819773a9f6b26a3ae4188a3f1ab9";
    private static final String INFORMASI_TOKO = "informasi-toko";
    private static final String TEMPLATE_CHAT = "template-chat";
    private static final String PESANAN_BARU = "pesanan-baru";
    private static final String PEMBAYARAN = "pembayaran";
    private static final String TERIMAKASIH = "terima-kasih";
    private static final String KIRIM_NOMOR_RESI = "kirim-nomor-resi";

    private Toolbar toolbar;
    private EditText txtEmail, txtPassword, txtNamaToko;
    private Button btnRegisterToko;

    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY,
            SheetsScopes.DRIVE };

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_toko_baru);

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtNamaToko = findViewById(R.id.txtNamaToko);

        btnRegisterToko = findViewById(R.id.btnRegisterToko);
        btnRegisterToko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTokoToFirebase();
            }
        });
    }

    private void registerTokoToFirebase(){
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        final String namaToko = txtNamaToko.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    txtEmail.setText("");
                    txtPassword.setText("");
                    String idToko = UUID.randomUUID().toString();
                    InformasiToko informasiToko = InformasiToko.builder()
                            .id(idToko)
                            .namaToko(namaToko)
                            .spreadsheetId("belum ada")
                            .build();
                    databaseReference.child(INFORMASI_TOKO).child(task.getResult().getUser().getUid()).setValue(informasiToko);
                    txtNamaToko.setText("");
                    saveDefaultTemplateChatToFirebaseDatabase(task.getResult().getUser().getUid());
                }
            }
        });
    }

    private void saveDefaultTemplateChatToFirebaseDatabase(String userId){
        databaseReference.child(TEMPLATE_CHAT).child(userId).child(PESANAN_BARU).setValue(defaultPesananBaruTemplateChat());
        databaseReference.child(TEMPLATE_CHAT).child(userId).child(PEMBAYARAN).setValue(defaultPembayaranTemplateChat());
        databaseReference.child(TEMPLATE_CHAT).child(userId).child(TERIMAKASIH).setValue(defaultTerimaKasihTemplateChat());
        databaseReference.child(TEMPLATE_CHAT).child(userId).child(KIRIM_NOMOR_RESI).setValue(defaultKirimNomorResiTemplateChat());
    }

    private TemplateChat defaultPesananBaruTemplateChat(){
        UUID idTemplate = UUID.randomUUID();
        return TemplateChat.builder()
                .id(idTemplate.toString())
                .templateTitle("pesanan-baru")
                .templateContent("Untuk memesan, tolong isi format pemesanan ini ya :\n" +
                        "\n" +
                        "Barang, qty, ukuran :\n" +
                        "Nama pemesan :\n" +
                        "Alamat pemesan :\n" +
                        "Nomor telepon pemesan :\n" +
                        "\n" +
                        "Terima kasih")
                .build();
    }
    private TemplateChat defaultPembayaranTemplateChat(){
        UUID idTemplate = UUID.randomUUID();
        return TemplateChat.builder()
                .id(idTemplate.toString())
                .templateTitle("pembayaran")
                .templateContent("Hai /nama/, terima kasih sudah memesan di /nama-toko/, berikut detail pesananmu :\n" +
                        "\n" +
                        "Detail pesanan :\n" +
                        "/list-produk/\n" +
                        "\n" +
                        "Ongkir : /ongkir/\n" +
                        "Total harga : /total-harga/\n" +
                        "Rekening tujuan :\n" +
                        "/list-rekening/\n" +
                        "Kirim bukti pembayaran ya kalau sudah ditransfer")
                .build();
    }
    private TemplateChat defaultTerimaKasihTemplateChat(){
        UUID idTemplate = UUID.randomUUID();
        return TemplateChat.builder()
                .id(idTemplate.toString())
                .templateTitle("terima-kasih")
                .templateContent("Terima kasih /nama/ sudah melakukan pembayaran \uD83D\uDE0D\n" +
                        "\n" +
                        "Untuk nomor resinya harap ditunggu dulu ya nanti akan kami kirimkan jika sudah terbit.\n" +
                        "\n" +
                        "Terima kasih \uD83D\uDE18")
                .build();
    }
    private TemplateChat defaultKirimNomorResiTemplateChat(){
        UUID idTemplate = UUID.randomUUID();
        return TemplateChat.builder()
                .id(idTemplate.toString())
                .templateTitle("kirim-nomor-resi")
                .templateContent("Hai /nama/, berikut adalah nomor resimu ya :\n" +
                        "\n" +
                        "Logistik : /logistik/\n" +
                        "Nomor resi : /nomor-resi/\n" +
                        "\n" +
                        "Terima kasih")
                .build();
    }

}
