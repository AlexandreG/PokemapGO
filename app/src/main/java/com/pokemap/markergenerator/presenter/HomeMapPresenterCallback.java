package com.pokemap.markergenerator.presenter;

/**
 * Created by AG on 16/07/2016.
 */
public interface HomeMapPresenterCallback {
    void onCreate();

    void onResume();

    void onPokemonSearched(String pkmName);

    void onClickSettingsButton();

    void onClickYesSendPkm(String pokemonName);

    void onGpsPermissionAllowed();

    void onGpsPermissionAllowedFromSnackbar();
}
