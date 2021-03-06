package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TINH HUYNH on 5/25/2017.
 */

public class Cafe implements Serializable {
    public static final String OPEN_NOW = "Open now";
    public static final String CLOSED = "Closed";

    public static final String PRICE_LEVEL_0 = "Free";
    public static final String PRICE_LEVEL_1 = "Inexpensive";
    public static final String PRICE_LEVEL_2 = "Moderate";
    public static final String PRICE_LEVEL_3 = "Expensive";
    public static final String PRICE_LEVEL_4 = "Very Expensive";

    private String name;
    private String address;
    private float mRating;
    private double mLatitude;
    private double mLongitude;
    private String mCafeId;
    private String mMainPhotoRef;
    private String mOpenNow;
    private int mPriceLevel;
    private String mPhoneNumber;
    private String mWebsite;
    private String mDistance;
    private String mDuration;
    private ArrayList<String> mWorkingDays;
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

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        mDuration = duration;
    }

    public ArrayList<String> getWorkingDays() {
        return mWorkingDays;
    }

    public void setWorkingDays(ArrayList<String> workingDays) {
        mWorkingDays = workingDays;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(String website) {
        mWebsite = website;
    }

    public int getPriceLevel() {
        return mPriceLevel;
    }

    public void setPriceLevel(int priceLevel) {
        mPriceLevel = priceLevel;
    }
}
