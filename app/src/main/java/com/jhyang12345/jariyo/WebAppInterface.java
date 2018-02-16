package com.jhyang12345.jariyo;

import android.content.Context;
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

    WebAppInterface(Context c) {
        context = c;

    }

    @JavascriptInterface
    public void retryConnection() {
        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
    }
}
