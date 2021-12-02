package com.devpilot.weatherkok.intro;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class DialogView {
    private static final String TAG = DialogView.class.getSimpleName();

    public static ProgressDialog mMProgressDialog;
    public static String LOADING_MESSAGE = "Loading...";


    public static Dialog getDefaultDialog(Context context, View view) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        return dialog;
    }


    public static void showLoadingProgressDialog(final Activity activity) {
        //activity 체크
        if(activity.isFinishing()) {
            dismissDialog();
        } else {
            if (mMProgressDialog != null) {
                if (!mMProgressDialog.isShowing()) {
                    mMProgressDialog = ProgressDialog.show(activity, null, LOADING_MESSAGE, false, true);
                }
            } else {
                mMProgressDialog = ProgressDialog.show(activity, null, LOADING_MESSAGE, false, true);
            }
        }
    }

    public static void showProgressDialog(final Activity activity) {
        //activity 체크
        if(activity.isFinishing()) {
            dismissDialog();
        } else {
            if (mMProgressDialog != null) {
                if (!mMProgressDialog.isShowing()) {
                    mMProgressDialog = ProgressDialog.show(activity, null, LOADING_MESSAGE, false, false);
                }
            } else {
                mMProgressDialog = ProgressDialog.show(activity, null, LOADING_MESSAGE, false, false);
            }
        }
    }

    public static void dismissDialog() {
        if (mMProgressDialog != null) {
            if (mMProgressDialog.isShowing()) {
                try {
                    mMProgressDialog.dismiss();
                }catch (Exception e) {

                }
            }
        }
    }

}
