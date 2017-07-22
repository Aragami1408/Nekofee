package com.huynhtinh.android.nekofee.controler.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.huynhtinh.android.nekofee.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.RouteInfo;
import data.DataFetcher;

/**
 * Created by TINH HUYNH on 6/1/2017.
 */

public class MapActivity extends AppCompatActivity {
    private static final String EXTRA_CURRENT_LATLNG = "currentLatLngExtra";
    private static final String EXTRA_CAFE_LATLNG = "cafeLatLngExtra";
    private static final String DRIVING_MODE = "Driving";
    private static final String WALKING_MODE = "Walking";

    private LatLng mCurrentLatLng;
    private LatLng mCafeLatLng;
    private GoogleMap mMap;
    private boolean mDrivingMode;
    private RouteInfo mRouteInfo;

    private Button mDistanceButton;
    private Button mDurationButton;
    private Button mModeButton;

    public static Intent newIntent(Context packageContext, LatLng current, LatLng cafe) {
        Intent intent = new Intent(packageContext, MapActivity.class);
        intent.putExtra(EXTRA_CURRENT_LATLNG, current);
        intent.putExtra(EXTRA_CAFE_LATLNG, cafe);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDistanceButton = (Button) findViewById(R.id.distance_button);
        mDurationButton = (Button) findViewById(R.id.duration_button);
        mModeButton = (Button) findViewById(R.id.mode_button);

        mModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invalidateOptionsMenu();
                updateUI();
            }
        });

        mDrivingMode = true;

        Intent intent = getIntent();
        mCurrentLatLng = intent.getParcelableExtra(EXTRA_CURRENT_LATLNG);
        mCafeLatLng = intent.getParcelableExtra(EXTRA_CAFE_LATLNG);
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                updateUI();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_map, menu);
        MenuItem changModeItem = menu.findItem(R.id.change_transit_mode_item);
        mDrivingMode = !mDrivingMode;
        String driveMode = mDrivingMode ? DRIVING_MODE : WALKING_MODE;
        changModeItem.setTitle(getResources().getString(R.string.change_mode, driveMode));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_transit_mode_item:
                invalidateOptionsMenu();
                updateUI();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        if (mMap == null) {
            Toast.makeText(this, "Null Google map", Toast.LENGTH_LONG).show();
            return;
        }

        mMap.clear();

        MarkerOptions currentMarker = new MarkerOptions();
        currentMarker.position(mCurrentLatLng);
        currentMarker.title("Your location");
        currentMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        MarkerOptions cafeMarker = new MarkerOptions();
        cafeMarker.position(mCafeLatLng);
        cafeMarker.title("Cafe Location");
        cafeMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        mMap.addMarker(currentMarker);
        mMap.addMarker(cafeMarker);

        new DrawRouteTask().execute(mCurrentLatLng, mCafeLatLng);

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(mCurrentLatLng)
                .include(mCafeLatLng)
                .build();

        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 120);
        mMap.animateCamera(update);

    }

    private class DrawRouteTask extends AsyncTask<LatLng, Void, RouteInfo> {

        @Override
        protected RouteInfo doInBackground(LatLng... params) {
            LatLng current = params[0];
            LatLng cafe = params[1];
            return new DataFetcher().fetchRoutes(current, cafe, mDrivingMode);
        }

        @Override
        protected void onPostExecute(RouteInfo result) {
            super.onPostExecute(result);
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            mRouteInfo = result;

            List<List<HashMap<String, String>>> routes = result.getRoutes();

            // Traversing through all the routes

            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = routes.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }


                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);

                // Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);
            }
            mDistanceButton.setText(result.getDistance());
            mDurationButton.setText(result.getDuration());
            mModeButton.setText(mDrivingMode ? WALKING_MODE : DRIVING_MODE);
        }
    }

}
