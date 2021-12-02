package com.devpilot.weatherkok.main.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.devpilot.weatherkok.R;

public class InputErrorDialog extends Dialog {
    private static final String TAG = InputErrorDialog.class.getSimpleName();

    Context mContext;
    ImageView mClose;

    public InputErrorDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_error);

        mClose = findViewById(R.id.iv_back_arrow);

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
