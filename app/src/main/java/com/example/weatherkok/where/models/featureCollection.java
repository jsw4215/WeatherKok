package com.example.weatherkok.where.models;

import java.util.List;

public class featureCollection {
    String type;
    List<Float> bbox;
    List<Features> features;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Float> getBbox() {
        return bbox;
    }

    public void setBbox(List<Float> bbox) {
        this.bbox = bbox;
    }

    public List<Features> getFeatures() {
        return features;
    }

    public void setFeatures(List<Features> features) {
        this.features = features;
    }
}
