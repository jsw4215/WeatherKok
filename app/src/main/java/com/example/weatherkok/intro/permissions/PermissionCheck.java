package com.example.weatherkok.intro.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

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
