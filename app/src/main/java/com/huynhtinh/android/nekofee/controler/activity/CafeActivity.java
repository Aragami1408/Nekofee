package com.huynhtinh.android.nekofee.controler.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.huynhtinh.android.nekofee.controler.fragment.CafeFragment;

import model.Cafe;

/**
 * Created by TINH HUYNH on 5/29/2017.
 */

public class CafeActivity extends SingleFragmentActivity {
    private static final String EXTRA_CAFE = "cafeExtra";

    public static Intent newIntent(Context packageContext, Cafe cafe){
        Intent intent = new Intent(packageContext, CafeActivity.class);
        intent.putExtra(EXTRA_CAFE, cafe);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Cafe cafe = (Cafe) getIntent().getSerializableExtra(EXTRA_CAFE);
        return CafeFragment.newInstance(cafe);
    }


}
