package com.pokemap.markergenerator.helper;

/**
 * Created by AG on 20/07/2016.
 */
public class MarkerHelper {
    public static String formatPokeName(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(name.charAt(0));
        sb.append(name.charAt(1));
        sb.append("\n");
        sb.append(name.charAt(2));
        if (name.length() > 3) {
            sb.append(name.charAt(3));
        }
        return sb.toString();
    }


}
