package com.juancavr6.regibot;

import static com.juancavr6.regibot.controller.SettingsValuesProvider.CATEGORY_GENERAL;
import static com.juancavr6.regibot.controller.SettingsValuesProvider.CATEGORY_THRESHOLD;

import android.app.LocaleManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Looper;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.juancavr6.regibot.controller.SettingsValuesProvider;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.clear().apply();


        SettingsValuesProvider provider = SettingsValuesProvider.getInstance(this);

        // Load default values if not set

        if(sharedPreferences.getString(getString(R.string.preferences_key_wait_timeout),"").isEmpty()){
            provider.loadOnPreferences(this, CATEGORY_GENERAL);
        }
        if(sharedPreferences.getInt(getString(R.string.preferences_key_threshold_classifier),-1) == -1){
            provider.loadOnPreferences(this,CATEGORY_THRESHOLD);
        }

        if(sharedPreferences.getBoolean(getString(R.string.preferences_key_theme),false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        String language = sharedPreferences.getString(getString(R.string.preferences_key_language),"");
        if(!language.isEmpty()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                LocaleManager localeManager = getSystemService(LocaleManager.class);
                localeManager.getApplicationLocales().toLanguageTags();
                localeManager.setApplicationLocales(LocaleList.forLanguageTags(language));

            }
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this,NavigationActivity.class));
                finish();
            }
        },1800);

    }


}