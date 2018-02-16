package com.jhyang12345.jariyo;

/**
 * Created by jhyan on 2018-02-14.
 */

public class JariyoProperties {
    private static JariyoProperties instance = null;
//    public String url = "http://ec2-54-187-156-41.us-west-2.compute.amazonaws.com:8000/";
    public String url = "http://211.226.115.87:8000";

    protected JariyoProperties(){}

    public static JariyoProperties getInstance() {
        if(instance == null) {
            instance = new JariyoProperties();
        }
        return instance;
    }

}
