package com.jhyang12345.jariyo;

import android.content.Context;
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
    WebView mainWebView;
    WebView errorWebView;

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
}
