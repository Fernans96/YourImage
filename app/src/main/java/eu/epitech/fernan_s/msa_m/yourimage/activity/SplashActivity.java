package eu.epitech.fernan_s.msa_m.yourimage.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import eu.epitech.fernan_s.msa_m.yourimage.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);;
        setContentView(R.layout.activity_splash);
        Intent intent;
        if (preferences.getBoolean("firstLaunch", true)){
            intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
