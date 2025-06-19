package com.juancavr6.regibot.ui.fragment;

import static com.juancavr6.regibot.PermissionActivity.isAccessibilityServiceEnabled;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.juancavr6.regibot.R;
import com.juancavr6.regibot.controller.SettingsController;
import com.juancavr6.regibot.services.ActionService;
import com.juancavr6.regibot.services.FloatingMenuService;
import com.juancavr6.regibot.ui.adapter.RecyclerAdapterMenuPriority;
import com.juancavr6.regibot.ui.decorator.ListSeparatorDecoration;
import com.juancavr6.regibot.utils.DialogHelper;


public class HomeFragment extends Fragment implements FloatingMenuService.Callbacks {


    private Context context;
    private Intent floatMenuServiceIntent;
    private Intent actionServiceIntent;
    private FloatingMenuService fmService;
    private Boolean isRunning = false;
    private SettingsController controller;


    //UI
    private CardView mainButton;
    private ImageView mainButtonIcon;
    private TextView mainButtonText;
    private RecyclerView recycler_priority;
    private RecyclerView.LayoutManager layoutManager_recycler;
    private RecyclerAdapterMenuPriority adapter_priority;
    private SwitchCompat switch_fastCatch,switch_fixed, switch_throwBoost,switch_saveCoords;
    private MaterialCardView kofiImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //Init context and controller
        context = getContext();
        controller = SettingsController.getInstance(context);

        //Init Intents for services
        floatMenuServiceIntent = new Intent(context, FloatingMenuService.class);
        actionServiceIntent = new Intent(context, ActionService.class);

        //UI loaders
        initViews(root);
        setListeners(root);
        loadViews();

        return root;
    }

    //Launcher for open Overlay Permissions Screen
    public ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                }
            });

    //Service Connection for FloatMenuService callbacks
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Toast.makeText(context, getString(R.string.displayText_loading)+"...", Toast.LENGTH_SHORT).show();
            // We've binded to LocalService, cast the IBinder and get LocalService instance
            FloatingMenuService.LocalBinder binder = (FloatingMenuService.LocalBinder) service;
            fmService = binder.getServiceInstance(); //Get instance
            fmService.registerClient(HomeFragment.this); //Activity register in the service as client for callabcks!
        }


        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            //Toast.makeText(context, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();
        }
    };


    // Check if the permissions are granted
    private boolean checkPermissions(){

        if (!Settings.canDrawOverlays(context)) {
            DialogHelper.overlayPermissions(context,startActivityIntent);
        }else if(!isAccessibilityServiceEnabled(context, ActionService.class)){
            DialogHelper.accesibilityPermissions(context);
        }else return true;

        return false;
    }


    // Update the client with the service state
    @Override
    public void updateClient(boolean data) {
        isRunning = data;
        if(isRunning){// Service ready to play
            mainButton.setClickable(true);
        }
        else{ // Stopping the service
            context.unbindService(mConnection);
            context.stopService(floatMenuServiceIntent);
            updateMainButton();
        }


    }


    // --- UI
    public void initViews(View root) {
        mainButtonIcon = root.findViewById(R.id.mainButtonIcon);
        mainButtonText = root.findViewById(R.id.mainButtonText);
        kofiImage = root.findViewById(R.id.imageKofi);
        mainButton = root.findViewById(R.id.mainButton);
        recycler_priority = root.findViewById(R.id.recyclerPriority);
        layoutManager_recycler = new LinearLayoutManager(context);
        switch_fastCatch = root.findViewById(R.id.switch1);
        switch_throwBoost = root.findViewById(R.id.switch2);
        switch_fixed = root.findViewById(R.id.switch3);
        switch_saveCoords = root.findViewById(R.id.switch4);


        if(controller.shouldFastCatch())
            switch_fastCatch.setChecked(true);
        if (controller.shouldThrowBoost())
            switch_throwBoost.setChecked(true);
        if(controller.shouldFixedPokeball()){
            switch_fixed.setChecked(true);
            switch_saveCoords.setEnabled(false);
        }else if(controller.shouldSaveCoords())
            switch_saveCoords.setChecked(true);






    }
    public void setListeners(View root) {
        kofiImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://ko-fi.com/juancavr6"));
            startActivity(intent);
        });
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissions()){
                    if(isRunning){
                        updateClient(false);
                    }
                    else{
                        context.bindService(floatMenuServiceIntent,mConnection,Context.BIND_AUTO_CREATE);
                        context.startService(floatMenuServiceIntent);

                        actionServiceIntent.putExtra("action","play");
                        context.startService(actionServiceIntent);

                        launchPokemonGoApp();
                        isRunning = true;
                        mainButton.setClickable(false);
                        updateMainButton();
                    }

                }
            }
        });
        recycler_priority.setLayoutManager(layoutManager_recycler);
        recycler_priority.addItemDecoration(new ListSeparatorDecoration(context));
        switch_fastCatch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            controller.setFastCatch(isChecked);
        });
        switch_throwBoost.setOnCheckedChangeListener((buttonView, isChecked) -> {
            controller.setThrowBoost(isChecked);
        });
        switch_fixed.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                controller.setFixedPokeball(true);
                controller.setSaveCoords(false);
                switch_saveCoords.setChecked(false);
                switch_saveCoords.setEnabled(false);
            } else {
                controller.setFixedPokeball(false);
                switch_saveCoords.setEnabled(true);
            }
        });
        switch_saveCoords.setOnCheckedChangeListener((buttonView, isChecked) -> {
            controller.setSaveCoords(isChecked);
        });
    }


    private void loadViews() {

        adapter_priority = new RecyclerAdapterMenuPriority(context);

        recycler_priority.setAdapter(adapter_priority);
        recycler_priority.setHasFixedSize(true);
    }

    private void  updateMainButton(){


        if (isRunning){
            mainButton.setCardBackgroundColor(Color.parseColor("#C4C4C4"));
            mainButtonIcon.setImageResource(android.R.drawable.ic_media_pause);
            mainButtonText.setText("STOP");
        }
        else{
            mainButton.setCardBackgroundColor(Color.parseColor("#673AB7"));
            mainButtonIcon.setImageResource(android.R.drawable.ic_media_play);
            mainButtonText.setText("START");

        }


    }
  // Launch the Pokemon Go app
    private void launchPokemonGoApp() {
        try{
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.nianticlabs.pokemongo");
            startActivity( launchIntent );
        }
        catch (Exception e){
            Toast.makeText(context, "Failed to start Pokemon Go", Toast.LENGTH_SHORT).show();
        }
    }
}