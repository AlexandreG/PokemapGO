package com.pokemap.markergenerator.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * A helper for every Android Permission and gps related things
 */
public class PermissionHelper {

    public static boolean isGpsAllowed(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestGpsPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                requestCode);
    }

    public static boolean isResultOk(int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isGpsEnabled(Context c) {
        LocationManager locManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        ;
        return locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void startIntentEnableGps(Context c) {
        Intent gpsOptionsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        c.startActivity(gpsOptionsIntent);
    }

    public static boolean isGpsAllowedAlreadyRefused(Activity activity) {
        return !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * If the user has checked the box "never ask again" we got to take him to the settings
     *
     * @param c
     */
    public static void startIntentAllowGps(Context c) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", c.getPackageName(), null);
        intent.setData(uri);
        c.startActivity(intent);
    }
}
