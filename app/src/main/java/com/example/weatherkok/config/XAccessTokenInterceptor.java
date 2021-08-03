package com.example.weatherkok.config;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.weatherkok.src.ApplicationClass.X_ACCESS_TOKEN;
import static com.example.weatherkok.src.ApplicationClass.sSharedPreferences;

public class XAccessTokenInterceptor implements Interceptor {

    private static final String TAG = "XAccessTokenInterceptor";

    @Override
    @NonNull
    public Response intercept(@NonNull final Interceptor.Chain chain) throws IOException {

        final Request.Builder builder = chain.request().newBuilder();

//        final String jwtToken = sSharedPreferences.getString(X_ACCESS_TOKEN, null);
        String jwtToken = "8FFE0527-8171-32CF-B797-F3A1D0978E0F";
        if (jwtToken != null) {
            builder.addHeader("X-ACCESS-TOKEN", jwtToken);
        }

        return chain.proceed(builder.build());
    }
}
