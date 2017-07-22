package controller.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.Fragment;

import controller.fragment.CafeFragment;

import model.Cafe;

/**
 * Created by TINH HUYNH on 5/29/2017.
 */

public class CafeActivity extends SingleFragmentActivity {
    private static final String EXTRA_CAFE = "cafeExtra";
    private static final String EXTRA_CURRENT_LOCATION = "LocationExtra";

    public static Intent newIntent(Context packageContext, Cafe cafe, Location currentLocation){
        Intent intent = new Intent(packageContext, CafeActivity.class);
        intent.putExtra(EXTRA_CAFE, cafe);
        intent.putExtra(EXTRA_CURRENT_LOCATION, currentLocation);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Cafe cafe = (Cafe) getIntent().getSerializableExtra(EXTRA_CAFE);
        Location location =  getIntent().getParcelableExtra(EXTRA_CURRENT_LOCATION);
        return CafeFragment.newInstance(cafe, location);
    }


}
