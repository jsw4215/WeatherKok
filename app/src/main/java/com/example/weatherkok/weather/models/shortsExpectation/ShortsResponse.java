package com.example.weatherkok.weather.models;

import java.util.List;

public class ShortsResponse {
    private static final String TAG = ShortsResponse.class.getSimpleName();
    
    public class Header{
        public String resultCode;

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

        public String resultMsg;
    }

    public class Item{
        public String baseDate;
        public String baseTime;
        public String category;
        public String fcstDate;
        public String fcstTime;
        public String fcstValue;
        public int nx;
        public int ny;

        public String getBaseDate() {
            return baseDate;
        }

        public void setBaseDate(String baseDate) {
            this.baseDate = baseDate;
        }

        public String getBaseTime() {
            return baseTime;
        }

        public void setBaseTime(String baseTime) {
            this.baseTime = baseTime;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getFcstDate() {
            return fcstDate;
        }

        public void setFcstDate(String fcstDate) {
            this.fcstDate = fcstDate;
        }

        public String getFcstTime() {
            return fcstTime;
        }

        public void setFcstTime(String fcstTime) {
            this.fcstTime = fcstTime;
        }

        public String getFcstValue() {
            return fcstValue;
        }

        public void setFcstValue(String fcstValue) {
            this.fcstValue = fcstValue;
        }

        public int getNx() {
            return nx;
        }

        public void setNx(int nx) {
            this.nx = nx;
        }

        public int getNy() {
            return ny;
        }

        public void setNy(int ny) {
            this.ny = ny;
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

    public class Response{
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
    }

    public class Root{
        public Response response;

        public Response getResponse() {
            return response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }
    }



}
