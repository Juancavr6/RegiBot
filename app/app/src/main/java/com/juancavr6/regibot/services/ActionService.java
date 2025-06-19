package com.juancavr6.regibot.services;

import android.accessibilityservice.AccessibilityService;

import android.content.Intent;
import android.graphics.Insets;
import android.graphics.Rect;

import android.os.Handler;
import android.util.Log;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.accessibility.AccessibilityEvent;

import com.juancavr6.regibot.executor.ActionLooper;

import java.util.concurrent.Executor;

public class ActionService extends AccessibilityService {

    private final String TAG = "ACTION_SERVICE";

    public ActionLooper actionLoop;
    public Thread actionLoopThread;

    //Display metrics
    public int displayWidth;
    public int displayHeight;



    //Main executor instance for tasks launching
    public Executor mainExecutor;
    public Handler mainHandler;


    @Override
    public void onCreate() {
        super.onCreate();
        mainExecutor = this.getMainExecutor();
        mainHandler = new Handler(getMainLooper());
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand(): Action recieved");

        if(intent!=null){

            String action =intent.getStringExtra("action");
            if(action == null){
                Log.d(TAG, "onStartCommand(): Action is null");
                return START_STICKY;
            }

            if(action.equals("play") ){

                getScreenSize(); // Initialize width, height and dpi

                if(actionLoop == null) actionLoop = new ActionLooper(this);
                actionLoopThread = new Thread(actionLoop);
                actionLoopThread.start();
            }
            else if(action.equals("pause")){ //Put loop thread on pause, not service
                Log.d(TAG, "onStartCommand(): Action Pause");
                if(actionLoopThread != null) {
                    actionLoop.pause();
                }
            }
            else if(action.equals("resume")){
                Log.d(TAG, "onStartCommand(): Action Resume");
                if(actionLoopThread != null) {
                    actionLoop.resume();
                }
            }
            else if(action.equals("destroy")){
                Log.d(TAG, "onStartCommand(): Action Destroy");
                actionLoop.stop();
            }
        }
        return START_STICKY;
    }
    private void getScreenSize() {
        /*
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        display.getMetrics(displayMetrics);

        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;
        //dpi = displayMetrics.densityDpi;*/
        WindowManager windowManager = getSystemService(WindowManager.class);
        WindowMetrics windowMetrics = windowManager.getCurrentWindowMetrics();

        WindowInsets windowInsets = windowMetrics.getWindowInsets();
        Insets insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());

        Rect bounds = windowMetrics.getBounds();

        displayWidth = bounds.width() - insets.left - insets.right;
        displayHeight = bounds.height() - insets.top - insets.bottom;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override
    public void onInterrupt() {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy(): stopping self");
    }

}