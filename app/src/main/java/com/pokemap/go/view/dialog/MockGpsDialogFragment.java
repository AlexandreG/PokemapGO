package com.pokemap.go.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.pokemap.go.R;

/**
 * Created by AG on 16/07/2016.
 */
public class MockGpsDialogFragment extends DialogFragment {


    public static MockGpsDialogFragment newInstance() {
        MockGpsDialogFragment fragment = new MockGpsDialogFragment();
//        Bundle args = new Bundle();
//        args.putString(KEY_PKM_NAME, pkmName);
//        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.dialog_mock_gps_title));
        builder.setMessage(getString(R.string.dialog_mock_gps_msg));
        builder.setPositiveButton(R.string.dialog_mock_gps_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getActivity().finish();
            }
        });
        return builder.create();
    }
}
