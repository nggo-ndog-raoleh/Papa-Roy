package arkavidia.ljkeyboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import arkavidia.ljkeyboard.Database.DBHelper;
import arkavidia.ljkeyboard.Model.CityModel;
import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    String api_key = "f669819773a9f6b26a3ae4188a3f1ab9";
    String url_city = "https://api.rajaongkir.com/starter/city";
    private final String USER_AGENT = "Mozilla/5.0";

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    DBHelper dbHelper;

    EditText txtEmail, txtPass;
    Button btnLogin;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alertDialog = new SpotsDialog(MainActivity.this, "Logging in...");

        dbHelper = new DBHelper(getApplicationContext());

        int data_count = dbHelper.countAllData();
        System.out.println("Data count : " + data_count);
        if(data_count==0){
            PerformNetworkRequest performNetworkRequest = new PerformNetworkRequest(MainActivity.this, USER_AGENT, api_key, dbHelper);
            performNetworkRequest.execute();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
    }

    private void Login(){
        alertDialog.show();
        firebaseAuth.signInWithEmailAndPassword(txtEmail.getText().toString(), txtPass.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                try {
                    if (task.isSuccessful()) {
                        alertDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Berhasil Login!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Gagal Login!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex){
                    System.out.println("Gagal login : " + ex.toString());
                    Toast.makeText(MainActivity.this, "Gagal login " + ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, Boolean> {

        String USER_AGENT;
        String api_key;
        DBHelper dbHelper;
        Context context;
        AlertDialog alertDialog;

        PerformNetworkRequest(Context context, String USER_AGENT, String api_key, DBHelper dbHelper){
            this.context = context;
            this.USER_AGENT = USER_AGENT;
            this.api_key = api_key;
            this.dbHelper = dbHelper;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("Mulai menambahkan data ke SQLITE...");
            alertDialog = new SpotsDialog(context, "Harap tunggu...");
            alertDialog.setCancelable(false);
            alertDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                getCity();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private void getCity() throws Exception {
            URL obj = new URL(url_city);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("key", api_key);
            con.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url_city);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            JSONObject rajaongkir = jsonObject.getJSONObject("rajaongkir");
            JSONArray results = rajaongkir.getJSONArray("results");
            for (int i=0; i<results.length(); i++){
                JSONObject results_object = results.getJSONObject(i);
                CityModel cityModel = new CityModel();
                cityModel.setCity_id(results_object.getString("city_id"));
                cityModel.setCity_name(results_object.getString("city_name"));
                dbHelper.AddCity(cityModel);
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            System.out.println("Selesai tambah data ke SQLITE");
            alertDialog.dismiss();
        }
    }
}
