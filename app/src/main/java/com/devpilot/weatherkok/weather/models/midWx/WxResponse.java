package com.devpilot.weatherkok.weather.models.midWx;

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

    public class Body{
        public String dataType;
        public WxItems items = new WxItems();
        public int pageNo;
        public int numOfRows;
        public int totalCount;

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public WxItems getItems() {
            return items;
        }

        public void setItems(WxItems items) {
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

}
