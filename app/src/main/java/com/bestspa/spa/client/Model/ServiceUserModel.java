package com.bestspa.spa.client.Model;

public class ServiceUserModel {

    ServiceModel service;
    User user;

    public ServiceUserModel(ServiceModel service, User user) {
        this.service = service;
        this.user = user;
    }

    public ServiceUserModel() {

    }

    public ServiceModel getService() {
        return service;
    }

    public void setService(ServiceModel service) {
        this.service = service;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
