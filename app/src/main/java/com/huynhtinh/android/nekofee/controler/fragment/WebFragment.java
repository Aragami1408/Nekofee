package com.huynhtinh.android.nekofee.controler.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.huynhtinh.android.nekofee.R;


/**
 * Created by TINH HUYNH on 6/3/2017.
 */

public class WebFragment extends Fragment {

    private static final String ARG_URL = "UriArg";

    private Uri mUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static WebFragment newInstance(Uri uri) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_URL, uri);
        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUri = getArguments().getParcelable(ARG_URL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web, container, false);

        mWebView =  (WebView) view.findViewById(R.id.web_view);
        mProgressBar =  (ProgressBar) view.findViewById(R.id.web_progress_bar);

        mProgressBar.setMax(100);

        mWebView.getSettings().getJavaScriptEnabled();
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress == 100){
                    mProgressBar.setVisibility(View.GONE);
                }else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.getSupportActionBar().setSubtitle(title);
            }
        });

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    url = request.getUrl().toString();
                    if(!url.startsWith("https") || !url.startsWith("http")){
                        Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }
        });

        mWebView.loadUrl(String.valueOf(mUri));

        return view;
    }

    public WebView getWebView() {
        return mWebView;
    }
}
