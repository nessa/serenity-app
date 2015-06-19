package com.amusebouche.services;


/**
 * Service handler class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Class to handle all HTTP calls. It's responsible for making an HTTP call
 * and getting the response.
 */
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

    public final static String host = "https://127.0.0.1/";


    public ServiceHandler() {

    }

    /**
     * Making service call. By default it won't send any parameters.
     * @url URL to make request
     * @method HTTP request method
     * */
    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(host + url, method, null);
    }

    /**
     * Making service call
     * @url URL to make request
     * @method HTTP request method
     * @params HTTP request params
     * */
    public String makeServiceCall(String url, int method, List<NameValuePair> params) {
        try {
            // HTTP client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking HTTP request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(host + url);
                // Adding POST params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // Appending params to URL
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
            response = EntityUtils.toString(httpEntity);

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