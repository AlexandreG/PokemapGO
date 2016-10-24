package com.pokemap.markergenerator.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pokemap.markergenerator.R;
import com.pokemap.markergenerator.helper.FileHelper;
import com.pokemap.markergenerator.helper.MarkerHelper;
import com.pokemap.markergenerator.helper.PokemonHelper;
import com.pokemap.markergenerator.model.Pokemon;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;

/**
 * The main view of the app. It displays a map of pokemon. This is basically just the view.
 * I tried to respect a MVP pattern so this activity holds a persenter and they both
 * communicate with their callbacks.
 */
public class HomeMapActivity extends FragmentActivity {

    private static final String TAG = HomeMapActivity.class.getSimpleName();

    public static final int NB_TYPES = 18;
    public static final int TARGET_WIDTH = 40;
    public static final int TARGET_HEIGHT = 64;

    @BindView(R.id.fillme)
    ImageView imageView;

    List<Bitmap> leftMarkers;
    List<Bitmap> rightMarkers;
    Bitmap border;

    List<Pokemon> pokemons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);


        new Thread(new Runnable() {
            @Override
            public void run() {
                // Do network action in this function
                startTheShit();
            }
        }).start();
    }

    private void startTheShit() {
        loadPokemonNames();

        //Pour chercher sur pokeapi les types puis les saves :
//        loadPokemonTypes();
//        savePokemonTypes();

        loadMarkers();
        drawAndSave();

        Log.e(TAG, "sucesssssss");
    }

    private void savePokemonTypes() {
        Gson gson = new Gson();

        String result = gson.toJson(pokemons);
        FileHelper.writeToFile("pokemonsWithType", result);
    }

    private void loadPokemonTypes() {
        PokeApi pokeApi = new PokeApiClient();

        int nbPkmToFetch = 151;
        Pokemon currentPkm;
        int pkmType = 0;
        for (int i = 1; i <= nbPkmToFetch; ++i) {
            me.sargunvohra.lib.pokekotlin.model.Pokemon pkm = pokeApi.getPokemon(i);

            currentPkm = PokemonHelper.getPkmById(i, pokemons);

            pkmType = pkm.getTypes().get(0).getType().getId();
            currentPkm.setType1(pkmType);

            pkmType = 0;
            if (pkm.getTypes().size() > 1) {
                pkmType = pkm.getTypes().get(1).getType().getId();
            }
            currentPkm.setType2(pkmType);
        }

//        System.out.println(bulbasaur);
//        Log.w("TAG", bulbasaur.getTypes().toString());
    }

    private void loadPokemonNames() {
        pokemons = Arrays.asList(PokemonHelper.loadFullPkmList(this));
    }

    private void loadMarkers() {
        Bitmap icon;
        Bitmap resized;

        leftMarkers = new LinkedList<>();
        for (int i = 1; i <= NB_TYPES; ++i) {
            icon = BitmapFactory.decodeResource(this.getResources(),
                    FileHelper.getDrawableIdByName(this, "left" + i));
            resized = Bitmap.createScaledBitmap(icon, TARGET_WIDTH, TARGET_HEIGHT, true);
            icon.recycle();
            icon = null;
            leftMarkers.add(resized);
        }

        rightMarkers = new LinkedList<>();
        for (int i = 1; i <= NB_TYPES; ++i) {
            icon = BitmapFactory.decodeResource(this.getResources(),
                    FileHelper.getDrawableIdByName(this, "right" + i));
            resized = Bitmap.createScaledBitmap(icon, TARGET_WIDTH, TARGET_HEIGHT, true);
            icon.recycle();
            icon = null;
            rightMarkers.add(resized);
        }

        icon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.border);
        border = Bitmap.createScaledBitmap(icon, TARGET_WIDTH, TARGET_HEIGHT, true);
        icon.recycle();
        icon = null;
    }

    private void drawAndSave() {
        Bitmap bitmap = Bitmap.createBitmap(border.getWidth(), border.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        int nbPkmToDraw = 151;

        Pokemon currentPkm;
        for (int i = 1; i <= nbPkmToDraw; ++i) {    //boucle sur l'id
            currentPkm = PokemonHelper.getPkmById(i, pokemons);
            drawMarker(canvas, paint, currentPkm);
            drawName(canvas, MarkerHelper.formatPokeName(currentPkm.getName()));
            FileHelper.savebitmap(bitmap, "p" + i);
        }
    }

    private void drawMarker(Canvas canvas, Paint paint, Pokemon pkm) {
        canvas.drawBitmap(border, 0, 0, paint);
        canvas.drawBitmap(rightMarkers.get(pkm.getType1() - 1), 0, 0, paint);
        if (pkm.getType2() != 0) {
            canvas.drawBitmap(leftMarkers.get(pkm.getType2() - 1), 0, 0, paint);
        } else {
            canvas.drawBitmap(leftMarkers.get(pkm.getType1() - 1), 0, 0, paint);
        }
    }


    private void drawName(Canvas canvas, String name) {
        TextView view = new TextView(this);
        view.setText(name);
//        view.setLineSpacing(1, TARGET_HEIGHT * 0.0055f);       //version 120px
        view.setLineSpacing(1, TARGET_HEIGHT * 0.0105f);       //version 64px
        view.setTextSize(TARGET_WIDTH * 0.115f);
        view.setTextColor(Color.BLACK);
        view.setGravity(Gravity.CENTER);

        //Set a Rect for the 200 x 200 px center of a 400 x 400 px area
        Rect rect = new Rect();
        rect.set(0, 0, border.getWidth(), border.getHeight());

//
//        //Measure the view at the exact dimensions (otherwise the text won't center correctly)
//        int widthSpec = View.MeasureSpec.makeMeasureSpec(rect.width(), View.MeasureSpec.EXACTLY);
//        int heightSpec = View.MeasureSpec.makeMeasureSpec(rect.height(), View.MeasureSpec.EXACTLY);
//        view.measure(widthSpec, heightSpec);

        //Lay the view out at the rect width and height
        view.layout(0, 0, rect.width(), rect.height());

        //Translate the Canvas into position and drawAndSave it
        view.draw(canvas);
    }

    private void loadOnView(final Bitmap bitmap) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Your code to run in GUI thread here
                imageView.setImageBitmap(bitmap);

            }//public void run() {
        });
    }
}
