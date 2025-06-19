package com.juancavr6.regibot;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.card.MaterialCardView;
import com.juancavr6.regibot.R;
import com.juancavr6.regibot.services.ActionService;

import java.util.List;

public class PermissionActivity extends AppCompatActivity {

    ImageView imageViewBack;
    Button buttonAccesibility,buttonOverlay;
    MaterialCardView cardViewAccesibility,cardViewOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        imageViewBack = findViewById(R.id.buttonBack);
        buttonOverlay = findViewById(R.id.buttonEnableOverlay);
        buttonAccesibility = findViewById(R.id.buttonEnableAccesibility);
        cardViewAccesibility = findViewById(R.id.cardViewAccessibility);
        cardViewOverlay = findViewById(R.id.cardViewOverlay);

        imageViewBack.setOnClickListener(v -> finish());
        buttonAccesibility.setOnClickListener(v -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        });
        buttonOverlay.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,  Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        });

        checkPermissions();
    }

    private void checkPermissions(){

        if (Settings.canDrawOverlays(this)) {
            cardViewOverlay.setStrokeColor(Color.parseColor("#9eeb75"));
            buttonOverlay.setClickable(false);
            buttonOverlay.setBackgroundColor(Color.parseColor("#9eeb75"));
            buttonOverlay.setText("✔");
        }
        else{
            cardViewOverlay.setStrokeColor(Color.parseColor("#fb6d4e"));
        }

        if(isAccessibilityServiceEnabled(this, ActionService.class)){
            cardViewAccesibility.setStrokeColor(Color.parseColor("#9eeb75"));
            buttonAccesibility.setClickable(false);
            buttonAccesibility.setBackgroundColor(Color.parseColor("#9eeb75"));
            buttonAccesibility.setText("✔");
        }
        else{
            cardViewAccesibility.setStrokeColor(Color.parseColor("#fb6d4e"));
        }
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(service.getName()))
                return true;
        }

        return false;
    }

}