package com.pokemap.go.model;

import com.pokemap.go.helper.PokemonHelper;

import java.util.List;

/**
 * Created by AG on 14/07/2016.
 */
public class Pokemon {

    private int id;
    private String name;

    public Pokemon(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void loadName( List<Pokemon> fullPkmList){
        //the ws doesn't fill the name so lets do it
        name = PokemonHelper.getPkmNameById(id, fullPkmList);
    }
}
