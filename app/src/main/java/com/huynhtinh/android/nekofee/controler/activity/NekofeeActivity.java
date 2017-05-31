package com.huynhtinh.android.nekofee.controler.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.huynhtinh.android.nekofee.R;
import com.huynhtinh.android.nekofee.controler.fragment.NekofeeFragment;

public class NekofeeActivity extends SingleFragmentActivity {

    private static final int REQUEST_CODE = 1;

    @Override
    protected Fragment createFragment() {
        return NekofeeFragment.newInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int errorCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if(errorCode != ConnectionResult.SUCCESS){
            if(googleApiAvailability.isUserResolvableError(errorCode)){
                googleApiAvailability.getErrorDialog(this, errorCode,
                        REQUEST_CODE, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                //leave if services are unavailable
                                finish();
                            }
                        }).show();
            }
        }
    }
}
