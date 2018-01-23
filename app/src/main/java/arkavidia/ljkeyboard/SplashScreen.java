package arkavidia.ljkeyboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.login.LoginException;

import arkavidia.ljkeyboard.Database.DBHelper;
import arkavidia.ljkeyboard.Model.CityModel;
import dmax.dialog.SpotsDialog;

public class SplashScreen extends AppCompatActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 500;

    DBHelper dbHelper;

    String api_key = "f669819773a9f6b26a3ae4188a3f1ab9";
    String url_city = "https://api.rajaongkir.com/starter/city";
    private final String USER_AGENT = "Mozilla/5.0";

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener authStateListener;

    Intent intent;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(user!=null){
                    intent = new Intent(SplashScreen.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                } else {
                    intent = new Intent(SplashScreen.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                startActivity(intent);
                finish();
            }
        };
    }

}
