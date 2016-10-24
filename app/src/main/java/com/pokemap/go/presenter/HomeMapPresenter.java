package com.pokemap.go.presenter;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.pokemap.go.R;
import com.pokemap.go.helper.FileHelper;
import com.pokemap.go.helper.HomeMapHelper;
import com.pokemap.go.helper.PermissionHelper;
import com.pokemap.go.helper.PokemonHelper;
import com.pokemap.go.helper.PrefHelper;
import com.pokemap.go.helper.TimeFilterHelper;
import com.pokemap.go.model.Location;
import com.pokemap.go.model.MapArea;
import com.pokemap.go.model.Pokemon;
import com.pokemap.go.model.Trail;
import com.pokemap.go.view.HomeMapActivity;
import com.pokemap.go.ws.RequestManager;
import com.pokemap.go.ws.SeenPkm;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This is the presenter associated to the HomeMapActivity
 */
public class HomeMapPresenter implements HomeMapPresenterCallback, OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMapLoadedCallback {
    private static final String TAG = HomeMapPresenter.class.getSimpleName();

    private static final int TIME_BEFORE_LAUNCHING_REQUEST = 1000;

    private static final int GOOGLE_MAP_MIN_ZOOM = 5;

    public static final int PERMISSION_GPS_AT_LAUNCH = 1;

    public static final int PERMISSION_LOCATION_FROM_SNACKBAR = 2;

    private GoogleMap googleMap;

    private List<Pokemon> fullPkmList;
    private Map<Integer, BitmapDescriptor> pkmImgMap;

    private HashMap<Trail, Marker> trailMarkerMap;

    private double lastMapReleaseTime;
    private boolean isLastMapListRequestCameBack;

    private HomeMapActivity activity;

