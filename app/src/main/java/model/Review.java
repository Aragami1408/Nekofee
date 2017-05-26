package model;

import java.util.Date;

/**
 * Created by TINH HUYNH on 5/25/2017.
 */

public class Review {
    private String mAuthor;
    private String text;
    private Float mRating;
    private Date mDate;

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Float getRating() {
        return mRating;
    }

    public void setRating(Float rating) {
        mRating = rating;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }
}
