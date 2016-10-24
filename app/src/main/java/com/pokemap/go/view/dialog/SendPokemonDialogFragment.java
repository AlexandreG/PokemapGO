package com.pokemap.go.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.pokemap.go.R;
import com.pokemap.go.view.HomeMapActivity;

/**
 * Created by AG on 16/07/2016.
 */
public class SendPokemonDialogFragment extends DialogFragment {
    private static final String KEY_PKM_NAME = "KEY_PKM_NAME";

    private String pokemonName;

    public static SendPokemonDialogFragment newInstance(String pkmName) {
        SendPokemonDialogFragment fragment = new SendPokemonDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_PKM_NAME, pkmName);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pokemonName = getArguments().getString(KEY_PKM_NAME);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.send_dialog_title));
        builder.setMessage(getString(R.string.send_dialog_content, pokemonName));
        builder.setPositiveButton(R.string.send_dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ((HomeMapActivity) getActivity()).onClickYesSendPkm(pokemonName);
            }
        });
        builder.setNegativeButton(R.string.send_dialog_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //nothing to do
            }
        });

        return builder.create();
    }
}
