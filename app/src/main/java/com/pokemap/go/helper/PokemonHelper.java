package com.pokemap.go.helper;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.gson.Gson;
import com.pokemap.go.R;
import com.pokemap.go.model.Pokemon;

import java.util.LinkedList;
import java.util.List;

/**
 * All pokemon related static methods
 */
public class PokemonHelper {

    private static final String TAG = PokemonHelper.class.getSimpleName();

    /**
     * Load the complete list of pokemons from the assets
     *
     * @param context
     * @return an array of all pokemons
     */
    public static Pokemon[] loadFullPkmList(Context context) {
        String assetName = context.getString(R.string.asset_pkm_list);

        String jsonData = FileHelper.getAsset(context, assetName);
        Gson gson = new Gson();

        return gson.fromJson(jsonData, Pokemon[].class);
    }

    public static int getPkmIdByName(String name, List<Pokemon> pokemons) {
        for (Pokemon p : pokemons) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p.getId();
            }
        }
        return -1;
    }

    public static String getPkmNameById(int id, List<Pokemon> pokemons) {
        for (Pokemon p : pokemons) {
            if (p.getId() == id) {
                return p.getName();
            }
        }
        return "";
    }

    public static ObjectAnimator getRotationAnimator(View v) {
        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(v, "rotation", 0, 1080);
        rotationAnim.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnim.setDuration(1300);
        rotationAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        return rotationAnim;
    }

    public static String[] getDropDownViewItems(Context context, List<Pokemon> pkmDataRef) {
        String[] listPkmChoice = new String[pkmDataRef.size() + 1];

        for (int i = 0; i < listPkmChoice.length; ++i) {
            if (i == 0) {
                listPkmChoice[0] = context.getString(R.string.select_all_pkm);
            } else {
//                listPkmChoice[i] = "#" + Integer.toString(i + 1) + " " + pkmDataRef.get(i - 1).getName();
                listPkmChoice[i] = pkmDataRef.get(i - 1).getName();
            }
        }
        return listPkmChoice;
    }
}
