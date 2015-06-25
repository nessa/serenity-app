package com.amusebouche.services;


/**
 * Service handler class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Class to handle all HTTP calls. It's responsible for making an HTTP call
 * and getting the response.
 */
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


public class ServiceHandler {

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    public final static String host = "http://10.0.240.21:8002/";


    public ServiceHandler() {

    }

    /**
     * Make service call. By default it won't send any parameters.
     * @url URL to make request
     * @method HTTP request method
     * */
    public String makeServiceCall(String url, int method) {
        Log.d("INFO", "Make service call 1");
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return this.makeServiceCall(url, method, null);
        } else {
            return this.makeServiceCall(host + url, method, null);
        }
    }

    /**
     * Check internet connection
     * @ctx App context
     * */
    public Boolean checkInternetConnection(Context ctx) {
        ConnectivityManager conMgr = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = conMgr.getActiveNetworkInfo();
        if (info == null)
            return false;
        if (!info.isConnected())
            return false;
        if (!info.isAvailable())
            return false;
        return true;
    }

    /**
     * Making service call
     * @url URL to make request
     * @method HTTP request method
     * @params HTTP request params
     * */
    public String makeServiceCall(String url, int method, List<NameValuePair> params) {
        Log.d("INFO", "Make service call 2");

        try {
            // HTTP client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Check HTTP request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(host + url);
                // Add POST params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // Append params to URL
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url = host + url;
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);

            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}