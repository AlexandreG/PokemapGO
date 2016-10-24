package com.pokemap.go.helper;

/**
 * Created by AG on 22/07/2016.
 */
public class TimeFilterHelper {

    //TODO : faire un object special du filtre temps
    public static final int ALL = 0;
    public static final int LAST_MONTH = 1;
    public static final int LAST_WEEK = 2;
    public static final int LAST_DAY = 3;
    public static final int LAST_HOUR = 4;

    public static final int DEFAULT_SELECTED_ITEM_POS = 1;

    public static final int getServerFilterCodeFromPosition(int posInList) {
        switch (posInList) {
            case 0:
                return LAST_HOUR;
            case 1:
                return LAST_DAY;
            case 2:
                return LAST_WEEK;
            case 3:
                return LAST_MONTH;
            case 4:
                return ALL;
            default:
                return -1;
        }
    }
}
