package com.pokemap.markergenerator.ws;

import com.pokemap.markergenerator.model.Trail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by AG on 14/07/2016.
 */
public interface PokeService {

    @GET("pokemon/around/{latMin}/{latMax}/{lgnMin}/{lgnMax}/{id}")
    Call<List<Trail>> listClosePkm(
            @Path("latMin") double latMin,
            @Path("latMax") double latMax,
            @Path("lgnMin") double lgnMin,
            @Path("lgnMax") double lgnMax,
            @Path("id") int pokeId
    );

    @GET("pokemon/around/{latMin}/{latMax}/{lgnMin}/{lgnMax}")
    Call<List<Trail>> listAllClosePkm(
            @Path("latMin") double latMin,
            @Path("latMax") double latMax,
            @Path("lgnMin") double lgnMin,
            @Path("lgnMax") double lgnMax
    );

    @POST("pokemon/")
    Call<SeenPkm> sendSeenPokemon(@Body SeenPkm seenPkm);
}
