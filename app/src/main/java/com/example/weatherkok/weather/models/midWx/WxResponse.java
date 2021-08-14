package com.example.weatherkok.weather.models.midWx;

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

        public String getRegId() {
            return regId;
        }

        public void setRegId(String regId) {
            this.regId = regId;
        }

        public int getRnSt3Am() {
            return rnSt3Am;
        }

        public void setRnSt3Am(int rnSt3Am) {
            this.rnSt3Am = rnSt3Am;
        }

        public int getRnSt3Pm() {
            return rnSt3Pm;
        }

        public void setRnSt3Pm(int rnSt3Pm) {
            this.rnSt3Pm = rnSt3Pm;
        }

        public int getRnSt4Am() {
            return rnSt4Am;
        }

        public void setRnSt4Am(int rnSt4Am) {
            this.rnSt4Am = rnSt4Am;
        }

        public int getRnSt4Pm() {
            return rnSt4Pm;
        }

        public void setRnSt4Pm(int rnSt4Pm) {
            this.rnSt4Pm = rnSt4Pm;
        }

        public int getRnSt5Am() {
            return rnSt5Am;
        }

        public void setRnSt5Am(int rnSt5Am) {
            this.rnSt5Am = rnSt5Am;
        }

        public int getRnSt5Pm() {
            return rnSt5Pm;
        }

        public void setRnSt5Pm(int rnSt5Pm) {
            this.rnSt5Pm = rnSt5Pm;
        }

        public int getRnSt6Am() {
            return rnSt6Am;
        }

        public void setRnSt6Am(int rnSt6Am) {
            this.rnSt6Am = rnSt6Am;
        }

        public int getRnSt6Pm() {
            return rnSt6Pm;
        }

        public void setRnSt6Pm(int rnSt6Pm) {
            this.rnSt6Pm = rnSt6Pm;
        }

        public int getRnSt7Am() {
            return rnSt7Am;
        }

        public void setRnSt7Am(int rnSt7Am) {
            this.rnSt7Am = rnSt7Am;
        }

        public int getRnSt7Pm() {
            return rnSt7Pm;
        }

        public void setRnSt7Pm(int rnSt7Pm) {
            this.rnSt7Pm = rnSt7Pm;
        }

        public int getRnSt8() {
            return rnSt8;
        }

        public void setRnSt8(int rnSt8) {
            this.rnSt8 = rnSt8;
        }

        public int getRnSt9() {
            return rnSt9;
        }

        public void setRnSt9(int rnSt9) {
            this.rnSt9 = rnSt9;
        }

        public int getRnSt10() {
            return rnSt10;
        }

        public void setRnSt10(int rnSt10) {
            this.rnSt10 = rnSt10;
        }

        public String getWf3Am() {
            return wf3Am;
        }

        public void setWf3Am(String wf3Am) {
            this.wf3Am = wf3Am;
        }

        public String getWf3Pm() {
            return wf3Pm;
        }

        public void setWf3Pm(String wf3Pm) {
            this.wf3Pm = wf3Pm;
        }

        public String getWf4Am() {
            return wf4Am;
        }

        public void setWf4Am(String wf4Am) {
            this.wf4Am = wf4Am;
        }

        public String getWf4Pm() {
            return wf4Pm;
        }

        public void setWf4Pm(String wf4Pm) {
            this.wf4Pm = wf4Pm;
        }

        public String getWf5Am() {
            return wf5Am;
        }

        public void setWf5Am(String wf5Am) {
            this.wf5Am = wf5Am;
        }

        public String getWf5Pm() {
            return wf5Pm;
        }

        public void setWf5Pm(String wf5Pm) {
            this.wf5Pm = wf5Pm;
        }

        public String getWf6Am() {
            return wf6Am;
        }

        public void setWf6Am(String wf6Am) {
            this.wf6Am = wf6Am;
        }

        public String getWf6Pm() {
            return wf6Pm;
        }

        public void setWf6Pm(String wf6Pm) {
            this.wf6Pm = wf6Pm;
        }

        public String getWf7Am() {
            return wf7Am;
        }

        public void setWf7Am(String wf7Am) {
            this.wf7Am = wf7Am;
        }

        public String getWf7Pm() {
            return wf7Pm;
        }

        public void setWf7Pm(String wf7Pm) {
            this.wf7Pm = wf7Pm;
        }

        public String getWf8() {
            return wf8;
        }

        public void setWf8(String wf8) {
            this.wf8 = wf8;
        }

        public String getWf9() {
            return wf9;
        }

        public void setWf9(String wf9) {
            this.wf9 = wf9;
        }

        public String getWf10() {
            return wf10;
        }

        public void setWf10(String wf10) {
            this.wf10 = wf10;
        }
    }

    public class Items{
        public List<Item> item;

        public List<Item> getItem() {
            return item;
        }

        public void setItem(List<Item> item) {
            this.item = item;
        }
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

        public Items getItems() {
            return items;
        }

        public void setItems(Items items) {
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
