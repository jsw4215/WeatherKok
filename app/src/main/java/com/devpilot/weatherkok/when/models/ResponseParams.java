package com.devpilot.weatherkok.when.models;

import com.google.gson.annotations.SerializedName;

public class ResponseParams {
    private static final String TAG = ResponseParams.class.getSimpleName();

    @SerializedName("response")
    RestResponse response;

    public ResponseParams() {
    }

    public ResponseParams(RestResponse response) {
        this.response = response;
    }

    public RestResponse getResponse() {
        return response;
    }

    public void setResponse(RestResponse response) {
        this.response = response;
    }
}
