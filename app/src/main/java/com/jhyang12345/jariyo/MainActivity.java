package com.jhyang12345.jariyo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    JariyoProperties properties;

    @BindView(R.id.main_web_view)
    WebView mainWebView;

    @BindView(R.id.error_web_view)
    WebView errorWebView;

    @BindView(R.id.loading_overylay)
    LinearLayout loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        properties = JariyoProperties.getInstance();

        loadingOverlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        WebSettings webSettings = mainWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);

        mainWebView.setWebViewClient(new MainWebViewClient());
        mainWebView.setBackgroundColor(Color.TRANSPARENT);
        mainWebView.addJavascriptInterface(new WebAppInterface(this, mainWebView, errorWebView), "Android");

        try {
            mainWebView.loadUrl(properties.url);
        } catch(Exception e) {
            e.printStackTrace();
        }

        WebSettings errorWebSettings = errorWebView.getSettings();

        errorWebSettings.setJavaScriptEnabled(true);

        errorWebView.loadUrl("file:///android_asset/error.html");
        errorWebView.addJavascriptInterface(new WebAppInterface(this, mainWebView, errorWebView), "Android");


    }

    @Override
    public void onBackPressed() {
        if (mainWebView.canGoBack()) {
            mainWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private class MainWebViewClient extends WebViewClient {
        private boolean webViewSuccess = true;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            Log.d("WebViewClient", url);
            if (Uri.parse(url).getHost().equals(JariyoProperties.getInstance().url)) {

                // This is my web site, so do not override; let my WebView load the page
                return false;
            }

            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);

            errorWebView.setVisibility(View.VISIBLE);
            webViewSuccess = false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d("WebViewClient", "Page Reloading");
            loadingOverlay.setVisibility(View.VISIBLE);

            webViewSuccess = true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(webViewSuccess) {
                errorWebView.setVisibility(View.GONE);
            }

            if(JariyoProperties.getInstance().clearHistory) {
                mainWebView.clearHistory();
                JariyoProperties.getInstance().clearHistory = false;
            }
            loadingOverlay.setVisibility(View.GONE);
        }
    }


}
