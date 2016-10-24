package com.pokemap.go.view.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.pokemap.go.R;
import com.pokemap.go.helper.TimeFilterHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This view allows to set a time with spinner
 */
public class TimeFilterLayout extends LinearLayout {

    public interface Callback {
        void onSelectedTimeFilter(int timeFilter);
    }

    @BindView(R.id.layout_time_filter_filter)
    Spinner timeFilter;

    @BindView(R.id.layout_time_filter_clock)
    ImageView timeClock;

    private boolean isTimeFilterInit;//when we put the adapter, onclick is called but i don't want


    public TimeFilterLayout(Context context) {
        super(context);
        init();
    }

    public TimeFilterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeFilterLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimeFilterLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        View v = inflate(getContext(), R.layout.layout_time_filter, this);
        ButterKnife.bind(this, v);

        isTimeFilterInit = false;
    }


    public void setup(final Callback callback) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.time_filter_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeFilter.setAdapter(adapter);
        timeFilter.setSelection(TimeFilterHelper.DEFAULT_SELECTED_ITEM_POS);

        timeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isTimeFilterInit) {
                    callback.onSelectedTimeFilter(getCurrentTimeFilter());
                } else {
                    isTimeFilterInit = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        timeClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeFilter.performClick();
            }
        });
    }

    public int getCurrentTimeFilter() {
        return timeFilter.getSelectedItemPosition();
    }
}
