package com.bestspa.spa.client.Model;

import java.util.ArrayList;

public class ServiceModel {

    private ArrayList<SubServiceModel> subServiceList;
    private String serviceImage;
    private String serviceName;
    private String _id;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public ArrayList<SubServiceModel> getSubServiceList() {
        return subServiceList;
    }

    public void setSubServiceList(ArrayList<SubServiceModel> subServiceList) {
        this.subServiceList = subServiceList;
    }

    public String getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(String serviceImage) {
        this.serviceImage = serviceImage;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
