package com.jhyang12345.jariyo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Created by jhyan on 2018-02-17.
 */

public class WebAppInterface {
    Context context;
//    Activity activityContext;
    WebView mainWebView;
    WebView errorWebView;

    final int INT_TO_CHECK = 100;

    WebAppInterface(Context c, WebView mainWebView, WebView errorWebView) {
        context = c;
        this.mainWebView = mainWebView;
        this.errorWebView = errorWebView;

    }

    @JavascriptInterface
    public void retryConnection() {
        this.errorWebView.post(new Runnable() {
            @Override
            public void run() {
            }
        });
        this.mainWebView.post(new Runnable() {
            @Override
            public void run() {
                mainWebView.loadUrl(JariyoProperties.getInstance().url);
            }
        });

        Log.d("ReloadWebView", JariyoProperties.getInstance().url);
    }

    @JavascriptInterface
    public void clearHistory() {
        JariyoProperties.getInstance().clearHistory = true;
        Log.d("clearHistory", "called");
    }

    @JavascriptInterface
    public void handleBackButton() {
        JariyoProperties.getInstance().backButtonHandled = false;
    }

    @JavascriptInterface
    public void setHandleBackButton() {
        JariyoProperties.getInstance().backButtonHandled = true;
    }

    @JavascriptInterface
    public void invokeCall(String phoneNumber) {
        if ( ContextCompat.checkSelfPermission( context, Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions((Activity) context, new String[] {
                    android.Manifest.permission.CALL_PHONE  }, INT_TO_CHECK
                    );
        } else {
            // permission was granted, yay!
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));
            context.startActivity(intent);
        }
    }
}
