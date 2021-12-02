package com.devpilot.weatherkok.where.models;

public class WhereRequestBody {

    String key;
    String domain;
    String request;
    String format;
    int size;
    int page;
    boolean geometry;
    boolean attribute;
    String crs;
    String geomfilter;
    String data;

    public WhereRequestBody(String key, String domain, String request, String format, int size, int page, boolean geometry, boolean attribute, String crs, String geomfilter, String data) {
        this.key = key;
        this.domain = domain;
        this.request = request;
        this.format = format;
        this.size = size;
        this.page = page;
        this.geometry = geometry;
        this.attribute = attribute;
        this.crs = crs;
        this.geomfilter = geomfilter;
        this.data = data;
    }
}
