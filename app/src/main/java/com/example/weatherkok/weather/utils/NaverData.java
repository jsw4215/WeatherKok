package com.example.weatherkok.weather.utils;

import java.util.List;

public class NaverData {
    public String status;
    public Meta meta;
    public List<Address> addresses;
    public String errorMessage;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public class Meta{
        public int totalCount;
        public int page;
        public int count;

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public class AddressElement{
        public List<String> types;
        public String longName;
        public String shortName;
        public String code;

        public List<String> getTypes() {
            return types;
        }

        public void setTypes(List<String> types) {
            this.types = types;
        }

        public String getLongName() {
            return longName;
        }

        public void setLongName(String longName) {
            this.longName = longName;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    public class Address{
        public String roadAddress;
        public String jibunAddress;
        public String englishAddress;
        public List<AddressElement> addressElements;
        public String x;
        public String y;
        public double distance;

        public String getRoadAddress() {
            return roadAddress;
        }

        public void setRoadAddress(String roadAddress) {
            this.roadAddress = roadAddress;
        }

        public String getJibunAddress() {
            return jibunAddress;
        }

        public void setJibunAddress(String jibunAddress) {
            this.jibunAddress = jibunAddress;
        }

        public String getEnglishAddress() {
            return englishAddress;
        }

        public void setEnglishAddress(String englishAddress) {
            this.englishAddress = englishAddress;
        }

        public List<AddressElement> getAddressElements() {
            return addressElements;
        }

        public void setAddressElements(List<AddressElement> addressElements) {
            this.addressElements = addressElements;
        }

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }
    }


}