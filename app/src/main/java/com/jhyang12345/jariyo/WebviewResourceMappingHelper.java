package com.jhyang12345.jariyo;

/**
 * Created by jhyan on 2018-03-06.
 */

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebResourceResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebviewResourceMappingHelper {
    private static WebviewResourceMappingHelper instance;
    private List<LocalAssetMapModel> localAssetMapModelList;
    private List<String> overridableExtensions = new ArrayList<>(Arrays.asList("js", "css", "png", "jpg", "woff", "ttf", "eot", "ico"));
    private static Context context;

    private WebviewResourceMappingHelper(Context context){
        this.context = context;
    }

    public static WebviewResourceMappingHelper getInstance(Context context){
        if(instance == null){
            instance = new WebviewResourceMappingHelper(context);
        }
        return instance;
    }

//    public String getLocalAssetPath(String url){
//        if(StringUtils.isEmpty(url)){
//            return "";
//        }
//        if(localAssetMapModelList == null){
//            localAssetMapModelList = getLocalAssetList();
//        }
//        if(CollectionUtils.isNotEmpty(localAssetMapModelList)){
//            for(LocalAssetMapModel localAssetMapModel : localAssetMapModelList){
//                if(localAssetMapModel.url.equals(url)){
//                    return localAssetMapModel.asset_url;
//                }
//            }
//        }
//        return "";
//    }

    public String getLocalFilePath(String url){
        String localFilePath = "";
        String fileNameForUrl = getLocalFileNameForUrl(url);
        if(StringUtils.isNotEmpty(fileNameForUrl) && fileExists(fileNameForUrl)){
            localFilePath = getFileFullPath(fileNameForUrl);
        }
        return localFilePath;
    }

    public String getLocalFileNameForUrl(String url){
        String localFileName = "";
        String[] parts = url.split("/");
        if(parts.length > 0){
            localFileName = parts[parts.length-1];
        }
        return localFileName;
    }

    private boolean fileExists(String fileName){
        String path = context
                .getFilesDir() + "/cart/" + fileName;
        return new File(path).exists();
    }

    private String getFileFullPath(String relativePath){
        return context.getFilesDir() + "/cart/" + relativePath;
    }


    public List<String> getOverridableExtensions(){
        return overridableExtensions;
    }

    public String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    public String getMimeType(String fileExtension){
        String mimeType = "";
        switch (fileExtension){
            case "css" :
                mimeType = "text/css";
                break;
            case "js" :
                mimeType = "text/javascript";
                break;
            case "png" :
                mimeType = "image/png";
                break;
            case "jpg" :
                mimeType = "image/jpeg";
                break;
            case "ico" :
                mimeType = "image/x-icon";
                break;
            case "woff" :
            case "ttf" :
            case "eot" :
                mimeType = "application/x-font-opentype";
                break;
        }
        return mimeType;
    }

    public static WebResourceResponse getWebResourceResponseFromAsset(String assetPath, String mimeType, String encoding) throws IOException{
        InputStream inputStream =  context.getAssets().open(assetPath);
        Log.d("Getting Resource", "Asset");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int statusCode = 200;
            String reasonPhase = "OK";
            Map<String, String> responseHeaders = new HashMap<String, String>();
            responseHeaders.put("Access-Control-Allow-Origin", "*");
            Log.d("Resource", "Successfully found resource!");
            return new WebResourceResponse(mimeType, encoding, statusCode, reasonPhase, responseHeaders, inputStream);
        }
        return new WebResourceResponse(mimeType, encoding, inputStream);
    }

    public static WebResourceResponse getWebResourceResponseFromFile(String filePath, String mimeType, String encoding) throws FileNotFoundException {
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int statusCode = 200;
            String reasonPhase = "OK";
            Map<String, String> responseHeaders = new HashMap<String, String>();
            responseHeaders.put("Access-Control-Allow-Origin","*");
            return new WebResourceResponse(mimeType, encoding, statusCode, reasonPhase, responseHeaders, fileInputStream);
        }
        return new WebResourceResponse(mimeType, encoding, fileInputStream);
    }

    private class LocalAssetMapModel{
        String url;
        String asset_url;
    }
}