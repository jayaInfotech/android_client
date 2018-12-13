package com.bestspa.spa.client.Model;

import java.sql.Date;
import java.util.ArrayList;

public class Booking {

    String _id,customerId,serviceId,merchantId,bookingdate,bookingtime,visitType,bookingstatus,comment;
    Boolean completed;
    ArrayList<String> address;

    public Booking(String customerId, String serviceId, String merchantId, String bookingdate, String bookingtime, String visitType, String bookingstatus, String comment, Boolean completed, ArrayList<String> address) {
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.merchantId = merchantId;
        this.bookingdate = bookingdate;
        this.bookingtime = bookingtime;
        this.visitType = visitType;
        this.bookingstatus = bookingstatus;
        this.comment = comment;
        this.completed = completed;
        this.address = address;
    }

    public Booking() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public ArrayList<String> getAddress() {
        return address;
    }

    public void setAddress(ArrayList<String> address) {
        this.address = address;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getBookingdate() {
        return bookingdate;
    }

    public void setBookingdate(String bookingdate) {
        this.bookingdate = bookingdate;
    }

    public String getBookingtime() {
        return bookingtime;
    }

    public void setBookingtime(String bookingtime) {
        this.bookingtime = bookingtime;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public String getBookingstatus() {
        return bookingstatus;
    }

    public void setBookingstatus(String bookingstatus) {
        this.bookingstatus = bookingstatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
