package controller.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.webkit.WebView;

import com.huynhtinh.android.nekofee.R;
import controller.fragment.WebFragment;

/**
 * Created by TINH HUYNH on 6/3/2017.
 */

public class WebActivity extends SingleFragmentActivity {

    private static final String EXTRA_URL = "UrlExtra";

    public static Intent newIntent(Context packageContext, Uri uri){
        Intent intent = new Intent(packageContext, WebActivity.class);
        intent.putExtra(EXTRA_URL, uri);
        return intent;
    }

    @Override
    protected WebFragment createFragment() {
        return WebFragment.newInstance((Uri) getIntent().getParcelableExtra(EXTRA_URL));
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        WebFragment webFragment =
                (WebFragment) fragmentManager.findFragmentById(R.id.container_activity);
        if (webFragment != null) {
            WebView webView = webFragment.getWebView();
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }
}
