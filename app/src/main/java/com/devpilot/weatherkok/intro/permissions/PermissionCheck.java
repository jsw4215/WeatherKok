package com.devpilot.weatherkok.intro.permissions;

import android.content.Context;

public class PermissionCheck {

    Context mContext;

    public PermissionCheck(Context context) {
        this.mContext = context;
    }

    private final static int APP_PERMISSIONS_REQ_MIC = 1000;
    private final static int APP_PERMISSIONS_REQ_STORAGE = 1100;
    private final static int APP_PERMISSIONS_REQ_PHONE = 1200;
    private final static int ACCESS_COARSE_LOCATION = 1300;
    private final static int ACCESS_FINE_LOCATION = 1400;

    private static final String TAG = PermissionCheck.class.getSimpleName();





}
