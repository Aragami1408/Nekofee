package com.huynhtinh.android.nekofee.controler.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.huynhtinh.android.nekofee.R;
import com.huynhtinh.android.nekofee.controler.fragment.NekofeeFragment;

public class NekofeeActivity extends SingleFragmentActivity {

    private static final int REQUEST_CODE = 1;

    @Override
    protected Fragment createFragment() {
        return NekofeeFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_activity);

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS){
            if(googleApiAvailability.isUserResolvableError(result)){
                googleApiAvailability.getErrorDialog(this, result, REQUEST_CODE).show();
            }
        }
    }
}
