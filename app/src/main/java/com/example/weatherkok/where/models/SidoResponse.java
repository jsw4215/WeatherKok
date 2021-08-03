package com.example.weatherkok.where.models;

import com.google.gson.annotations.SerializedName;

public class SidoResponse {

    @SerializedName("service")
    Service service;

    @SerializedName("status")
    String status;

    @SerializedName("record")
    Record record;

    @SerializedName("page")
    Page page;

    @SerializedName("result")
    Result result;

    public SidoResponse() {
    }

    public SidoResponse(Service service, String status, Record record, Page page, Result result) {
        this.service = service;
        this.status = status;
        this.record = record;
        this.page = page;
        this.result = result;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
