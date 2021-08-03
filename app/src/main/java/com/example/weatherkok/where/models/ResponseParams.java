package com.example.weatherkok.where.models;

import com.google.gson.annotations.SerializedName;

public class ResponseParams {
    @SerializedName("response")
    SidoResponse response;

    public ResponseParams(SidoResponse response) {
        this.response = response;
    }

    public SidoResponse getResponse() {
        return response;
    }

    public void setResponse(SidoResponse response) {
        this.response = response;
    }
}
