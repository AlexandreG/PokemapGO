package com.pokemap.markergenerator.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pokemap.markergenerator.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A view that displays a list of pokemon and allows to search in
 * (in fact it is a viewgroup because I need a dummy view to remove the focus)
 */
public class PkmDropdownLayout extends LinearLayout {

    public interface Callback {
        void onSelectedPkmInList(String pkmName);
    }

    @BindView(R.id.layout_pokemon_list_dropdown)
    AutoCompleteTextView dropDownView;

    @BindView(R.id.layout_pokemon_list_dummy_focus_handler)
    EditText dummyFocusView;

    private String[] dropDownViewItems;

    Callback callback;

    public PkmDropdownLayout(Context context) {
        super(context);
        init();
    }

    public PkmDropdownLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PkmDropdownLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PkmDropdownLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View v = inflate(getContext(), R.layout.layout_pokemon_list, this);
        ButterKnife.bind(this, v);
    }

    public void setup(Callback cb, final String[] items) {
        callback = cb;
        dropDownViewItems = items;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, dropDownViewItems);

        dropDownView.setAdapter(adapter);
        dropDownView.setText(dropDownViewItems[0]);
        dropDownView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        looseSearchFocus();

        dropDownView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handleEmptyDropDownCase(dropDownViewItems[0]);
                    looseSearchFocus();
                    callback.onSelectedPkmInList(getSelectedPokemon());
                    return true;
                }
                return false;
            }
        });

        dropDownView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                looseSearchFocus();
                callback.onSelectedPkmInList(getSelectedPokemon());

            }
        });
    }

    public void looseSearchFocus() {
        clearFocus();
        dummyFocusView.requestFocus();
        hideKeyboard();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(dropDownView.getWindowToken(), 0);
    }

    private void handleEmptyDropDownCase(String defaultText) {
        if (TextUtils.isEmpty(getSelectedPokemon())) {
            dropDownView.setText(defaultText);
        }
    }

    public String getSelectedPokemon() {
        return dropDownView.getText().toString();
    }

    public boolean isAllPkmSelected() {
        return getContext().getString(R.string.select_all_pkm).equals(getSelectedPokemon());
    }

    public boolean isInputValid() {
        String currentPkm = getSelectedPokemon();
        for (String s : dropDownViewItems) {
            if (currentPkm.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
}
