package com.juancavr6.regibot;

import android.app.Application;

import com.juancavr6.regibot.utils.CrashLogger;

public class RegibotApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CrashLogger.init(this);

        Thread.UncaughtExceptionHandler defaultHandler =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {

            CrashLogger.logCrash(throwable);

            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable);
            }
        });
    }
}
