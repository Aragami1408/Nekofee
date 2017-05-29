package com.huynhtinh.android.nekofee.controler.activity;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.huynhtinh.android.nekofee.controler.fragment.CafeFragment;

/**
 * Created by TINH HUYNH on 5/29/2017.
 */

public class CafeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CafeFragment();
    }
}
