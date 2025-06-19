package com.juancavr6.regibot.executor;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.view.Display;

import androidx.annotation.NonNull;

import com.juancavr6.regibot.controller.SettingsController;
import com.juancavr6.regibot.ml.ModelHandler;
import com.juancavr6.regibot.services.ActionService;
import com.juancavr6.regibot.services.FloatingMenuService;
import com.juancavr6.regibot.utils.CustomUtils;

public class ActionLooper implements Runnable {

    private final String TAG = "ActionLooper";
    //AccessibilityService reference
    private final  ActionService service;
    private final Intent intent;

    //Running flags
    private boolean isRunning;
    private boolean isPaused;

    //Models instances
    private ModelHandler.Detector
            model_map,
            model_enconter,
            model_clickable;
    private ModelHandler.Classifier model_classifier;
    private ModelHandler.Predictor model_predictor;

    //Settings Controller instance
    private final SettingsController controller ;

    //Last screenshot made
    public Bitmap lastScreenShot;


    //Thread locker
    private final Object lock = new Object();



    public ActionLooper(ActionService service){
        this.service = service;
        this.controller = SettingsController.getInstance(service);
        this.intent = new Intent(service, FloatingMenuService.class);
        intent.putExtra("loadedNotification",true);

    }
    @Override
    public void run() {
        if(model_map==null) loadModels();
        controller.reloadAllValues();
        service.startService(intent);

        isRunning = true;
        isPaused = true;
        while (isRunning){
            if(!isPaused){
                try {
                    Thread.sleep(controller.getCycleInterval());

                    captureScreen();
                    synchronized(lock){lock.wait(controller.getWaitTimeout());} // Wait for screenshot

                    if(lastScreenShot != null){

                        model_classifier.classify(lastScreenShot);
                        Log.d(TAG,"run(): Class  " + model_classifier.getClassName(0) + " " + model_classifier.getScore(0) );

                        if(controller.isValidClassification(model_classifier)){
                            switch (model_classifier.getClassName(0)){
                                case "mapScreen":
                                    taskMapScreen();
                                    break;
                                case "pokestopScreen":
                                    taskPokestopScreen();
                                    break;
                                case "encounterScreen":
                                    taskEncounterScreen();
                                    break;
                                case "rewardScreen":
                                    taskRewardScreen();
                                    break;
                                case "eggScreen":
                                    taskEggScreen();
                                    break;
                                case "menusScreen":
                                    taskMenusScreen();
                                    break;
                                default:
                                    service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                    break;
                            }

                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }


    public void loadModels(){
        model_map = ModelHandler.buildDetector
           (service, "dmmv2.enc",controller.getMaxResults());
        model_enconter = ModelHandler.buildDetector
           (service, "dem.enc",controller.getMaxResults());
        model_clickable = ModelHandler.buildDetector
           (service, "dcmv2.enc",controller.getMaxResults());
        model_classifier = ModelHandler.buildClassifier
           (service, "cmv5.enc",controller.getMaxResults());
        model_predictor = ModelHandler.buildPredictor
                (service,"predictor.tflite");
    }
    public void pause(){ this.isPaused=true; }
    public void resume(){ this.isPaused=false; }
    public void stop(){ this.isRunning=false; }

    private void taskMapScreen() throws InterruptedException{
        model_map.detect(lastScreenShot);
        Log.d(TAG , "run(): Scanning the map : " + model_map.getDetectionList().toString());
        int objectMatchIndex = controller.lookForMatchAtMap(model_map);
        if(objectMatchIndex > -1){
            Log.d(TAG,"run(): Proceeding with: " + model_map.getClassName(objectMatchIndex));
            performActionTap(model_map.getBoundingBox(objectMatchIndex));
            synchronized(lock){lock.wait(controller.getWaitTimeout());}
        }
    }
    private void taskPokestopScreen() throws InterruptedException{
        performActionSpinDisc();
        synchronized(lock){lock.wait(controller.getWaitTimeout());}
    }
    private void taskEncounterScreen() throws InterruptedException{
        final float[] pokeballCoords = resolvePokeballCoords();

        if(pokeballCoords[1] != 0.0f){
            // Hold the Pokeball and look for the bounding box
            service.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    performActionHold(pokeballCoords);
                }
            });
            service.mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    captureScreen();
                }},50);
            synchronized(lock){lock.wait(controller.getWaitTimeout());}

            model_enconter.detect(lastScreenShot);
            Log.d(TAG,"run(): Finding BoundingBox " + model_enconter.getDetectionList());

            int boundingBoxIndex = controller.lookForMatchAtEncounter(model_enconter,"boundingBox");
            int dynamicBoxIndex = controller.lookForMatchAtEncounter(model_enconter,"dynamicBox");

            // If both bounding box and dynamic box are found, proceed with the throw
            if(boundingBoxIndex > -1 && dynamicBoxIndex > -1){

                manageThrow(boundingBoxIndex,pokeballCoords);

            }
        }
    }
    private void taskRewardScreen() throws InterruptedException{
        model_clickable.detect(lastScreenShot);
        int clickableIndex = controller.lookForMatchAtClickable(model_clickable,"clickable");
        Log.d(TAG,clickableIndex + " Finding Clickable:" + model_clickable.getDetectionList());
        if (clickableIndex > -1){
            Log.d(TAG,"run(): Clickable Found! ");
            performActionTap(model_clickable.getBoundingBox(clickableIndex));
            synchronized(lock){lock.wait(controller.getWaitTimeout());}
        }
    }
    private void taskEggScreen() {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        RectF centerBoxDisplay = new RectF(0,0,service.displayWidth,service.displayHeight);
        performActionTap(centerBoxDisplay);
    }
    private void taskMenusScreen() throws InterruptedException {
        model_clickable.detect(lastScreenShot);
        int passengerIndex = controller.lookForMatchAtClickable(model_clickable,"passenger");
        Log.d(TAG,passengerIndex + " Discarding Passenger Screen:" + model_clickable.getDetectionList());
        if (passengerIndex > -1){
            Log.d(TAG,"run(): */* You're going too fast! ");
            int clickableIndex = controller.lookForMatchAtClickable(model_clickable,"clickable");
            Log.d(TAG,clickableIndex + " Finding Clickable:" + model_clickable.getDetectionList());
            if (clickableIndex > -1){
                Log.d(TAG,"run(): Clickable Found! ");
                performActionTap(model_clickable.getBoundingBox(clickableIndex));
                synchronized(lock){lock.wait(controller.getWaitTimeout());}
            }
        }else service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    private void captureScreen (){
        service.takeScreenshot(Display.DEFAULT_DISPLAY, service.mainExecutor, new AccessibilityService.TakeScreenshotCallback() {
            @Override
            public void onSuccess(@NonNull AccessibilityService.ScreenshotResult screenshotResult) {

                try {
                    Log.w(TAG, "Screen Capture Completed");
                    Bitmap screenShot = Bitmap.wrapHardwareBuffer
                            (screenshotResult.getHardwareBuffer(), screenshotResult.getColorSpace());
                    screenShot = screenShot.copy(Bitmap.Config.ARGB_8888, true);

                    lastScreenShot = screenShot;
                    screenshotResult.getHardwareBuffer().close();
                    synchronized (lock) {
                        lock.notify();
                    }
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Capture conversion Failed");                }

            }

            @Override
            public void onFailure(int i) {
                Log.e(TAG, "Capture Failed");
                synchronized (lock) {
                    lock.notify();
                }
            }
        });

    }
    private void performActionTap(RectF boundingBox) {

        int x = Math.round(boundingBox.centerX());
        int y = Math.round(boundingBox.centerY());

        if(!CustomUtils.isValidSectionForTap(x,y, service.displayWidth, service.displayHeight))
            return;

        Path swipePath = new Path();
        swipePath.moveTo(x, y);
        swipePath.lineTo(x, y);

        Log.v(TAG,"Tapping -->" + x + " , " + y);

        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 10));
        service.dispatchGesture(gestureBuilder.build(), new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                synchronized(lock){lock.notify();}

            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                synchronized(lock){lock.notify();}

            }
        }, null);

    }
    private void performActionSpinDisc() {

        int x = Math.round((float)service.displayWidth/2);
        int y = Math.round((float)service.displayHeight/2);

        Path swipePath = new Path();
        swipePath.moveTo(x, y);
        swipePath.lineTo(x+200, y);

        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 300));

        service.dispatchGesture(gestureBuilder.build(), new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){Thread.currentThread().interrupt();}

                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);

                synchronized(lock){lock.notify();}


            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                synchronized(lock){lock.notify();}

            }
        }, null);
    }
    private void performActionHold(float[] coords) {

        int x = Math.round(coords[0]);
        int y = Math.round(coords[1]);
        Path swipePath = new Path();
        swipePath.moveTo(x, y);
        swipePath.lineTo(x, y);
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 4000));

        service.dispatchGesture(gestureBuilder.build(), new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                //synchronized(lock){lock.notify();}

            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.e(TAG,"Gesture Hold Cancelled " + gestureDescription.toString());
                //synchronized(lock){lock.notify();}

            }
        }, null);


    }
    private void performActionThrow(float[] pokeballCoords,RectF boundingBox ,float deltaY, long duration){
        Path swipePath = new Path();
        swipePath.moveTo(pokeballCoords[0], pokeballCoords[1]);
        float finalX = boundingBox.centerX();
        float finalY = pokeballCoords[1]+deltaY;
        swipePath.lineTo(finalX, finalY);

        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 50, Math.round(duration*controller.getThrowBoostDurationFactor())));

        service.dispatchGesture(gestureBuilder.build(), new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);

                synchronized(lock){lock.notify();}

            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);

                synchronized(lock){lock.notify();}

            }
        }, null);
    }
    private void performActionFastThrow(float[] pokeballCoords,RectF boundingBox ,float deltaY, long duration){

        Path swipePath = new Path();
        swipePath.moveTo(pokeballCoords[0], pokeballCoords[1]);
        float finalX = boundingBox.centerX();
        float finalY = pokeballCoords[1]+deltaY;
        swipePath.lineTo(finalX, finalY);

        Path berryPath = new Path();
        berryPath.moveTo(service.displayWidth/8f, service.displayHeight-(service.displayHeight/16f));
        berryPath.lineTo(service.displayWidth/8f, service.displayHeight/2f);

        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(berryPath, 20, 400));
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 40, Math.round(duration*controller.getThrowBoostDurationFactor())));


        service.dispatchGesture(gestureBuilder.build(), new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);

                // Los timings son puro freestyle
                service.mainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }},700);
                service.mainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }},1000);


            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.e(TAG,"Gesture FastThrow Cancelled " + gestureDescription.toString());

                synchronized(lock){lock.notify();}

            }
        }, null);
    }

    private float[] resolvePokeballCoords() {
        final float[] coords = new float[2];

        if (controller.shouldFixedPokeball()) {
            Log.d(TAG, "run(): Using fixed Pokeball coords");
            return getFixedCoords();
        }
        if (!controller.shouldSaveCoords() || !controller.isPokeballCoordsSet()) {
            model_enconter.detect(lastScreenShot);
            Log.d(TAG, "run(): Finding Pokeball: " + model_enconter.getDetectionList());

            int pokeballIndex = controller.lookForMatchAtEncounter(model_enconter, "pokeball");

            if (pokeballIndex > -1) {
                float centerX = model_enconter.getBoundingBox(pokeballIndex).centerX();
                float centerY = model_enconter.getBoundingBox(pokeballIndex).centerY();

                if (CustomUtils.isValidSectionForPokeball(centerX, centerY, service.displayWidth, service.displayHeight)) {
                    coords[0] = centerX;
                    coords[1] = centerY;

                    if (controller.shouldSaveCoords()) {
                        controller.setPokeballCoords(coords);
                    }
                    Log.d(TAG, "run(): Using detected Pokeball coords");
                    return coords;
                }
            }

            // fallback to fixed coords if detection failed
            Log.d(TAG, "run(): Using (fallback) fixed Pokeball coords");
            return getFixedCoords();
        }
        Log.d(TAG, "run(): Using saved Pokeball coords");
        return controller.getPokeballCoords();
    }
    private float[] getFixedCoords() {
        return new float[] {
                service.displayWidth / 2f,
                service.displayHeight - (service.displayHeight / 16f)
        };
    }
    private void manageThrow(int boundingBoxIndex,float[] pokeballCoords) throws InterruptedException {
        RectF boundingBox = model_enconter.getBoundingBox(boundingBoxIndex);
        float[] input = {boundingBox.centerX(),boundingBox.centerY(),
                boundingBox.width(),boundingBox.height()};
        float[] normalizedInput = model_predictor.getNormalizedInput(
                service.displayWidth,service.displayHeight,input);

        model_predictor.predict(normalizedInput);

        if(controller.shouldFastCatch()){
            performActionTap(new RectF(0,0,service.displayWidth,service.displayHeight));
            synchronized(lock){lock.wait(controller.getWaitTimeout());}

            service.mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    performActionFastThrow(pokeballCoords, boundingBox,
                            model_predictor.getDenormalizedDeltaY(service.displayHeight),
                            (long)model_predictor.getDenormalizedDuration());
                }},500);
            Thread.sleep(1700);
        }
        else{
            performActionThrow(pokeballCoords, boundingBox,
                    model_predictor.getDenormalizedDeltaY(service.displayHeight),
                    (long)model_predictor.getDenormalizedDuration());
            synchronized(lock){lock.wait(controller.getWaitTimeout());}
        }
    }
}
