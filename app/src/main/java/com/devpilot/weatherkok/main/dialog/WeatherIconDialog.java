package com.devpilot.weatherkok.main.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.devpilot.weatherkok.R;

public class WeatherIconDialog extends Dialog {
    private static final String TAG = WeatherIconDialog.class.getSimpleName();

    Context mContext;
    ImageView mClose;

    public WeatherIconDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_weathericon_info);
        mClose = findViewById(R.id.iv_close);

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

}
