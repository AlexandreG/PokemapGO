package com.pokemap.go.ws;

import com.pokemap.go.BuildConfig;
import com.pokemap.go.model.MapArea;
import com.pokemap.go.model.Trail;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AG on 16/07/2016.
 */
public class RequestManager {
    private static RequestManager INSTANCE = null;

    public static RequestManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RequestManager();
        }
        return INSTANCE;
    }

    //TODO: add your own server adress
    private static final String ENDPOINT_DEBUG = "";
    private static final String ENDPOINT_RELEASE = "";

    private static String getEndpoint() {
        if (BuildConfig.DEBUG) {
            return ENDPOINT_DEBUG;
        } else {
            return ENDPOINT_RELEASE;
        }
    }

    private Retrofit retrofit;

    private PokeService pokeService;

    private RequestManager() {
        retrofit = new Retrofit.Builder()
                .baseUrl(getEndpoint())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        pokeService = retrofit.create(PokeService.class);
    }

    public void fetchPkmList(int pkmId, MapArea mapArea, int filterServerId, Callback<List<Trail>> callback) {
        Call<List<Trail>> call;

        if (pkmId < 1) {
            if (BuildConfig.DEBUG) {
                //filter not enabled on the debug version
                call = pokeService.listAllClosePkm(mapArea.latMin, mapArea.latMax, mapArea.lngMin, mapArea.lngMax);
            } else {
                call = pokeService.listAllClosePkmWithFilter(mapArea.latMin, mapArea.latMax, mapArea.lngMin, mapArea.lngMax, filterServerId);
            }
        } else {
            if (BuildConfig.DEBUG) {
                //filter not enabled on the debug version
                call = pokeService.listClosePkm(mapArea.latMin, mapArea.latMax, mapArea.lngMin, mapArea.lngMax, pkmId);
            } else {
                call = pokeService.listClosePkmWithFilter(mapArea.latMin, mapArea.latMax, mapArea.lngMin, mapArea.lngMax, pkmId, filterServerId);
            }
        }
        //asynchronous call
        call.enqueue(callback);
    }

    public void sendSeenPkm(SeenPkm seenPkm, Callback<SeenPkm> callback) {
        Call<SeenPkm> call = pokeService.sendSeenPokemon(seenPkm);
        call.enqueue(callback);
    }
}
