package com.lumi;

import java.nio.ByteBuffer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;

public class Comm {

    static byte[] bytes = new byte[6];

    public static String displayLEDString(int row, int column, int color) {
        String str = "d" + row + column + Integer.toHexString(color).substring(2);
        return str;
    }

    public static byte[] displayLEDBytes(int row, int column, int color) {
        byte[] colors = ByteBuffer.allocate(4).putInt(color).array();
        // colors[0] contains the alpha, which is not used for Lumi
        // colors[1-3] contain values for red, green, and blue, respectively

        bytes[0] = 'd';
        bytes[1] = (byte) (row & 0xFF);
        bytes[2] = (byte) (column & 0xFF);
        bytes[3] = colors[1]; // Red
        bytes[4] = colors[2]; // Green
        bytes[5] = colors[3]; // Blue

        /*for (byte b : bytes) {
            Log.i("Bytestream", "B: " + b);
        }*/

        return bytes;
    }
    
    public static byte[] displayImageBytes(int row, int column, int color) {
        byte[] colors = ByteBuffer.allocate(4).putInt(color).array();
        // colors[0] contains the alpha, which is not used for Lumi
        // colors[1-3] contain values for red, green, and blue, respectively

        bytes[0] = 'd';
        bytes[1] = (byte) (row & 0xFF);
        bytes[2] = (byte) (column & 0xFF);
        bytes[3] = colors[1]; // Red
        bytes[4] = colors[2]; // Green
        bytes[5] = colors[3]; // Blue

        /*for (byte b : bytes) {
            Log.i("Bytestream", "B: " + b);
        }*/

        return bytes;
    }
    
    public static String clearScreen() {
        return "clrscr";
    }

    // Thanks to Andrew:
    // http://stackoverflow.com/questions/5081145/android-how-do-you-check-if-a-particular-accessibilityservice-is-enabled
    /**
     * Checks of Accessibility has been enabled for the Lumi application
     * @param context Context of calling activity
     * @return true/false
     */
    public static boolean isLumiAccessibilityEnabled(Context context) {
        final String LOGTAG = "Lumi";
        final String LUMI_ACCESSIBILITY_SERVICE = "com.lumi.NotificationCatcherService";
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.d(LOGTAG, "ACCESSIBILITY: " + accessibilityEnabled);
        } catch (SettingNotFoundException e) {
            Log.d(LOGTAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.d(LOGTAG, "***ACCESSIBILIY IS ENABLED***: ");

            String settingValue = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Log.d(LOGTAG, "Setting: " + settingValue);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    Log.d(LOGTAG, "Setting: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(LUMI_ACCESSIBILITY_SERVICE)) {
                        Log.d(LOGTAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }

            Log.d(LOGTAG, "***END***");
        } else {
            Log.d(LOGTAG, "***ACCESSIBILIY IS DISABLED***");
        }
        return false;
    }
}
