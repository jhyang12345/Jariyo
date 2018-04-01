package com.jhyang12345.jariyo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by jhyan on 2018-02-17.
 */

public class WebAppInterface {
    Context context;
//    Activity activityContext;
    WebView mainWebView;
    WebView errorWebView;

    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private boolean mLocationPermissionGranted;

    private Location mLastKnownLocation;

    final int INT_TO_CHECK = 100;

    private final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 101;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 102;

    WebAppInterface(Context c, WebView mainWebView, WebView errorWebView) {
        context = c;
        this.mainWebView = mainWebView;
        this.errorWebView = errorWebView;

        mGeoDataClient = Places.getGeoDataClient((Activity) context);
        mPlaceDetectionClient = Places.getPlaceDetectionClient((Activity) context);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient((Activity) context);

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


    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener((Activity) context, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if(mLastKnownLocation != null) {
                                double latitude = mLastKnownLocation.getLatitude();
                                double longitude = mLastKnownLocation.getLongitude();
                                Log.d("Location", String.valueOf(latitude));

                                mainWebView.evaluateJavascript("pageObject.addMyLocationFromAndroid(" +
                                        String.valueOf(latitude) + "," + String.valueOf(longitude) + ");",
                                        new ValueCallback<String>() {
                                            @Override
                                            public void onReceiveValue(String s) {
                                                Log.d("LogName", s); // Prints: "this"
                                            }
                                        });
                            } else {
                                Log.d("Location", "GPS Not working");

                                MaterialDialog.Builder dialog = new MaterialDialog.Builder(context)
                                        .title(R.string.gps_dialog_title)
                                        .content(R.string.gps_dialog_content)
                                        .positiveText(R.string.agree);
                                dialog.show();
                            }

                        } else {
                            Log.d("Location", "Current location is null. Using defaults.");
                            Log.e("Location", "Exception: %s", task.getException());
                        }
                    }
                });

            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission(Context context) {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        Log.d("Android", "Requesting Location");
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getDeviceLocation();
            Log.d("Android", "Permission granted");
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    @JavascriptInterface
    public Location requestDeviceLocation() {
        getLocationPermission(context);

        return null;
    }
}
