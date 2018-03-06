package com.jhyang12345.jariyo;

/**
 * Created by jhyan on 2018-02-14.
 */

public class JariyoProperties {
    private static JariyoProperties instance = null;
    public String url = "http://ec2-35-161-127-102.us-west-2.compute.amazonaws.com";
//    public String url = "http://59.13.200.8:8000";
    public boolean clearHistory = false;
    public boolean backButtonHandled = false;

    protected JariyoProperties(){}

    public static JariyoProperties getInstance() {
        if(instance == null) {
            instance = new JariyoProperties();
        }
        return instance;
    }

}
