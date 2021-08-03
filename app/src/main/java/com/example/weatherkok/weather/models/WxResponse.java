package com.example.weatherkok.weather.models;

import java.util.List;

public class WxResponse {
    private static final String TAG = WxResponse.class.getSimpleName();

    public Header header;
    public Body body;

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

    public class Header{
        public String resultCode;
        public String resultMsg;
    }

    public class Item{
        public String regId;
        public int rnSt3Am;
        public int rnSt3Pm;
        public int rnSt4Am;
        public int rnSt4Pm;
        public int rnSt5Am;
        public int rnSt5Pm;
        public int rnSt6Am;
        public int rnSt6Pm;
        public int rnSt7Am;
        public int rnSt7Pm;
        public int rnSt8;
        public int rnSt9;
        public int rnSt10;
        public String wf3Am;
        public String wf3Pm;
        public String wf4Am;
        public String wf4Pm;
        public String wf5Am;
        public String wf5Pm;
        public String wf6Am;
        public String wf6Pm;
        public String wf7Am;
        public String wf7Pm;
        public String wf8;
        public String wf9;
        public String wf10;
    }

    public class Items{
        public List<Item> item;
    }

    public class Body{
        public String dataType;
        public Items items;
        public int pageNo;
        public int numOfRows;
        public int totalCount;

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }
    }

}
