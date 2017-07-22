package com.huynhtinh.android.nekofee.controler.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.huynhtinh.android.nekofee.R;
import com.huynhtinh.android.nekofee.controler.activity.ListCafeActivity;

import network.ConnectionChecker;
import preferences.SharedPreference;

/**
 * Created by TINH HUYNH on 5/22/2017.
 */

public class NekofeeFragment extends Fragment {
    private static final String TAG = "NekofeeFragment";

    private static final int ALERT_GPS = 0;
    private static final int ALERT_NETWORK = 1;

    private Button mFindButton;
    private EditText mRadiusEditText;
    private GoogleApiClient mClient;

    public static NekofeeFragment newInstance() {
        Bundle args = new Bundle();

        NekofeeFragment fragment = new NekofeeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        mFindButton.setEnabled(true);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mFindButton.setEnabled(false);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        mFindButton.setEnabled(false);
                    }
                })
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nekofee, container, false);

        mRadiusEditText = (EditText) view.findViewById(R.id.radius_edit_text);
        int savedRadius = SharedPreference.getRadius(getActivity());
        if(savedRadius != 0){
            mRadiusEditText.setText(savedRadius + "");
        }

        mFindButton = (Button) view.findViewById(R.id.find_button);
        mFindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String radius = mRadiusEditText.getText().toString();
                if (radius.isEmpty()) {
                    mRadiusEditText.setError("Please type radius");
                } else if (!radius.isEmpty()) {
                    int radiusNumber = Integer.parseInt(radius);
                    if (radiusNumber < 1 || radiusNumber > 50000) {
                        mRadiusEditText.setError("Radius must be in range of [1, 50000]");
                    } else {
                        ConnectionChecker connectionChecker = new ConnectionChecker(getActivity());
                        if (!connectionChecker.isGpsOn()) {
                            showAlert(ALERT_GPS);
                            return;
                        }
                        if (!connectionChecker.isNetworkConnected()) {
                            showAlert(ALERT_NETWORK);
                            return;
                        }
                        SharedPreference.saveRadius(getActivity(), radiusNumber);
                        sendCurrentLocation(radiusNumber);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mClient != null) {
            mClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mClient != null && mClient.isConnecting()) {
            mClient.disconnect();
        }
    }

    private void showAlert(int alertCode) {
        AlertDialog alertDialog = null;

        if (alertCode == ALERT_GPS) {
            alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("GPS is sleeping")
                    .setMessage("Looks like you forgot to wake GPS up. Please turn it on!")
                    .setIcon(R.mipmap.ic_gps)
                    .setPositiveButton("Do it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setCancelable(false)
                    .create();
        } else if (alertCode == ALERT_NETWORK) {
            alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("Where is the Internet ?")
                    .setMessage("We cannot work without the Internet. Please turn Wifi on!")
                    .setIcon(R.mipmap.ic_network)
                    .setPositiveButton("Do it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setCancelable(false)
                    .create();
        }
        alertDialog.show();
    }

    private void sendCurrentLocation(final int radius) {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.i(TAG, "Current location: lat=" + location.getLatitude() + ", lon=" + location.getLongitude());

                Intent intent = ListCafeActivity.newIntent(getActivity(), location, radius);
                startActivity(intent);
            }
        });
    }

}
