package arkavidia.ljkeyboard.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import arkavidia.ljkeyboard.Database.SqliteDbHelper;
import arkavidia.ljkeyboard.R;

public class SplashScreen extends AppCompatActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 500;

    SqliteDbHelper sqliteDbHelper;

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
                    intent = new Intent(SplashScreen.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                startActivity(intent);
                finish();
            }
        };
    }

}
