package com.samsung.mscr.gearconsolesdk.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class GCUtils {

    public static final String INTENT_NAME = "com.samsung.mscr.gearconsolesdk.intentname";
    public static final String EVENT_MOTION = "com.samsung.mscr.gearconsolesdk.event.motion";
    public static final String MOTION_ACCEL_GRAVITY = "accelerationIncludingGravity";
    public static final String MOTION_ACCEL = "acceleration";
    public static final String MOTION_ROTATION = "rotationRate";
    public static final String SUBSCRIBE_DEVICEMOTION = "devicemotion";

    /**
     * Convert drawable to bitmap
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Send notification to gear device
     * Be noted that it appears only when both devices are sleeping
     * Comment from source code: [SANotificationServiceForwardManager] Noti limited for dev ID XXX,  It will be forwarded when device is not awake.
     * @param context
     * @param packageName
     * @param text
     * @param message
     * @param notificationId
     * @param icon
     * @param appId
     */
    public static void sendNotification(Context context, String packageName, String text, String message, Integer notificationId, byte[] icon, String appId) {
        final String ALERT_NOTIFICATION =
                "com.samsung.accessory.intent.action.ALERT_NOTIFICATION_ITEM";
        final int NOTIFICATION_SOURCE_API_SECOND = 1;

        // Put data to Intent
        Intent myIntent = new Intent(ALERT_NOTIFICATION);
        //Mandatory
        myIntent.putExtra("NOTIFICATION_PACKAGE_NAME", packageName);
        //Mandatory
        myIntent.putExtra("NOTIFICATION_VERSION", NOTIFICATION_SOURCE_API_SECOND);
        //Optional
        myIntent.putExtra("NOTIFICATION_TIME", System.currentTimeMillis());
        //Mandatory if no NOTIFICATION_TEXT_MESSAGE is provided
        myIntent.putExtra("NOTIFICATION_MAIN_TEXT", text);
        //Mandatory if no NOTIFICATION_MAIN_TEXT is provided
        myIntent.putExtra("NOTIFICATION_TEXT_MESSAGE", message);
        //Optional
        if (icon != null) myIntent.putExtra("NOTIFICATION_APP_ICON", icon);
        //Optional
        //What application to launch when "show more" button is pressed on gear side
        //This feature will be unavailable when using NOTIFICATION_LAUNCH_TOACC_INTENT
//        myIntent.putExtra("NOTIFICATION_LAUNCH_INTENT", "com.samsung.mscr.gearconsoledemo");
//        myIntent.putExtra("NOTIFICATION_LAUNCH_INTENT", "browser");
        //Optional
        //What application to launch on Gear side after tapping on notification
        if (appId != null) myIntent.putExtra("NOTIFICATION_LAUNCH_TOACC_INTENT", appId);
        //Mandatory
        //Used for synchronization (dismissing, updating)
        myIntent.putExtra("NOTIFICATION_ID", notificationId);
        context.sendBroadcast(myIntent);
    }

    public static boolean isAppInstalled(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean appInstalled = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            appInstalled = false;
        }
        return appInstalled ;
    }

    /**
     * Return next requestId by incrementing previous
     * then store it to SharedPreferences
     * @param context
     * @return
     */
    public static int getNextRequestId(Context context) {
        SharedPreferences pref = context.getSharedPreferences("GEAR_CONSOLE", Context.MODE_PRIVATE);
        int requestId = pref.getInt("requestId", 1);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("requestId", ++requestId);
        editor.commit();
        return requestId;
    }
}