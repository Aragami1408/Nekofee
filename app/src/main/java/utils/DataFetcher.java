package utils;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import model.Cafe;
import model.Review;
import model.RouteInfo;

/**
 * Created by TINH HUYNH on 5/25/2017.
 */

public class DataFetcher {
    private static final String TAG = "DataFetcher";
    private static final String PLACES_API_KEY = "AIzaSyCXm1oYyeBWPSK1lpss15mcv0vSDnxMy2E";
    private static final String MAPS_DISTANCE_API_KEY = "AIzaSyCqwZQoYD7H2XOxc1qzJg4EMEjNE8ScLmQ";
    private static final String MAPS_DIRECTION_API_KEY = "AIzaSyCqwZQoYD7H2XOxc1qzJg4EMEjNE8ScLmQ";
    private static String sNextPageToken;
//    private Location mCurrentLocation;
//    private int mRadius;

    public DataFetcher() {
    }

    public static String getNextPageToken() {
        return sNextPageToken;
    }

    public List<Cafe> fetchNearbyCafe(Location location, int radius, boolean isMore) {
        Uri uri = Uri.parse("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
                .buildUpon()
                .appendQueryParameter("type", "cafe")
                .appendQueryParameter("key", PLACES_API_KEY)
                .appendQueryParameter("location", location.getLatitude() + "," + location.getLongitude())
                .appendQueryParameter("radius", String.valueOf(radius))
                .build();

        if (isMore) {
            uri = uri.buildUpon()
                    .appendQueryParameter("pagetoken", sNextPageToken).build();
        }
        Log.i(TAG, "Uri fetchNearbyCafe: " + uri.toString());
//        mCurrentLocation = location;
//        mRadius = radius;
        return downloadCafeList(uri.toString());
    }

    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        try {
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int readBytes = 0;
            byte[] buffer = new byte[1024];

            while ((readBytes = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readBytes);
            }

            outputStream.flush();
            outputStream.close();

            inputStream.close();
            return outputStream.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    private String getUrlString(String urlSpec) throws IOException {

        return new String(getUrlBytes(urlSpec));
    }

    private List<Cafe> downloadCafeList(String urlSpec) {
        List<Cafe> cafes = new ArrayList<>();
        try {
            String jsonString = getUrlString(urlSpec);
            Log.i(TAG, "Received JSON String: " + jsonString);
            cafes = parseItems(jsonString);
        } catch (IOException e) {
            Log.e(TAG, "Failed to receive cafe list ", e);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse json ", e);
        }
        return cafes;
    }

    private List<Cafe> parseItems(String jsonString) throws JSONException {
        List<Cafe> cafes = new ArrayList<>();
        JSONObject rootObject = new JSONObject(jsonString);
        JSONArray results = rootObject.getJSONArray("results");

        sNextPageToken = rootObject.optString("next_page_token");
        Log.i(TAG, "Next page token: " + sNextPageToken);
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            Cafe cafe = new Cafe();

            JSONObject location = result.getJSONObject("geometry").getJSONObject("location");

            cafe.setLatitude(location.getDouble("lat"));
            cafe.setLongitude(location.getDouble("lng"));
            cafe.setName(result.getString("name"));

            JSONObject openingHours = result.optJSONObject("opening_hours");
            if (openingHours != null) {
                cafe.setOpenNow(openingHours.getBoolean("open_now") ? Cafe.OPEN_NOW : Cafe.CLOSED);
            }
            JSONArray photos = result.optJSONArray("photos");
            if (photos != null) {
                JSONObject photo = photos.getJSONObject(0);
                cafe.setMainPhotoRef(photo.getString("photo_reference"));
            }

            cafe.setCafeId(result.getString("place_id"));
            cafe.setRating((float) result.optDouble("rating"));

            if (Float.isNaN(cafe.getRating())) {
                cafe.setRating(0f);
            }

            cafe.setAddress(result.getString("vicinity"));

            cafes.add(cafe);

//            Location cafeLocation = new Location("Destination location");
//            cafeLocation.setLatitude(cafe.getLatitude());
//            cafeLocation.setLongitude(cafe.getLongitude());

        }

        return cafes;
    }

    public String getPhotoUrl(String photoRef, int size) {
        return getPhotoUrl(photoRef, size, size);
    }

    public String getPhotoUrl(String photoRef, int width, int height) {
        Uri uri = Uri.parse("https://maps.googleapis.com/maps/api/place/photo")
                .buildUpon()
                .appendQueryParameter("maxwidth", width + "")
                .appendQueryParameter("maxheight", height + "")
                .appendQueryParameter("photoreference", photoRef)
                .appendQueryParameter("key", PLACES_API_KEY)
                .build();
        return uri.toString();
    }

    public void getCafeDetail(Cafe cafe) {
        Uri uri = Uri.parse("https://maps.googleapis.com/maps/api/place/details/json")
                .buildUpon()
                .appendQueryParameter("placeid", cafe.getCafeId())
                .appendQueryParameter("key", PLACES_API_KEY)
                .build();

        String jsonString;
        try {
            jsonString = getUrlString(uri.toString());
            JSONObject root = new JSONObject(jsonString);
            Log.i(TAG, "Received detail JSON: " + jsonString);
            Log.i(TAG, "Url getCafeDetail: " + uri.toString());
            JSONObject result = root.getJSONObject("result");

            cafe.setPhoneNumber(result.optString("formatted_phone_number"));

            JSONObject openingHours = result.optJSONObject("opening_hours");

            if (openingHours != null) {
                JSONArray weekdayText = openingHours.optJSONArray("weekday_text");
                if (weekdayText != null) {
                    ArrayList<String> workingDays = new ArrayList<>();
                    for (int i = 0; i < weekdayText.length(); i++) {
                        workingDays.add(weekdayText.getString(i));
                    }
                    cafe.setWorkingDays(workingDays);
                }
            }

            JSONArray photos = result.optJSONArray("photos");
            if (photos != null) {
                ArrayList<String> photoRefs = new ArrayList<>();
                for (int i = 0; i < photos.length(); i++) {
                    photoRefs.add(photos.getJSONObject(i).getString("photo_reference"));
                }
                cafe.setPhotoRefs(photoRefs);
            }

            cafe.setPriceLevel(result.optInt("price_level", -1));

            JSONArray reviews = result.optJSONArray("reviews");
            if (reviews != null) {
                ArrayList<Review> cafeReviews = new ArrayList<>();
                for (int i = 0; i < reviews.length(); i++) {
                    JSONObject review = reviews.getJSONObject(i);
                    Review cafeReview = new Review();
                    cafeReview.setAuthor(review.getString("author_name"));
                    cafeReview.setRating((float) review.getDouble("rating"));
                    if (Float.isNaN(cafeReview.getRating())) {
                        cafe.setRating(0f);
                    }
                    cafeReview.setTime(review.getString("relative_time_description"));
                    cafeReview.setText(review.getString("text"));
                    cafeReview.setProfilePhotoUrl(review.optString("profile_photo_url"));

                    cafeReviews.add(cafeReview);

                }

                cafe.setReviews(cafeReviews);
            }

            cafe.setWebsite(result.optString("website"));

        } catch (IOException e) {
            Log.i(TAG, "Fail to download data", e);
        } catch (JSONException e) {
            Log.i(TAG, "Fail to parse JSON", e);
        }

    }


    public RouteInfo getRoutes(LatLng current, LatLng cafe, boolean isDrivingMode) {
        Uri uri = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("origin", current.latitude + "," + current.longitude)
                .appendQueryParameter("destination", cafe.latitude + "," + cafe.longitude)
                .appendQueryParameter("units", "metric")
                .appendQueryParameter("key", MAPS_DIRECTION_API_KEY)
                .build();
        if (!isDrivingMode) {
            uri = uri.buildUpon().appendQueryParameter("mode", "walking").build();
        }
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        RouteInfo routeInfo = new RouteInfo();

        List<List<HashMap<String, String>>> routes = new ArrayList<>();

        try {
            String jsonString = getUrlString(uri.toString());
            JSONObject root = new JSONObject(jsonString);
            Log.i(TAG, "Uri for getRoutes: " + uri.toString());
            Log.i(TAG, "Receive routes JSON: " + jsonString);
            jRoutes = root.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(l)).latitude));
                            hm.put("lng", Double.toString((list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }
            routeInfo.setRoutes(routes);
            jLegs = (jRoutes.getJSONObject(0)).getJSONArray("legs");
            routeInfo.setDuration(jLegs.getJSONObject(0).getJSONObject("duration").getString("text"));
            routeInfo.setDistance(jLegs.getJSONObject(0).getJSONObject("distance").getString("text"));


        } catch (IOException e) {
            Log.i(TAG, "Fail to download routes", e);
        } catch (JSONException e) {
            Log.i(TAG, "Fail to parse routes", e);
        }
        return routeInfo;
    }

    // Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


}
