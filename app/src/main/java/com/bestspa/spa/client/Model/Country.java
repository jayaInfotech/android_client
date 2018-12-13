package com.bestspa.spa.client.Model;

public class Country {

    private String countryCode;
    private String countryCourrencyCode;
    private String countryCourrencySymbol;
    private String countryName;
    private String countryPrice;
    private String countyId;

    public Country() {

    }

    public String getCountryPrice() {
        return this.countryPrice;
    }

    public void setCountryPrice(String countryPrice) {
        this.countryPrice = countryPrice;
    }

    public Country(String countyId, String countryName, String countryCode, String countryCourrencyCode, String countryCourrencySymbol) {
        this.countyId = countyId;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.countryCourrencyCode = countryCourrencyCode;
        this.countryCourrencySymbol = countryCourrencySymbol;
    }

    public String getCountyId() {
        return this.countyId;
    }

    public void setCountyId(String countyId) {
        this.countyId = countyId;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCourrencyCode() {
        return this.countryCourrencyCode;
    }

    public void setCountryCourrencyCode(String countryCourrencyCode) {
        this.countryCourrencyCode = countryCourrencyCode;
    }

    public String getCountryCourrencySymbol() {
        return this.countryCourrencySymbol;
    }

    public void setCountryCourrencySymbol(String countryCourrencySymbol) {
        this.countryCourrencySymbol = countryCourrencySymbol;
    }
}

