package com.example.weatherkok.when.models;

import com.google.gson.annotations.SerializedName;

public class RestResponse {
    private static final String TAG = RestResponse.class.getSimpleName();

        @SerializedName("header")
        private Header header;

        @SerializedName("body")
        private Body body;

        public Header getHeader() {
            return header;
        }

        public void setHeader(Header header) {
            this.header = header;
        }

        public Body getBody() {
            return body;
        }

        public void setBody(Body body) {
            this.body = body;
        }

    }
