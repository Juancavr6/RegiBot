package com.juancavr6.regibot.utils;


import android.app.Activity;
import android.app.LocaleManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.LocaleList;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.juancavr6.regibot.R;


public class DialogHelper {

    // Show a dialog to request overlay permissions

    public static void overlayPermissions(Context context,ActivityResultLauncher<Intent> launcher){

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(context.getString(R.string.displayText_overlap))
                .setIcon(R.drawable.vector_layers)
                .setCancelable(false) 

                .setPositiveButton(context.getString(R.string.displayText_enable), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,  Uri.parse("package:" + context.getPackageName()));
                        launcher.launch(intent);                   }
                })
                .setNegativeButton(context.getString(R.string.displayText_later), null);
        builder.create().show();
    }
 // Show a dialog to request accessibility permissions
    public static void accesibilityPermissions(Context context){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(context.getString(R.string.displayText_accesibility))
                .setIcon(R.drawable.icon_accesibility)
                .setMessage(context.getString(R.string.displayText_accesibility_desc))
                .setCancelable(false)

                .setPositiveButton(context.getString(R.string.displayText_enable), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        context.startActivity(intent);                    }
                })
                .setNegativeButton(context.getString(R.string.displayText_later), null);
        builder.create().show();
    }

    // Show a dialog to select the language
    public static void languageSelector(Activity activity, String currentLanguage) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        BottomSheetDialog dialog = new BottomSheetDialog(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_selector_language, null);
        dialog.setContentView(view);

        if(currentLanguage.equals("es")){
            view.findViewById(R.id.lang_spanish).setAlpha(0.5f);
        } else {
            view.findViewById(R.id.lang_english).setAlpha(0.5f);
        }


        view.findViewById(R.id.lang_english).setOnClickListener(v -> {
            sharedPref.edit().putString(activity.getString(R.string.preferences_key_language), "en").apply();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                LocaleManager localeManager = activity.getSystemService(LocaleManager.class);
                localeManager.setApplicationLocales(LocaleList.forLanguageTags("en"));
            }
            dialog.dismiss();
        });

        view.findViewById(R.id.lang_spanish).setOnClickListener(v -> {
            sharedPref.edit().putString(activity.getString(R.string.preferences_key_language), "es").apply();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                LocaleManager localeManager = activity.getSystemService(LocaleManager.class);
                localeManager.setApplicationLocales(LocaleList.forLanguageTags("es"));
            }
            dialog.dismiss();
        });

        dialog.show();
    }
    // Show a dialog to select the theme
    public static void themeSelector(Context context,int nightMode){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_selector_theme, null);
        dialog.setContentView(view);
        if(nightMode == Configuration.UI_MODE_NIGHT_YES){
            view.findViewById(R.id.theme_dark).setAlpha(0.5f);
        } else {
            view.findViewById(R.id.theme_light).setAlpha(0.5f);
        }

        view.findViewById(R.id.theme_light).setOnClickListener(v -> {

            sharedPref.edit().putBoolean(context.getString(R.string.preferences_key_theme), false).apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

            dialog.dismiss();
        });

        view.findViewById(R.id.theme_dark).setOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            sharedPref.edit().putBoolean(context.getString(R.string.preferences_key_theme), true).apply();
            dialog.dismiss();
        });

        dialog.show();
    }







}
