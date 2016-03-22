package com.amusebouche.services;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Pair;

import com.amusebouche.amuseapp.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

    private Context mContext;

    // Needed to abort
    private DefaultHttpClient mHttpClient;
    private HttpPost mHttpPost;
    private HttpGet mHttpGet;

    // TODO Set correct URL
    public final static String host = "http://192.168.1.55/";


    /**
     * Basic constructor
     */
    public ServiceHandler(Context context) {
        mContext = context;
    }

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

    public String buildUrl(String base, int page, ArrayList<Pair<String, ArrayList<String>>> params) {
        base += String.format("%s%s%s%d", mContext.getString(R.string.API_GET_SEPARATOR),
                mContext.getString(R.string.API_PARAM_PAGE),
                mContext.getString(R.string.API_PARAM_EQUAL), page);

        if (params != null) {
            for (Pair item : params) {
                String key = (String) item.first;

                for (String value : (ArrayList<String>) item.second) {
                    base += mContext.getString(R.string.API_PARAM_SEPARATOR);

                    if (key.startsWith(mContext.getString(R.string.API_PARAM_DISLIKE_PREFIX))) {

                        if (key.equals(mContext.getString(R.string.API_PARAM_DISLIKE_CATEGORY))) {
                            base += mContext.getString(R.string.API_PARAM_CATEGORY);
                        }

                        if (key.equals(mContext.getString(R.string.API_PARAM_DISLIKE_INGREDIENT))) {
                            base += mContext.getString(R.string.API_PARAM_INGREDIENT);
                        }

                        base += mContext.getString(R.string.API_PARAM_NOT_EQUAL);
                    } else {
                        base += key;
                        base += mContext.getString(R.string.API_PARAM_EQUAL);
                    }

                    base += value;
                }
            }
        }

        Log.d("BASE", base);

        return base;
    }


    public String buildDetailUrl(String base, int id) {
        return String.format("%s%d", base, id);
    }

    /**
     * Check internet connection
     * */
    public Boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

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