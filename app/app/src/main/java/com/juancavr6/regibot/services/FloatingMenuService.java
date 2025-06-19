package com.juancavr6.regibot.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.juancavr6.regibot.R;
import com.juancavr6.regibot.ui.fragment.HomeFragment;

public class FloatingMenuService extends Service implements View.OnClickListener{

    private final IBinder mBinder = new LocalBinder();
    Callbacks fragment;

    private boolean isRunning = false;

    private WindowManager mWindowManager;
    private View myFloatingView;

    private ProgressBar loader;
    private ImageView mainIcon;
    private CardView mainButton;
    private CardView destroyButton;

    private final BroadcastReceiver screenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                fragment.updateClient(false);
            }
        }
    };

    public FloatingMenuService() {


    }



    @Override
    public IBinder onBind(Intent intent) {return mBinder;}
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, filter);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if( loader != null && intent != null && intent.getBooleanExtra("loadedNotification", false)) {
            loader.setVisibility(View.GONE);
            mainButton.setVisibility(View.VISIBLE);
            destroyButton.setVisibility(View.VISIBLE);

            fragment.updateClient(true);
            Toast.makeText(this, getString(R.string.displayText_ready), Toast.LENGTH_SHORT).show();

        }
        else{
            //getting the widget layout from xml using layout inflater
            myFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_menu, null);


            int layout_parms = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;


            //setting the layout parameters
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    layout_parms,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP;

            //getting windows services and adding the floating view to it
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(myFloatingView, params);

            //adding an touchlistener to make drag movement of the floating widget
            myFloatingView.findViewById(R.id.thisIsAnID).setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = params.x;
                            initialY = params.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;

                        case MotionEvent.ACTION_UP:

                            return true;

                        case MotionEvent.ACTION_MOVE:
                            //this code is helping the widget to move around the screen with fingers
                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);
                            mWindowManager.updateViewLayout(myFloatingView, params);
                            return true;
                    }
                    return false;
                }
            });

            //getting the widgets reference from xml layout
            loader = myFloatingView.findViewById(R.id.loader);
            mainIcon = myFloatingView.findViewById(R.id.mainIcon);
            mainButton =  myFloatingView.findViewById(R.id.main);
            mainButton.setOnClickListener(this);
            destroyButton = myFloatingView.findViewById(R.id.destroy);
            destroyButton.setOnClickListener(this);
        }

           return START_NOT_STICKY;
    }

    public class LocalBinder extends Binder {
        public FloatingMenuService getServiceInstance(){
            return FloatingMenuService.this;
        }
    }

    public void registerClient(HomeFragment fragment){
        this.fragment = fragment;
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(getApplicationContext(), ActionService.class);
        int id = v.getId();
        if (id == R.id.main) {
            if(isRunning){
                intent.putExtra("action", "pause");
            }else{
                intent.putExtra("action", "resume");
            }
            isRunning = !isRunning;
            updateMainButton();

        } else if (id == R.id.destroy) {
            isRunning = false;
            fragment.updateClient(false);
        }
        getApplication().startService(intent);
    }


    @Override
    public void onDestroy() {

        Intent intent = new Intent(getApplicationContext(), ActionService.class);
        intent.putExtra("action", "destroy");
        getApplication().startService(intent);

        super.onDestroy();
        mWindowManager.removeView(myFloatingView);
        unregisterReceiver(screenReceiver);
    }

    private void updateMainButton() {
        if(!isRunning){
            mainButton.setCardBackgroundColor(Color.parseColor("#979797"));
            mainIcon.setImageResource(android.R.drawable.ic_media_play);
        }else{
            mainButton.setCardBackgroundColor(Color.parseColor("#C4C4C4"));
            mainIcon.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    public interface Callbacks{
        void updateClient(boolean data);
    }


}