package com.jhyang12345.jariyo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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

    final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 101;
    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 102;

    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private GoogleApiClient googleApiClient;

    private boolean mLocationPermissionGranted;

    private Location mLastKnownLocation;

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

        mGeoDataClient = Places.getGeoDataClient(this);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

//        getLocationPermission();

//        getDeviceLocation();
    }



//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (googleApiClient != null) {
//            googleApiClient.connect();
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        googleApiClient.disconnect();
//        super.onStop();
//    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Location", "Received!");
//                    getDeviceLocation();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }

    }

    @Override
    public void onBackPressed() {
        if(JariyoProperties.getInstance().backButtonHandled) {
            Log.d("BackButtonHandled", "handled");
            mainWebView.evaluateJavascript("pageObject.closeOpenView()",
                    new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.d("LogName", s); // Prints: "this"
                        }
                    }
            );
        } else if (mainWebView.canGoBack()) {
            mainWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private class MainWebViewClient extends WebViewClient {
        private boolean webViewSuccess = true;
        String encoding = "UTF-8";

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            Log.d("WebViewClient", url);
//            if (Uri.parse(url).getHost().equals(JariyoProperties.getInstance().url)) {
//
//                // This is my web site, so do not override; let my WebView load the page
//                return false;
//            }

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

            // resetting backButtonHandled
            JariyoProperties.getInstance().backButtonHandled = false;

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



        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

            WebviewResourceMappingHelper helper = WebviewResourceMappingHelper.getInstance(MainActivity.this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String url = request.getUrl().toString();
                String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
                String mimeType;

//                Log.d("Intercepted request", fileName);
                if(fileName.endsWith(".woff")) {
                    Log.d("Font file found!", fileName);
                    mimeType = helper.getMimeType("woff");
                    try {
                        return WebviewResourceMappingHelper.getWebResourceResponseFromAsset("css/fonts/" + fileName, mimeType, encoding);
                    } catch (IOException e) {
                        Log.d("ResourceException", fileName);
                        return super.shouldInterceptRequest(view, request);
                    }

                } else if(fileName.endsWith(".ttf")) {
                    Log.d("Font file found!", fileName);
                    mimeType = helper.getMimeType("ttf");
                    try {
                        return WebviewResourceMappingHelper.getWebResourceResponseFromAsset("css/fonts/" + fileName, mimeType, encoding);
                    } catch (IOException e) {
                        Log.d("ResourceException", fileName);
                        return super.shouldInterceptRequest(view, request);
                    }
                } else if(fileName.endsWith(".otf")) {
                    Log.d("Font file found!", fileName);
                    mimeType = helper.getMimeType("otf");
                    try {
                        return WebviewResourceMappingHelper.getWebResourceResponseFromAsset("css/fonts/" + fileName, mimeType, encoding);
                    } catch (IOException e) {
                        Log.d("ResourceException", fileName);
                        return super.shouldInterceptRequest(view, request);
                    }
                } else if(fileName.endsWith(".css")) {
                    if(fileName.contains("font-awesome")) {
                        Log.d("Font file found!", fileName);
                        mimeType = helper.getMimeType("css");
                        try {
                            return WebviewResourceMappingHelper.getWebResourceResponseFromAsset("css/font-awesome/css/" + fileName, mimeType, encoding);
                        } catch (IOException e) {
                            Log.d("ResourceException", fileName);
                            return super.shouldInterceptRequest(view, request);
                        }
                    } else if(fileName.contains("fontawesome")) {
                        Log.d("Font file found!", fileName);
                        mimeType = helper.getMimeType("css");
                        try {
                            return WebviewResourceMappingHelper.getWebResourceResponseFromAsset("css/font-awesome/css/" + fileName, mimeType, encoding);
                        } catch (IOException e) {
                            Log.d("ResourceException", fileName);
                            return super.shouldInterceptRequest(view, request);
                        }
                    } else if(fileName.contains("bootstrap")) {
                        Log.d("Font file found!", fileName);
                        mimeType = helper.getMimeType("css");
                        try {
                            return WebviewResourceMappingHelper.getWebResourceResponseFromAsset("css/" + fileName, mimeType, encoding);
                        } catch (IOException e) {
                            Log.d("ResourceException", fileName);
                            return super.shouldInterceptRequest(view, request);
                        }
                    }
                } else if(fileName.endsWith(".js")) {
                    if(fileName.contains("bootstrap")) {
                        Log.d("Font file found!", fileName);
                        mimeType = helper.getMimeType("js");
                        try {
                            return WebviewResourceMappingHelper.getWebResourceResponseFromAsset("js/" + fileName, mimeType, encoding);
                        } catch (IOException e) {
                            Log.d("ResourceException", fileName);
                            return super.shouldInterceptRequest(view, request);
                        }
                    }
                }




            }

            return super.shouldInterceptRequest(view, request);

        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//            Log.d("Intercepted request 21", url);

            return super.shouldInterceptRequest(view, url);

        }

        public WebResourceResponse getWebResourceResponseFromAsset(String assetPath, String mimeType, String encoding) throws IOException {
            InputStream inputStream =  getAssets().open(assetPath);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int statusCode = 200;
                String reasonPhase = "OK";
                Map<String, String> responseHeaders = new HashMap<String, String>();
                responseHeaders.put("Access-Control-Allow-Origin", "*");
                return new WebResourceResponse(mimeType, encoding, statusCode, reasonPhase, responseHeaders, inputStream);
            }
            return new WebResourceResponse(mimeType, encoding, inputStream);
        }
    }


}
