package utils;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import model.Cafe;

/**
 * Created by TINH HUYNH on 5/25/2017.
 */

public class DataFetcher {
    private static final String TAG = "DataFetcher";
    private static final String API_KEY = "AIzaSyCXm1oYyeBWPSK1lpss15mcv0vSDnxMy2E";
    private static final Uri ENDPOINT = Uri.parse("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
            .buildUpon()
            .appendQueryParameter("type", "cafe")
            .appendQueryParameter("key", API_KEY)
            .build();
    private static String sNextPageToken;

    public static String getNextPageToken() {
        return sNextPageToken;
    }

    public List<Cafe> fetchNearbyCafe(Location location, int radius, boolean isMore) {
        Uri uri = ENDPOINT.buildUpon()
                .appendQueryParameter("location", location.getLatitude() + "," + location.getLongitude())
                .appendQueryParameter("radius", String.valueOf(radius))
                .build();

        if (isMore) {
            uri = uri.buildUpon()
                    .appendQueryParameter("pagetoken", sNextPageToken).build();
        }
        Log.i(TAG, "Uri fetchNearbyCafe: " + uri.toString());

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
        }

        return cafes;
    }

    public String getPhotoUrl(String photoRef){
        Uri uri =  Uri.parse("https://maps.googleapis.com/maps/api/place/photo")
                .buildUpon()
                .appendQueryParameter("maxwidth", 300 + "")
                .appendQueryParameter("maxheight", 300 + "")
                .appendQueryParameter("photoreference", photoRef)
                .appendQueryParameter("key", API_KEY)
                .build();
        return uri.toString();
    }

}
