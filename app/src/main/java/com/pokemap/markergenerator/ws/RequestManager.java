package com.pokemap.markergenerator.ws;

import com.pokemap.markergenerator.BuildConfig;
import com.pokemap.markergenerator.model.MapArea;
import com.pokemap.markergenerator.model.Trail;

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

    public void fetchPkmList(int pkmId, MapArea mapArea, Callback<List<Trail>> callback) {
        Call<List<Trail>> call;

        if (pkmId < 1) {
            call = pokeService.listAllClosePkm(mapArea.latMin, mapArea.latMax, mapArea.lngMin, mapArea.lngMax);
        } else {
            call = pokeService.listClosePkm(mapArea.latMin, mapArea.latMax, mapArea.lngMin, mapArea.lngMax, pkmId);
        }
        //asynchronous call
        call.enqueue(callback);
    }

    public void sendSeenPkm(SeenPkm seenPkm, Callback<SeenPkm> callback) {
        Call<SeenPkm> call = pokeService.sendSeenPokemon(seenPkm);
        call.enqueue(callback);
    }
}
