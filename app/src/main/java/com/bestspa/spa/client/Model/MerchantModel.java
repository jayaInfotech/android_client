package com.bestspa.spa.client.Model;

import java.util.ArrayList;

public class MerchantModel {

    String businessName, websiteLink, facebookLink, description;
    TimeModel time;
    Boolean canVisit,canGo;
    ArrayList<Integer> rating;

    public ArrayList<Integer> getRating() {
        return rating;
    }

    public void setRating(ArrayList<Integer> rating) {
        this.rating = rating;
    }

    public Boolean getCanVisit() {
        return canVisit;
    }

    public void setCanVisit(Boolean canVisit) {
        this.canVisit = canVisit;
    }

    public Boolean getCanGo() {
        return canGo;
    }

    public void setCanGo(Boolean canGo) {
        this.canGo = canGo;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getWebsiteLink() {
        return websiteLink;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TimeModel getTime() {
        return time;
    }

    public void setTime(TimeModel time) {
        this.time = time;
    }
}
