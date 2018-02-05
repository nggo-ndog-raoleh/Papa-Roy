package arkavidia.ljkeyboard.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import arkavidia.ljkeyboard.Api.Retrofit.ApiUtil.RajaOngkirApiUtil;
import arkavidia.ljkeyboard.Api.Retrofit.Service.RajaOngkirService;
import arkavidia.ljkeyboard.Database.SqliteDbHelper;
import arkavidia.ljkeyboard.Model.Retrofit.City.RajaOngkirResponse;
import arkavidia.ljkeyboard.Model.Retrofit.City.Result;
import arkavidia.ljkeyboard.Model.Sqlite.City;
import arkavidia.ljkeyboard.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String API_KEY = "f669819773a9f6b26a3ae4188a3f1ab9";

    RajaOngkirService rajaOngkirService;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    SqliteDbHelper sqliteDbHelper;

    EditText txtEmail, txtPass;
    TextView txtRegister;
    Button btnLogin;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initiate();

        int dataCount = getRecordsCountFromSqlite();

        Log.i(TAG, "data count on sqlite : " + dataCount);

        if(dataCount==0){
            if(isNetworkAvailable()) {
                insertCitiesToSqliteFromApiRequest();
            } else {
                Toast.makeText(this, "Please connect to internet!", Toast.LENGTH_SHORT).show();
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()) {
                    login();
                } else {
                    Toast.makeText(LoginActivity.this, "Please connect to internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initiate(){
        progressDialog = new ProgressDialog(this);
        sqliteDbHelper = new SqliteDbHelper(LoginActivity.this);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);
        txtRegister = findViewById(R.id.txtRegister);
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterTokoBaruActivity.class);
                startActivity(intent);
            }
        });
        btnLogin = findViewById(R.id.btnLogin);

        rajaOngkirService = RajaOngkirApiUtil.getRajaOngkirService();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private int getRecordsCountFromSqlite(){
        return sqliteDbHelper.countDataTableCity();
    }

    private void insertCitiesToSqliteFromApiRequest(){
        progressDialog.setMessage("Saving city to sqlite...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        rajaOngkirService.getAllCity(API_KEY).enqueue(new Callback<RajaOngkirResponse>() {
            @Override
            public void onResponse(Call<RajaOngkirResponse> call, Response<RajaOngkirResponse> response) {
                if(response.isSuccessful()){
                    Log.i(TAG, response.body().getRajaOngkir().toString());
                    List<Result> cityList = response.body().getRajaOngkir().getResults();
                    for (Result result:cityList
                         ) {
                        City city = new City();
                        city.setId(result.getCityId());
                        city.setCityName(result.getCityName());
                        sqliteDbHelper.addCity(city);
                    }
                    progressDialog.dismiss();
                } else{
                    Log.e(TAG, "Error : " + response.code() + ", " + response.message());
                    Toast.makeText(LoginActivity.this,
                            "Error : " + response.code() + ", " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RajaOngkirResponse> call, Throwable t) {
                Log.e(TAG, "Failed : " + t.toString());
            }
        });
    }

    private void login(){
        progressDialog.setMessage("Logging in...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(txtEmail.getText().toString(), txtPass.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                try {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Berhasil login!", Toast.LENGTH_SHORT).show();
                        goToHomescreen();
                    } else {
                        Toast.makeText(LoginActivity.this, "Gagal login!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex){
                    Log.e(TAG, "Gagal login : " + ex.toString());
                    Toast.makeText(LoginActivity.this, "Gagal login " + ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToHomescreen(){
        Intent i = new Intent(LoginActivity.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
