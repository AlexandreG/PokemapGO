package com.pokemap.markergenerator.presenter;

/**
 * This is the presenter associated to the HomeMapActivity
 */
//public class HomeMapPresenter implements HomeMapPresenterCallback, OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMapLoadedCallback {

public class HomeMapPresenter {
//public class HomeMapPresenter implements HomeMapPresenterCallback, OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMapLoadedCallback {
//    private static final String TAG = HomeMapPresenter.class.getSimpleName();
//
//    private static final int TIME_BEFORE_LAUCHING_REQUEST = 1000;
//
//    public static final int PERMISSION_GPS_AT_LAUNCH = 1;
//
//    public static final int PERMISSION_LOCATION_FROM_SNACKBAR = 2;
//
//    private GoogleMap googleMap;
//
//    private List<Pokemon> fullPkmList;
//    private List<BitmapDescriptor> fullImgList;
//
//    private HashMap<Trail, Marker> trailMarkerMap;
//
//    private int lastSelectedPkmId;
//
//    private double lastMapReleaseTime;  //TODO refact this shit
//
//    private HomeMapActivity activity;
//
//    public HomeMapPresenter(HomeMapActivity activity) {
//        this.activity = activity;
//    }
//
//    @Override
//    public void onCreate() {
//        checkGpsPermission();
//
//        new LoadFullPkmDataTask().execute(activity);
//        lastSelectedPkmId = 0;
//
//        trailMarkerMap = new HashMap<>();
//        //no need to launch anything, we continue when the map get ready
//    }
//
//    @Override
//    public void onResume() {
//        setupMapPositionButtons();
//    }
//
//    private class LoadFullPkmDataTask extends AsyncTask<Context, Void, Void> {
//        @Override
//        protected Void doInBackground(Context... contexts) {
//            Pokemon[] pokemons = PokemonHelper.loadFullPkmList(activity);
//            fullPkmList = new LinkedList<>();
//            fullImgList = new LinkedList<>();
//            Pokemon currentPokemon;
//            BitmapDescriptor currentBitmap;
//            for (int i = 0; i < pokemons.length; ++i) {
//                currentPokemon = pokemons[i];
//                fullPkmList.add(currentPokemon);
//
//                if (contexts[0] != null) {
//                    currentBitmap = BitmapDescriptorFactory.fromResource(FileHelper.getDrawableIdByName(contexts[0], "p" + currentPokemon.getId()));
//                    fullImgList.add(currentBitmap);
//                }
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            activity.initDropDownView(PokemonHelper.getDropDownViewItems(activity, fullPkmList));
//        }
//    }
//
//
//    public void checkGpsPermission() {
//        if (!PermissionHelper.isGpsAllowed(activity)) {
//            PermissionHelper.requestGpsPermission(activity, PERMISSION_GPS_AT_LAUNCH);
//        } else {
//            setupMapPositionButtons();
//        }
//    }
//
//
//    public void onMapReady(GoogleMap map) {
//        googleMap = map;
//        googleMap.getUiSettings().setRotateGesturesEnabled(false);
////        googleMap.moveCamera(CameraUpdateFactory.newLatLng(trails.get(0).getLocation().convertToMapsModel()));
//        activity.showSpinner(false);
//        googleMap.setOnCameraChangeListener(this);
//        googleMap.setOnMapLoadedCallback(this);
//    }
//
//    @Override
//    public void onCameraChange(CameraPosition cameraPosition) {
//        if (System.currentTimeMillis() - lastMapReleaseTime > TIME_BEFORE_LAUCHING_REQUEST) {
//            lastMapReleaseTime = System.currentTimeMillis();
//            fetchPkmList();
//        }
//    }
//
//    @Override
//    public void onMapLoaded() {
//        setupMapPositionButtons();
//    }
//
//    public void onPokemonSearched(String pkmName) {
//        lastSelectedPkmId = PokemonHelper.getPkmIdByName(pkmName, fullPkmList);
//        if (lastSelectedPkmId < 1 && !pkmName.equalsIgnoreCase(activity.getString(R.string.select_all_pkm))) {
//            activity.showToast(R.string.error_pokemon_not_found);
//        } else {
//            fetchPkmList();
//        }
//    }
//
//    private void setupMapPositionButtons() {
//        if (googleMap != null) {
//            boolean isGpsAllowed = PermissionHelper.isGpsAllowed(activity);
//            boolean isGpsEnabled = PermissionHelper.isGpsEnabled(activity);
//
//            googleMap.getUiSettings().setMyLocationButtonEnabled(isGpsAllowed && isGpsEnabled);
//            googleMap.setMyLocationEnabled(isGpsAllowed && isGpsEnabled);
//            googleMap.setPadding(0, HomeMapHelper.pxFromDp(activity, 80), HomeMapHelper.pxFromDp(activity, 14), 0);
//        }
//    }
//
//    private void fetchPkmList() {
//        MapArea mapArea = HomeMapHelper.calculateMapArea(googleMap.getProjection().getVisibleRegion());
//        RequestManager.getInstance().fetchPkmList(lastSelectedPkmId, mapArea, getPkmListCallback());
//    }
//
//    private void updateMarkersMap(List<Trail> receivedTrails) {
//        Set<Trail> currentTrailSet = trailMarkerMap.keySet();
//        LinkedList<Trail> trailsToRemove = new LinkedList<>(currentTrailSet);
//        HomeMapHelper.removeAllBinA(trailsToRemove, receivedTrails);
//
//        LinkedList<Trail> trailsToAdd = new LinkedList<>(receivedTrails);
//        HomeMapHelper.removeAllBinA(trailsToAdd, new LinkedList<>(currentTrailSet));
//
//        for (Trail t : trailsToRemove) {
//            trailMarkerMap.get(t).remove(); //remove from google map
//            trailMarkerMap.remove(t); //remove from data
//        }
//
//        Marker currentMarker;
//        for (Trail t : trailsToAdd) {
//            t.getPokemon().loadName(fullPkmList);
//            currentMarker = googleMap.addMarker(HomeMapHelper.getMapMarkerOption(activity, fullImgList, t));
//            trailMarkerMap.put(t, currentMarker);
//        }
//    }
//
//    @Override
//    public void onClickYesSendPkm(String selectedPkmName) {
//        if (PermissionHelper.isGpsAllowed(activity)) {
//
//            //I update the map ui again because of that bug : if we enable the gps from
//            //the status bar, we don't go to onResume and we are fucked
//            setupMapPositionButtons();
//
//            if (checkIsGpsEnabled()) {
//                SeenPkm seenPkm = generateSeenPokemon(selectedPkmName);
//                if (seenPkm != null) {
//                    RequestManager.getInstance().sendSeenPkm(seenPkm, getSendTrailCallback());
//                    activity.startTurningSendButton(true);
//                } else {
//                    Log.e(TAG, "onClickYesSendPkm failed, generateSeenPokemon null");
//                    activity.showSnackbar(R.string.error_request_post);
//                }
//            } else {
//                //case already handled by checkIsGpsEnabled()
//            }
//        } else {
//            activity.showSnackbarGpsNeeded();
//        }
//    }
//
//
//    @Override
//    public void onGpsPermissionAllowed() {
//        setupMapPositionButtons();
//    }
//
//    @Override
//    public void onGpsPermissionAllowedFromSnackbar() {
//        //cette duplication de méthode est un peu degueu mais la tout de suite j'ai pas mieux
//        onGpsPermissionAllowed();
//        checkIsGpsEnabled();
//    }
//
//    /**
//     * Return true if was enabled, if not start setting activity
//     *
//     * @return
//     */
//    private boolean checkIsGpsEnabled() {
//        if (!PermissionHelper.isGpsEnabled(activity)) {
//            activity.showSnackbarAction(R.string.permission_please_unable_gps, R.string.permission_please_unable_gps_action, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    PermissionHelper.startIntentEnableGps(activity);
//                }
//            });
//            return false;
//        }
//        return true;
//    }
//
//    private SeenPkm generateSeenPokemon(String selectedPkmName) {
//        SeenPkm t = new SeenPkm();
//
//        int pkmId = PokemonHelper.getPkmIdByName(selectedPkmName, fullPkmList);
//        if (pkmId < 1) {
//            return null;
//        }
//        t.setId(pkmId);
//
//        Location location = getUserLocation();
//        if (location == null) {
//            return null;
//        }
//        t.setLocation(location);
//
//        return t;
//    }
//
//    private Location getUserLocation() {
//        try {
//            return new Location(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude());
//        } catch (Exception e) {
//            Log.e(TAG, e.getMessage());
//            return null;
//        }
//    }
//
//
//    private Callback<List<Trail>> getPkmListCallback() {
//        return new PkmListCallback();
//    }
//
//
//    class PkmListCallback implements Callback<List<Trail>> {
//        @Override
//        public void onResponse(Call<List<Trail>> call, Response<List<Trail>> response) {
//            if (response.isSuccessful()) {
//                updateMarkersMap(response.body());
//            } else {
//                activity.showSnackbar(R.string.error_request_pokemon);
//            }
//        }
//
//        @Override
//        public void onFailure(Call<List<Trail>> call, Throwable t) {
//            //une ébauche pour relancer la requete mais pas encore au point
////                View.OnClickListener errorListener = new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        getPkmListCallback();
////                    }
////                };
////                activity.showSnackbarAction(R.string.error_request_pokemon, R.string.error_retry, errorListener);
//            activity.showSnackbar(R.string.error_request_pokemon);
//        }
//    }
//
//
//    private Callback<SeenPkm> getSendTrailCallback() {
//        return new SendTrailCallback();
//    }
//
//    class SendTrailCallback implements Callback<SeenPkm> {
//        @Override
//        public void onResponse(Call<SeenPkm> call, Response<SeenPkm> response) {
//            activity.startTurningSendButton(false);
//            activity.showToast(R.string.send_pokemon_succes);
//            fetchPkmList();
//        }
//
//        @Override
//        public void onFailure(Call<SeenPkm> call, Throwable t) {
//            Log.e(TAG, "onFailure getSendTrailCallback");
//            activity.showSnackbar(R.string.error_unexpected);
//            activity.startTurningSendButton(false);
//        }
//    }
//
//    @Override
//    public void onClickSettingsButton() {
////        Intent t = new Intent(this, SettingActivity.class);
////        startActivity(t);
////        activity.showToast("hey");
//    }
}
