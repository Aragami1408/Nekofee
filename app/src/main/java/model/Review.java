package model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by TINH HUYNH on 5/25/2017.
 */

public class Review implements Serializable {
    private String mAuthor;
    private String text;
    private Float mRating;
    private String mTime;

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

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }
}
