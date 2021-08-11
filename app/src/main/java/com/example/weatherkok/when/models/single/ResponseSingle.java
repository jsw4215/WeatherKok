package com.example.weatherkok.when.models.single;

import com.google.gson.annotations.SerializedName;

public class ResponseSingle {
    private static final String TAG = ResponseSingle.class.getSimpleName();

    @SerializedName("response")
    RestResponse response;

    public ResponseSingle() {
    }

    public ResponseSingle(RestResponse response) {
        this.response = response;
    }

    public RestResponse getResponse() {
        return response;
    }

    public void setResponse(RestResponse response) {
        this.response = response;
    }
}
