package com.pokemap.markergenerator.model;

import com.pokemap.markergenerator.helper.PokemonHelper;

import java.util.List;

/**
 * Created by AG on 14/07/2016.
 */
public class Pokemon {

    private int id;
    private String name;
    private int type1;
    private int type2;

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

    public int getType1() {
        return type1;
    }

    public void setType1(int type1) {
        this.type1 = type1;
    }

    public int getType2() {
        return type2;
    }

    public void setType2(int type2) {
        this.type2 = type2;
    }
}
