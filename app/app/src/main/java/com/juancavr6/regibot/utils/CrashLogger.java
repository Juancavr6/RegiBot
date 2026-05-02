package com.juancavr6.regibot.utils;

import android.content.Context;
import android.os.Build;

import com.juancavr6.regibot.ui.fragment.MoreFragment;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CrashLogger {

    private static final String CRASH_DIR = "crashes";
    private static final String LOG_FILE = "breadcrumbs.log";
    private static final int MAX_BREADCRUMBS = 50;

    private static final List<String> breadcrumbs = new LinkedList<>();
    private static Context context;

    public static void init(Context ctx) {
        context = ctx.getApplicationContext();
    }

    //before events
    public static void log(String message) {
        synchronized (breadcrumbs) {
            if (breadcrumbs.size() >= MAX_BREADCRUMBS) {
                breadcrumbs.remove(0);
            }
            breadcrumbs.add(timestamp() + " | " + message);
        }
    }

    // crash
    public static void logCrash(Throwable throwable) {
        try {
            File dir = new File(context.getFilesDir(), CRASH_DIR);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, "lastest_crash.txt");

            PrintWriter writer = new PrintWriter(new FileWriter(file));

            writer.println("--- CRASH REPORT ---");
            writer.println("Date: " + timestamp());

            writer.println("\n--- DEVICE INFO ---");
            writer.println("Brand: " + Build.BRAND);
            writer.println("Model: " + Build.MODEL);
            writer.println("SDK: " + Build.VERSION.SDK_INT);

            writer.println("\n--- APP INFO ---");
            writer.println("Package: " + context.getPackageName());

            writer.println("\n--- BREADCRUMBS ---");
            synchronized (breadcrumbs) {
                for (String b : breadcrumbs) {
                    writer.println(b);
                }
            }

            writer.println("\n--- STACKTRACE ---");
            throwable.printStackTrace(writer);

            writer.close();

        } catch (Exception ignored) {
        }
    }

    public static File[] getCrashFiles() {
        File dir = new File(context.getFilesDir(), CRASH_DIR);
        if (!dir.exists()) return new File[0];
        return dir.listFiles();
    }

    public static void clearCrashes() {
        File[] files = getCrashFiles();
        for (File f : files) {
            f.delete();
        }
    }

    private static String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }

    public static File getLatestCrashFile(Context context) {
        File dir = new File(context.getFilesDir(), CRASH_DIR);

        if (!dir.exists()) return null;

        File[] files = dir.listFiles();

        if (files == null || files.length == 0) return null;

        Arrays.sort(files, (f1, f2) ->
                Long.compare(f2.lastModified(), f1.lastModified())); //Debería haber solo uno pero por si acaso me quedo con el último

        return files[0];
    }

    public static String readCrashFile(File latest) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(latest))) {
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

        } catch (Exception e) {
            return "Reading error: " + e.getMessage();
        }

        return sb.toString();
    }
}
