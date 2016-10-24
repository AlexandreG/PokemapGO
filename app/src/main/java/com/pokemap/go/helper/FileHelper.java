package com.pokemap.go.helper;

import android.content.Context;
import android.content.res.Resources;

import java.io.IOException;
import java.io.InputStream;

/**
 * All file related static methods
 */
public class FileHelper {
    private static final String TAG = FileHelper.class.getSimpleName();

    /**
     * Fetch the given asset from memory
     *
     * @param context
     * @param assetName
     * @return the asset as a String
     */
    public static String getAsset(Context context, String assetName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(assetName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static int getDrawableIdByName(Context context, String name) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        return resourceId;
    }
}
