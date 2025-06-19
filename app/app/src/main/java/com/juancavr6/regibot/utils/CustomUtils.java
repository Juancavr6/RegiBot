package com.juancavr6.regibot.utils;


import android.graphics.Rect;

// Utility class for various custom methods
public class CustomUtils {

    // Removes all control characters from a string and trims it
    public static String trimAll(String s) {
        return s.replaceAll("\\p{C}", "").trim();
    }

    // Checks if a point (x, y) is within a valid section for a Pokeball
    public static boolean isValidSectionForPokeball(float x , float y , int displayWidth, int displayHeight) {
        int invalidSection = 200;

        int limitLeft = displayWidth / 4;
        int limitRight = displayWidth * 3 / 4;
        int limitTop = displayHeight - invalidSection;

        Rect validSection = new Rect(limitLeft, limitTop, limitRight, displayHeight);


        return validSection.contains((int) x, (int) y);
    }

    // Checks if a point (x, y) is within a valid section for a tap
    public static boolean isValidSectionForTap(float x , float y , int displayWidth, int displayHeight) {
        int invalidSection = Math.round(displayHeight*0.1f);

        int limitTop = displayHeight - invalidSection;

        Rect validSection = new Rect(0, limitTop, displayWidth, displayHeight);

        return !validSection.contains((int) x, (int) y);
    }

    // Calculates the x-coordinate at a given y-coordinate on a line defined by two points (x1, y1) and (x2, y2)
    public static float getXAtY(float x1, float y1, float x2, float y2, float yPrima) {
        if (y1 == y2) {
            return (yPrima == y1) ? x1 : -1;
        }

        float m = (y2 - y1) / (x2 - x1);

        float xPrima = x1 + (yPrima - y1) / m;

        if ((yPrima >= Math.min(y1, y2)) && (yPrima <= Math.max(y1, y2))) {
            return xPrima;
        } else {
            return -1; // Y' está fuera del rango
        }
    }


}
