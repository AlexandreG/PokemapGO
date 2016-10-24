package com.pokemap.go.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.SupportMapFragment;
import com.pokemap.go.R;
import com.pokemap.go.helper.PermissionHelper;
import com.pokemap.go.helper.PokemonHelper;
import com.pokemap.go.presenter.HomeMapPresenter;
import com.pokemap.go.view.dialog.MockGpsDialogFragment;
import com.pokemap.go.view.dialog.SendPokemonDialogFragment;
import com.pokemap.go.view.ui.PkmDropdownLayout;
import com.pokemap.go.view.ui.TimeFilterLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

/**
 * The main view of the app. It displays a map of pokemon. This is basically just the view.
 * I tried to respect a MVP pattern so this activity holds a persenter and they both
 * communicate with their callbacks.
 */
public class HomeMapActivity extends FragmentActivity implements HomeMapViewCallback, PkmDropdownLayout.Callback, TimeFilterLayout.Callback {

    private static final String TAG = HomeMapActivity.class.getSimpleName();

    @BindView(R.id.home_coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.home_spinner)
    View loaderView;

    @BindView(R.id.home_pkm_list)
    PkmDropdownLayout pkmDropdownLayout;

    @BindView(R.id.home_content)
    ViewGroup wrapperView;

    @BindView(R.id.home_send_pkm_btn)
    FloatingActionButton sendPkmBtn;

    @BindView(R.id.home_time_filter)
    TimeFilterLayout timeFilterLayout;

    private SupportMapFragment mapFragment;

    private HomeMapPresenter presenter;

    private AnimatorSet sendAnimatorSet;
    private ObjectAnimator rotateAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        showSpinner(true);

        presenter = new HomeMapPresenter(this);
        presenter.onCreate();

        setupMapView();

        sendAnimatorSet = new AnimatorSet();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();

        if (PermissionHelper.isMockSettingsON(this)) {
            MockGpsDialogFragment newFragment = MockGpsDialogFragment.newInstance();
            newFragment.show(getSupportFragmentManager(), "dialog");
        }
    }

    private void setupMapView() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.home_map);
        mapFragment.getMapAsync(presenter);
    }

    @OnClick(R.id.home_send_pkm_btn)
    protected void onClickAddTrail() {
        //c'est ptet plus au presenter de gérer ce comportement
        //mais cest que de la vue donc je laisse la pour le moment
        if (!pkmDropdownLayout.isInputValidForSend()) {
            //snackbar not usable here because the keyboard is probably openned
            showToast(R.string.error_select_pkm);
        } else {
            pkmDropdownLayout.hideKeyboard();
            showSendPkmDialog(getSelectedPokemon());
        }
    }

    @Override
    public void initDropDownView(String[] listPkmChoice) {
        pkmDropdownLayout.setup(this, listPkmChoice);
    }

    @Override
    public void initTimeFilterView() {
        timeFilterLayout.setup(this);
    }

    public void showSendPkmDialog(String pokemonName) {
        DialogFragment newFragment = SendPokemonDialogFragment.newInstance(pokemonName);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    public void onClickYesSendPkm(String pokemonName) {
        presenter.onClickYesSendPkm(pokemonName);
    }

    public void startTurningSendButton(boolean b) {
        if (b) {
            rotateAnimator = PokemonHelper.getRotationAnimator(sendPkmBtn);
            sendAnimatorSet.play(rotateAnimator);
            sendAnimatorSet.start();
        } else {
            rotateAnimator.setRepeatCount(0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case HomeMapPresenter.PERMISSION_GPS_AT_LAUNCH:
                if (PermissionHelper.isResultOk(grantResults)) {
                    presenter.onGpsPermissionAllowed();
                } else {
                    //Gps blocked at launch (it's ok, map buttons already disabled)
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // no idea why, in this case we don't move properly whithout delay
                        presenter.initMapInititalPos();
                    }
                }, 200);

                break;
            case HomeMapPresenter.PERMISSION_LOCATION_FROM_SNACKBAR:
                if (PermissionHelper.isResultOk(grantResults)) {
                    presenter.onGpsPermissionAllowedFromSnackbar();
                } else {
                    showSnackbarGpsNeeded();
                }
                break;
        }
    }

    public void showSnackbarGpsNeeded() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, getString(R.string.permission_please_allow_gps), Snackbar.LENGTH_LONG);

        boolean wasRefused = PermissionHelper.isGpsAllowedAlreadyRefused(this);

        if (!wasRefused) {
            snackbar.setAction(getString(R.string.permission_please_allow_gps_action), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PermissionHelper.requestGpsPermission(HomeMapActivity.this, HomeMapPresenter.PERMISSION_LOCATION_FROM_SNACKBAR);
                }
            });
        } else {
            snackbar.setAction(getString(R.string.permission_please_allow_gps_action), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PermissionHelper.startIntentAllowGps(HomeMapActivity.this);
                }
            });
        }
        snackbar.show();
    }

    @Override
    public void onSelectedPkmInList(String pkmName) {
        presenter.onPokemonSearched(pkmName, getCurrentTimeFilter());
    }

    @Override
    public void onSelectedTimeFilter(int timeFilter) {
        presenter.onPokemonSearched(getSelectedPokemon(), timeFilter);
    }

    public void showToast(int stringId) {
        Toast.makeText(getApplicationContext(), getString(stringId), Toast.LENGTH_SHORT).show();
    }

    public void showSpinner(boolean isVisible) {
        if (isVisible) {
            loaderView.setVisibility(View.VISIBLE);
            wrapperView.setVisibility(View.INVISIBLE);
        } else {
            loaderView.setVisibility(View.GONE);
            wrapperView.setVisibility(View.VISIBLE);
        }
    }

    public void showSnackbar(int stringId) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, getString(stringId), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void showSnackbarAction(int stringId, int actionId, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, getString(stringId), Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(actionId), listener);
        snackbar.show();
    }

    public String getSelectedPokemon() {
        return pkmDropdownLayout.getSelectedPokemon();
    }

    public int getCurrentTimeFilter() {
        return timeFilterLayout.getCurrentTimeFilter();
    }


    public boolean isInputValidForSend() {
        return pkmDropdownLayout.isInputValidForSend();
    }

    public boolean isInputValidForSearch() {
        return pkmDropdownLayout.isInputValidForSearch();
    }
}
