package com.bestspa.spa.client.Model;

import java.util.ArrayList;

public class User {

    String _id,userName, email, password, phone, address, country, userTypes, birthdate, signupwith,fcmToken;
    ArrayList<String> favoriteList,userImage,latLong;
    MerchantModel merchantid;

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public ArrayList<String> getLatLong() {
        return latLong;
    }

    public void setLatLong(ArrayList<String> latLong) {
        this.latLong = latLong;
    }

    public void setMerchantid(MerchantModel merchantid) {
        this.merchantid = merchantid;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUserTypes() {
        return userTypes;
    }

    public void setUserTypes(String userTypes) {
        this.userTypes = userTypes;
    }

    public ArrayList<String> getUserImage() {
        return userImage;
    }

    public void setUserImage(ArrayList<String> userImage) {
        this.userImage = userImage;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public MerchantModel getMerchantid() {
        return merchantid;
    }

    public String getSignupwith() {
        return signupwith;
    }

    public void setSignupwith(String signupwith) {
        this.signupwith = signupwith;
    }

    public ArrayList<String> getFavoriteList() {
        return favoriteList;
    }

    public void setFavoriteList(ArrayList<String> favoriteList) {
        this.favoriteList = favoriteList;
    }
}