    public HomeMapPresenter(HomeMapActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate() {
        checkPlayServiceAvailable();
        checkGpsPermission();

        new LoadFullPkmDataTask().execute();

        trailMarkerMap = new HashMap<>();
        isLastMapListRequestCameBack = true;
        //no need to launch anything, we continue when the map get ready
    }

    private void checkPlayServiceAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity.getApplicationContext());
        if (status != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(status, activity, status);
        }
    }

    @Override
    public void onResume() {
        setupMapPositionButtons();
    }

    private class LoadFullPkmDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... contexts) {
            Pokemon[] pokemons = PokemonHelper.loadFullPkmList(activity);
            fullPkmList = new LinkedList<>();
            pkmImgMap = new HashMap<>();
            Pokemon currentPokemon;
            BitmapDescriptor bitmapDescriptor;
            for (int i = 0; i < pokemons.length; ++i) {
                try {
                    currentPokemon = pokemons[i];
                    fullPkmList.add(currentPokemon);

                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(FileHelper.getDrawableIdByName(activity, "p" + Integer.toString(i + 1)));
                    pkmImgMap.put(i + 1, bitmapDescriptor);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    Crashlytics.logException(e);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            activity.initDropDownView(PokemonHelper.getDropDownViewItems(activity, fullPkmList));
            activity.initTimeFilterView();
            //TODO : appeler une seule méthode d'init globale ?
        }
    }

    public void checkGpsPermission() {
        if (!PermissionHelper.isGpsAllowed(activity)) {
            PermissionHelper.requestGpsPermission(activity, PERMISSION_GPS_AT_LAUNCH);
        } else {
            setupMapPositionButtons();
        }
    }


    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        activity.showSpinner(false);
        googleMap.setOnCameraChangeListener(this);
        googleMap.setOnMapLoadedCallback(this);
        initMapInititalPos();
    }

    public void initMapInititalPos() {
        Geocoder geocoder = new Geocoder(activity);
        List<Address> addresses;
        String userCountry = HomeMapHelper.getDeviceCountryName(activity);
        try {
            addresses = geocoder.getFromLocationName(userCountry, 1);
            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to move to user country " + userCountry);
        }
    }

    public void saveUserPos(double lat, double lng) {
        PrefHelper.saveUserLat(activity, lat);
        PrefHelper.saveUserLat(activity, lng);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (cameraPosition.zoom < GOOGLE_MAP_MIN_ZOOM) {
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(GOOGLE_MAP_MIN_ZOOM));
        } else {
            if (isLastMapListRequestCameBack && (System.currentTimeMillis() - lastMapReleaseTime > TIME_BEFORE_LAUNCHING_REQUEST)) {
                lastMapReleaseTime = System.currentTimeMillis();
                checkInputAndFetchPkmList();
            }
        }
    }

    @Override
    public void onMapLoaded() {
        setupMapPositionButtons();
    }

    public void onPokemonSearched(String pkmName, int timeFilter) {
        checkInputAndFetchPkmList();
    }

    private void setupMapPositionButtons() {
        if (googleMap != null) {
            boolean isGpsAllowed = PermissionHelper.isGpsAllowed(activity);
            boolean isGpsEnabled = PermissionHelper.isGpsEnabled(activity);

            googleMap.getUiSettings().setMyLocationButtonEnabled(isGpsAllowed && isGpsEnabled);
            googleMap.setMyLocationEnabled(isGpsAllowed && isGpsEnabled);
            googleMap.setPadding(0, HomeMapHelper.pxFromDp(activity, 80), HomeMapHelper.pxFromDp(activity, 14), 0);
        }
    }

    private void checkInputAndFetchPkmList() {
        if (!activity.isInputValidForSearch()) {
            activity.showToast(R.string.error_pokemon_not_found);
        } else {
            int pkmId = PokemonHelper.getPkmIdByName(activity.getSelectedPokemon(), fullPkmList);
            fetchPkmList(pkmId);
        }
    }


    private void fetchPkmList(int selectedPkm) {
        MapArea mapArea = HomeMapHelper.calculateMapArea(googleMap.getProjection().getVisibleRegion());
        int filterServerId = TimeFilterHelper.getServerFilterCodeFromPosition(activity.getCurrentTimeFilter());
        RequestManager.getInstance().fetchPkmList(selectedPkm, mapArea, filterServerId, getPkmListCallback());
    }

    private void updateMarkersMap(List<Trail> receivedTrails) {
        Set<Trail> currentTrailSet = trailMarkerMap.keySet();
        LinkedList<Trail> trailsToRemove = new LinkedList<>(currentTrailSet);
        HomeMapHelper.removeAllBinA(trailsToRemove, receivedTrails);

        final LinkedList<Trail> trailsToAdd = new LinkedList<>(receivedTrails);
        HomeMapHelper.removeAllBinA(trailsToAdd, new LinkedList<>(currentTrailSet));

        for (Trail t : trailsToRemove) {
            trailMarkerMap.get(t).remove(); //remove from google map
            trailMarkerMap.remove(t); //remove from data
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Marker currentMarker;
                for (Trail t : trailsToAdd) {
                    t.getPokemon().loadName(fullPkmList);
                    currentMarker = googleMap.addMarker(HomeMapHelper.getMapMarkerOption(activity, pkmImgMap, t));
                    trailMarkerMap.put(t, currentMarker);
                }
            }
        });
    }

    @Override
    public void onClickYesSendPkm(String selectedPkmName) {
        if (PermissionHelper.isGpsAllowed(activity)) {

            //I update the map ui again because of that bug : if we enable the gps from
            //the status bar, we don't go to onResume and we are fucked
            setupMapPositionButtons();

            if (checkIsGpsEnabled()) {
                SeenPkm seenPkm = generateSeenPokemon(selectedPkmName);
                if (seenPkm != null) {
                    RequestManager.getInstance().sendSeenPkm(seenPkm, getSendTrailCallback());
                    activity.startTurningSendButton(true);
                } else {
                    Log.e(TAG, "onClickYesSendPkm failed, generateSeenPokemon null");
                    activity.showSnackbar(R.string.error_request_post);
                }
            } else {
                //case already handled by checkIsGpsEnabled()
            }
        } else {
            activity.showSnackbarGpsNeeded();
        }
    }


    @Override
    public void onGpsPermissionAllowed() {
        setupMapPositionButtons();
    }

    @Override
    public void onGpsPermissionAllowedFromSnackbar() {
        //cette duplication de méthode est un peu degueu mais la tout de suite j'ai pas mieux
        onGpsPermissionAllowed();
        checkIsGpsEnabled();
    }

    /**
     * Return true if was enabled, if not start setting activity
     *
     * @return
     */
    private boolean checkIsGpsEnabled() {
        if (!PermissionHelper.isGpsEnabled(activity)) {
            activity.showSnackbarAction(R.string.permission_please_unable_gps, R.string.permission_please_unable_gps_action, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PermissionHelper.startIntentEnableGps(activity);
                }
            });
            return false;
        }
        return true;
    }

    private SeenPkm generateSeenPokemon(String selectedPkmName) {
        SeenPkm t = new SeenPkm();

        int pkmId = PokemonHelper.getPkmIdByName(selectedPkmName, fullPkmList);
        if (pkmId < 1) {
            return null;
        }
        t.setId(pkmId);

        Location location = getUserLocation();
        if (location == null) {
            return null;
        }
        t.setLocation(location);

        return t;
    }

    private Location getUserLocation() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (googleMap.getMyLocation().isFromMockProvider()) {
                    return null;
                }
            }
            Location location = new Location(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude());
            return location;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    private Callback<List<Trail>> getPkmListCallback() {
        isLastMapListRequestCameBack = false;
        return new PkmListCallback();
    }


    class PkmListCallback implements Callback<List<Trail>> {
        @Override
        public void onResponse(Call<List<Trail>> call, final Response<List<Trail>> response) {
            isLastMapListRequestCameBack = true;
            if (response.isSuccessful()) {
//                Thread t = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        updateMarkersMap(response.body());
//                    }
//                });
//                t.run();
                updateMarkersMap(response.body());

            } else {
                activity.showSnackbar(R.string.error_request_pokemon);
            }
        }

        @Override
        public void onFailure(Call<List<Trail>> call, Throwable t) {
            isLastMapListRequestCameBack = true;
            //une ébauche pour relancer la requete mais pas encore au point
//                View.OnClickListener errorListener = new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        getPkmListCallback();
//                    }
//                };
//                activity.showSnackbarAction(R.string.error_request_pokemon, R.string.error_retry, errorListener);
            activity.showSnackbar(R.string.error_request_pokemon);
        }
    }


    private Callback<SeenPkm> getSendTrailCallback() {
        return new SendTrailCallback();
    }

    class SendTrailCallback implements Callback<SeenPkm> {
        @Override
        public void onResponse(Call<SeenPkm> call, Response<SeenPkm> response) {
            activity.startTurningSendButton(false);
            activity.showToast(R.string.send_pokemon_succes);
            checkInputAndFetchPkmList();
        }

        @Override
        public void onFailure(Call<SeenPkm> call, Throwable t) {
            Log.e(TAG, "onFailure getSendTrailCallback");
            activity.showSnackbar(R.string.error_unexpected);
            activity.startTurningSendButton(false);
        }
    }

    @Override
    public void onClickSettingsButton() {
//        Intent t = new Intent(this, SettingActivity.class);
//        startActivity(t);
//        activity.showToast("hey");
    }
}
