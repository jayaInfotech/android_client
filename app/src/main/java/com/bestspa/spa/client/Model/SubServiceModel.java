package com.bestspa.spa.client.Model;

import java.util.ArrayList;

public class SubServiceModel {

    private String serviceName;
    private String serviceDuration;
    private String servicePrice;
    private String serviceDescription;
    private String serviceImage;
    private String merchantId;
    private ArrayList<String> serviceReviewsIds;
    private ArrayList<String> serviceFavoriteIds;
    private ArrayList<String> serviceRatings;
    private int serviceUses;
    private String _id;

    public SubServiceModel() {
    }

    public SubServiceModel(String serviceName, String serviceDuration, String servicePrice, String serviceDescription, String merchantId) {
        this.serviceName = serviceName;
        this.serviceDuration = serviceDuration;
        this.servicePrice = servicePrice;
        this.serviceDescription = serviceDescription;
        this.merchantId = merchantId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(String serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    public String getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(String servicePrice) {
        this.servicePrice = servicePrice;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(String serviceImage) {
        this.serviceImage = serviceImage;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public ArrayList<String> getServiceReviewsIds() {
        return serviceReviewsIds;
    }

    public void setServiceReviewsIds(ArrayList<String> serviceReviewsIds) {
        this.serviceReviewsIds = serviceReviewsIds;
    }

    public ArrayList<String> getServiceFavoriteIds() {
        return serviceFavoriteIds;
    }

    public void setServiceFavoriteIds(ArrayList<String> serviceFavoriteIds) {
        this.serviceFavoriteIds = serviceFavoriteIds;
    }

    public ArrayList<String> getServiceRatings() {
        return serviceRatings;
    }

    public void setServiceRatings(ArrayList<String> serviceRatings) {
        this.serviceRatings = serviceRatings;
    }

    public int getServiceUses() {
        return serviceUses;
    }

    public void setServiceUses(int serviceUses) {
        this.serviceUses = serviceUses;
    }
}
