package com.huynhtinh.android.nekofee.controler.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.Fragment;

import com.huynhtinh.android.nekofee.controler.fragment.ListCafeFragment;

/**
 * Created by TINH HUYNH on 5/24/2017.
 */

public class ListCafeActivity extends SingleFragmentActivity {

    private static final String EXTRA_CURRENT_LOCATION = "currentLocationExtra";

    @Override
    protected Fragment createFragment() {
        Location location = getIntent().getParcelableExtra(EXTRA_CURRENT_LOCATION);
        if(location == null){
            return null;
        }
        return ListCafeFragment.newInstance(location);
    }

    public static Intent newIntent(Context packageContext, Location currentLocation){
        Intent intent = new Intent(packageContext, ListCafeActivity.class);
        intent.putExtra(EXTRA_CURRENT_LOCATION, currentLocation);
        return intent;
    }
}
