package com.huynhtinh.android.nekofee.controler.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.huynhtinh.android.nekofee.R;

/**
 * Created by TINH HUYNH on 5/21/2017.
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {

    abstract protected Fragment createFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_activity);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.container_activity);
        if (fragment == null) {
            fragment = createFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.container_activity, fragment)
                    .commit();
        }
    }
}
