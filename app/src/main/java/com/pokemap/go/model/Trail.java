package com.pokemap.go.model;

/**
 * Created by AG on 14/07/2016.
 */
public class Trail {
    private Pokemon pokemon;
    private long time;
    private Location location;

    public Pokemon getPokemon() {
        return pokemon;
    }

    public long getTime() {
        return time;
    }

    public Location getLocation() {
        return location;
    }

    public void setPokemon(Pokemon pokemon) {
        this.pokemon = pokemon;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Trail)) {
            return false;
        }
        Trail toCompare = (Trail) o;

        if (getPokemon().getId() != toCompare.getPokemon().getId()) {
            return false;
        }

        if (time != toCompare.getTime()) {
            return false;
        }

        if (Double.compare(getLocation().getLat(), toCompare.getLocation().getLat()) != 0) {
            return false;
        }

        if (Double.compare(getLocation().getLng(), toCompare.getLocation().getLng()) != 0) {
            return false;
        }

        return true;
    }
}
