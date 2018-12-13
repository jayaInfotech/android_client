package com.bestspa.spa.client.map;

public class CustomMarker {
    private Double latitudeStr;
    private Double longitudeStr;
    private String userIdStr;
    private String userImage;
    private String userNameStr;
    private String userRating;
    private String userReviewCount;
    private String userType;

    public CustomMarker(Double latitudeStr, Double longitudeStr, String userIdStr, String userNameStr, String userRating, String userReviewCount, String userImage, String userType) {
        this.latitudeStr = latitudeStr;
        this.longitudeStr = longitudeStr;
        this.userIdStr = userIdStr;
        this.userNameStr = userNameStr;
        this.userImage = userImage;
        this.userRating = userRating;
        this.userReviewCount = userReviewCount;
        this.userType = userType;
    }


    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Double getLatitudeStr() {
        return this.latitudeStr;
    }

    public Double getLongitudeStr() {
        return this.longitudeStr;
    }

    public String getUserIdStr() {
        return this.userIdStr;
    }

    public String getUserNameStr() {
        return this.userNameStr;
    }

    public String getUserImage() {
        return this.userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserRating() {
        return this.userRating;
    }

    public String getUserReviewCount() {
        return this.userReviewCount;
    }
}
