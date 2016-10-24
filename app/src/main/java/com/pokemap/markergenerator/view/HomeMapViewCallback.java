package com.pokemap.markergenerator.view;

import android.view.View;

/**
 * Created by AG on 16/07/2016.
 */
public interface HomeMapViewCallback {
    void initDropDownView(String[] listPkmChoice);

    void showSendPkmDialog(String pokemonName);

    void startTurningSendButton(boolean b);

    void showSnackbar(int stringId);

    void showSnackbarAction(int stringId, int actionId, View.OnClickListener listener);
    void showSnackbarGpsNeeded();

    void showToast(int stringId);
}
