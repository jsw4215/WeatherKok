package com.example.weatherkok.weather.models.midTemp;

import java.util.ArrayList;

public class MidTempResponse {
    private static final String TAG = MidTempResponse.class.getSimpleName();

    Response response;

    public MidTempResponse() {
    }

    public MidTempResponse(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public class Header{
        public String resultCode;
        public String resultMsg;

        public String getResultCode() {
            return resultCode;
        }

        public void setResultCode(String resultCode) {
            this.resultCode = resultCode;
        }

        public String getResultMsg() {
            return resultMsg;
        }

        public void setResultMsg(String resultMsg) {
            this.resultMsg = resultMsg;
        }
    }

    public class Body{
        public String dataType;
        public TempItems items = new TempItems();
        public int pageNo;
        public int numOfRows;
        public int totalCount;

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public TempItems getItems() {
            return items;
        }

        public void setItems(TempItems items) {
            this.items = items;
        }

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public int getNumOfRows() {
            return numOfRows;
        }

        public void setNumOfRows(int numOfRows) {
            this.numOfRows = numOfRows;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
    }

    public class Response{
        public Header header = new Header();
        public Body body = new Body();

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

}
