package com.swalla.campusdock.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.swalla.campusdock.R;

import static com.swalla.campusdock.Utils.Config.PREF_NAME;
import static com.swalla.campusdock.Utils.Config.PREF_USER_IS_LOGGED_IN;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                if (pref.getBoolean(PREF_USER_IS_LOGGED_IN, false)) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), Registration.class));
                    finish();
                }
            }
        }, 1500);
    }
}
