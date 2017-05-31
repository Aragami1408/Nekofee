package model;

import java.util.HashMap;
import java.util.List;

/**
 * Created by TINH HUYNH on 6/1/2017.
 */

public class RouteInfo {
    private List<List<HashMap<String, String>>> mRoutes;
    private String mDistance;
    private String mDuration;

    public List<List<HashMap<String, String>>> getRoutes() {
        return mRoutes;
    }

    public void setRoutes(List<List<HashMap<String, String>>> routes) {
        mRoutes = routes;
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
}
