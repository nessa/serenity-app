package com.amusebouche.services;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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


/**
 * Service handler class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Class to handle all HTTP calls. It's responsible for making an HTTP call
 * and getting the response.
 */
public class ServiceHandler {

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    // Needed to abort
    private DefaultHttpClient mHttpClient;
    private HttpPost mHttpPost;
    private HttpGet mHttpGet;

    // TODO Set correct URL
    public final static String host = "http://10.0.240.21:8002/";


    /**
     * Basic constructor
     */
    public ServiceHandler() {}

    /**
     * Make service call. By default it won't send any parameters.
     * @param url URL to make request
     * @param method HTTP request method
     * */
    public String makeServiceCall(String url, int method) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return this.makeServiceCall(url, method, null);
        } else {
            return this.makeServiceCall(host + url, method, null);
        }
    }

    /**
     * Check internet connection
     * @param ctx App context
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
     * @param url URL to make request
     * @param method HTTP request method
     * @param params HTTP request params
     */
    public String makeServiceCall(String url, int method, List<NameValuePair> params) {

        try {
            // HTTP client
            mHttpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Check HTTP request method type
            if (method == POST) {
                mHttpPost = new HttpPost(host + url);
                // Add POST params
                if (params != null) {
                    mHttpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = mHttpClient.execute(mHttpPost);

            } else if (method == GET) {
                // Append params to URL
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url = host + url;
                    url += "?" + paramString;
                }
                mHttpGet = new HttpGet(url);

                httpResponse = mHttpClient.execute(mHttpGet);

            }
            if (httpResponse != null) {
                httpEntity = httpResponse.getEntity();
            }
            response = EntityUtils.toString(httpEntity, "UTF-8");

        } catch (UnsupportedEncodingException | ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Abort present requests and shutdown client
     */
    public void abort() {
        if (mHttpGet != null) {
            mHttpGet.abort();
        }
        if (mHttpPost != null) {
            mHttpPost.abort();
        }
        if (mHttpClient != null) {
            mHttpClient.getConnectionManager().shutdown();
        }
    }
}