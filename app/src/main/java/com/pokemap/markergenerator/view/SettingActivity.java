package com.pokemap.markergenerator.view;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;

import com.pokemap.markergenerator.R;

/**
 * Created by AG on 14/07/2016.
 */
public class SettingActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_settings);
    }
}
