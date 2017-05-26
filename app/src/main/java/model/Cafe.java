package model;

import java.util.ArrayList;

/**
 * Created by TINH HUYNH on 5/25/2017.
 */

public class Cafe {
    public static final String OPEN_NOW = "Open now";
    public static final String CLOSED = "Closed";

    private String name;
    private String address;
    private float mRating;
    private double mLatitude;
    private double mLongitude;
    private String mCafeId;
    private String mMainPhotoRef;
    private String mOpenNow;
    private String mPhoneNumber;
    private ArrayList<String> mWeekDay;
    private ArrayList<String> mPhotoRefs;
    private ArrayList<Review> mReviews;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        mRating = rating;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public String getCafeId() {
        return mCafeId;
    }

    public void setCafeId(String cafeId) {
        mCafeId = cafeId;
    }

    public String getMainPhotoRef() {
        return mMainPhotoRef;
    }

    public void setMainPhotoRef(String mainPhotoRef) {
        mMainPhotoRef = mainPhotoRef;
    }

    public String getOpenNow() {
        return mOpenNow;
    }

    public void setOpenNow(String openNow) {
        mOpenNow = openNow;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public ArrayList<String> getWeekDay() {
        return mWeekDay;
    }

    public void setWeekDay(ArrayList<String> weekDay) {
        mWeekDay = weekDay;
    }

    public ArrayList<String> getPhotoRefs() {
        return mPhotoRefs;
    }

    public void setPhotoRefs(ArrayList<String> photoRefs) {
        mPhotoRefs = photoRefs;
    }

    public ArrayList<Review> getReviews() {
        return mReviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        mReviews = reviews;
    }
}
