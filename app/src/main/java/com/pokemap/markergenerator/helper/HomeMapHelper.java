package com.pokemap.markergenerator.helper;

import android.content.Context;
import android.location.LocationManager;
import android.util.DisplayMetrics;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.pokemap.markergenerator.model.MapArea;
import com.pokemap.markergenerator.model.Trail;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * All Home Map related static methods
 */
public class HomeMapHelper {

    /**
     * Generate options for a Google Map Marker
     *
     * @param context
     * @param t
     * @return
     */
    public static MarkerOptions getMapMarkerOption(Context context, List<BitmapDescriptor> pkmImgList, Trail t) {
        int pkmId = t.getPokemon().getId();
        String pkmName = t.getPokemon().getName();

        String dateToDisplay = formatDate(context, t.getTime() * 1000L);

        LatLng latLng = t.getLocation().convertToMapsModel();
        MarkerOptions options;

        options = new MarkerOptions()
                .position(latLng)
                .title("#" + pkmId + " " + pkmName)
                .snippet(dateToDisplay);
        //TODO: remettre cet opti / ces logos ?
//                .icon(pkmImgList.get(pkmId-1));
//                .icon(BitmapDescriptorFactory.fromResource(FileHelper.getDrawableIdByName(context, "p" + pkmId)))


        return options;
    }

    /**
     * Format the given timestamp to "15/07/2016 16:28"
     *
     * @param context
     * @param timestamp timestamp in MILLIseconds è..é
     * @return
     */
    public static String formatDate(Context context, long timestamp) {
        Date date = new Date(timestamp);
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        DateFormat hourFormat = android.text.format.DateFormat.getTimeFormat(context);
        String dateToDisplay = dateFormat.format(date) + " " + hourFormat.format(date);
        return dateToDisplay;
    }

    public static int pxFromDp(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int dpFromPx(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }


    public static MapArea calculateMapArea(VisibleRegion region) {
        MapArea mapArea = new MapArea();

        mapArea.latMin = region.nearRight.latitude; //latMin
        mapArea.latMax = region.farLeft.latitude; //latMax
        mapArea.lngMin = region.farLeft.longitude; //lngMin
        mapArea.lngMax = region.nearRight.longitude; //lngMax

        //we get a litle bit more than the actual area
        double areaHeight = calculateAbsoluteDistance(mapArea.latMax, mapArea.latMin);
        double areaWidth = calculateAbsoluteDistance(mapArea.lngMax, mapArea.lngMin);

        mapArea.latMin -= areaHeight / 2;
        mapArea.latMax += areaHeight / 2;
        mapArea.lngMin -= areaWidth / 2; //lngMin
        mapArea.lngMax += areaWidth / 2; //lngMax

        return mapArea;
    }

    public static double calculateAbsoluteDistance(double a, double b) {
        if (a < 0) {
            a = -a;
        }
        if (b < 0) {
            b = -b;
        }
        if (a > b) {
            return a - b;
        } else {
            return b - a;
        }
    }

    /**
     * Remove all items in B that are in A
     * No idea why removeAll() from List wasn't working
     *
     * @param aList what we want to clean
     * @param bList the items to remove
     */
    public static void removeAllBinA(List<Trail> aList, List<Trail> bList) {
        List<Trail> toRemove = new LinkedList<>();
        for (Trail a : aList) {
            for (Trail b : bList) {
                if (a.equals(b)) {
                    toRemove.add(a);
                }
            }
        }
        aList.removeAll(toRemove);
    }

    public static boolean isGPSEnabled(Context mContext) {
        //TODO handle this case
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
