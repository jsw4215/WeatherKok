package com.devpilot.weatherkok.config;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class XAccessTokenInterceptor implements Interceptor {

    private static final String TAG = "XAccessTokenInterceptor";

    @Override
    @NonNull
    public Response intercept(@NonNull final Interceptor.Chain chain) throws IOException {

        final Request.Builder builder = chain.request().newBuilder();

//        final String jwtToken = sSharedPreferences.getString(X_ACCESS_TOKEN, null);
        String jwtToken = "972E51FE-7AEA-3D7F-8AF1-DE08FCD4E069";
        if (jwtToken != null) {
            builder.addHeader("X-ACCESS-TOKEN", jwtToken);
        }

        return chain.proceed(builder.build());
    }
}
