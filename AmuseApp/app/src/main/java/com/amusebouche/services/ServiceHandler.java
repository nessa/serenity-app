package com.amusebouche.services;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Pair;

import com.amusebouche.amuseapp.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Service handler class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Class to handle all HTTP calls. It's responsible for making an HTTP call
 * and getting the response.
 */
public class ServiceHandler {

    public final static int GET = 1;
    public final static int POST = 2;

    static String mResponse = null;
    static int mStatus = 0;

    private Context mContext;
    private SharedPreferences mPreferences;

    // Needed to abort
    private DefaultHttpClient mHttpClient;
    private HttpPost mHttpPost;
    private HttpGet mHttpGet;

    // TODO Set correct URL
    public final static String host = "http://192.168.1.54/";


    /**
     * Basic constructor
     */
    public ServiceHandler(Context context, SharedPreferences preferences) {
        mContext = context;
        mPreferences = preferences;
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

                    try {
                        base += URLEncoder.encode(value, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return base;
    }


    /**
     * Check internet connection
     * */
    public Boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = conMgr.getActiveNetworkInfo();
        return info != null && info.isConnected() && info.isAvailable();
    }

    /**
     * Making service call
     * @param url URL to make request
     * @param method HTTP request method
     * @param params HTTP request params
     */
    public String makeServiceCall(String url, int method, List<NameValuePair> params) {

        mStatus = 0;

        try {
            // HTTP client
            mHttpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            String token = mPreferences.getString(mContext.getString(R.string.preference_auth_token), "");

            // Check HTTP request method type
            if (method == POST) {
                mHttpPost = new HttpPost(host + url);
                // Add POST params
                if (params != null) {
                    mHttpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                if (!token.equals("")) {
                    mHttpPost.addHeader("Authorization", "Token " + token);
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

                if (!token.equals("")) {
                    mHttpGet.addHeader("Authorization", "Token " + token);
                }

                httpResponse = mHttpClient.execute(mHttpGet);
            }

            if (httpResponse != null) {
                httpEntity = httpResponse.getEntity();
                mStatus = httpResponse.getStatusLine().getStatusCode();
            }

            mResponse = EntityUtils.toString(httpEntity, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return mResponse;
    }

    public int getStatus() {
        return mStatus;
    }

    /**
     * Login
     * @param username Username credential
     * @param password Password credential
     * @return int Request status
     */
    public int login(String username, String password) {
        int status = 0;

        try {
            // HTTP client
            mHttpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse;

            // The POST request MUST end with "/"
            HttpPost httpPost = new HttpPost(host + "api-token-auth/");

            List <NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("username", username));
            nvps.add(new BasicNameValuePair("password", password));

            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            httpResponse = mHttpClient.execute(httpPost);
            if (httpResponse != null) {
                status = httpResponse.getStatusLine().getStatusCode();
                httpEntity = httpResponse.getEntity();
            }

            mResponse = EntityUtils.toString(httpEntity, "UTF-8");

            if (mResponse != null) {
                try {
                    JSONObject jObject = new JSONObject(mResponse);
                    String token = jObject.getString("token");

                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putString(mContext.getString(R.string.preference_auth_token),
                            token);
                    editor.putString(mContext.getString(R.string.preference_username),
                            username);
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mHttpClient.getConnectionManager().shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return status;
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