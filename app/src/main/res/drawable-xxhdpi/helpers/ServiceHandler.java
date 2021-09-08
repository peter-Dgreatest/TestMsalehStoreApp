package com.itcrusaders.msaleh.helpers;

import android.util.Log;

import com.loopj.android.http.HttpGet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by EbukaProf on 04/10/2016.
 */
public class ServiceHandler {
    private static final String TAG = "ServiceHandler";
    static String response = null;
    static int responseCode = 0;
    public final static int GET = 1;
    public final static int POST = 2;
    static HttpResponse x=null;
    public ServiceHandler() {
    }

    public AuthorizationHttpResponse makeServiceCall(String url, int method, List<NameValuePair> params)
    {
        AuthorizationHttpResponse httpResponses = new AuthorizationHttpResponse();
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
            // Checking http request method type
            if (method == POST) {
                Log.e("in POST","in POST");
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    Log.e("in POST params","in POST params");
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                    httpPost.setHeader("Content-type", "application/json");
                }
                Log.e("url in post service",url);
                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // appending params to url
                Log.e("in GET","in GET");
                if (params != null) {
                    Log.e("in GET params","in GET params");
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                Log.e("url in get service", url);
                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);

            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            httpResponses.setResponseData(response);
            httpResponses.setResponseCode(responseCode);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "Response code from server : " + url+ " __ "+responseCode + ". response gotten from service:" + response);
        return httpResponses;
    }

    public String makeServiceCallIMAGE(String url, int method, List<NameValuePair> params) {
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }
                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
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

    public void postJsonBody(String url, JSONObject obj) {
        // Create a new HttpClient and Post Header

        HttpParams myParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(myParams, 10000);
        HttpConnectionParams.setSoTimeout(myParams, 10000);
        HttpClient httpclient = new DefaultHttpClient(myParams );
        String json=obj.toString();

        try {

            HttpPost httppost = new HttpPost(url.toString());
            httppost.setHeader("Content-type", "text/html"); //application/json

            StringEntity se = new StringEntity(obj.toString());
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(se);

            HttpResponse response = httpclient.execute(httppost);
            String temp = EntityUtils.toString(response.getEntity());
            Log.i("tag", temp);


        } catch (ClientProtocolException e) {

        } catch (IOException e) {
        }
    }

    public HttpResponse makeJsonCall(String url, JSONObject obj)
    {
        // Create a new HttpClient and Post Header

        HttpParams myParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(myParams, 10000);
        HttpConnectionParams.setSoTimeout(myParams, 10000);
        HttpClient httpclient = new DefaultHttpClient(myParams );
        String json=obj.toString();

        try {

            HttpPost httppost = new HttpPost(url.toString());
            httppost.setHeader("Content-type", "text/html"); //application/json

            StringEntity se = new StringEntity(obj.toString());
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(se);

            HttpResponse response = httpclient.execute(httppost);
            String temp = EntityUtils.toString(response.getEntity());
            Log.i("tag", temp);
     x=response;
        } catch (ClientProtocolException e) {

        } catch (IOException e) {
        }
        return x;

    }

    public AuthorizationHttpResponse makeServiceCallJSON(String url, int method, JSONObject params)
    {
        AuthorizationHttpResponse httpResponses = new AuthorizationHttpResponse();
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
            // Checking http request method type
            if (method == POST) {
                Log.e("in POST","in POST");
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    Log.e("in POST params","in POST params");

                    httpPost.setHeader("Content-type", "application/json");
                    httpPost.setHeader("Accept", "application/json");
                    //httpPost.setEntity(new UrlEncodedFormEntity(params));
                    httpPost.setEntity(new StringEntity(params.toString(), "UTF-8"));
                }
                Log.e("url in post service",url);
                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // appending params to url
                Log.e("in GET","in GET");
                if (params != null) {
                    Log.e("in GET params","in GET params");
                    //cannot use raw json for get method
                    //  String paramString = URLEncodedUtils
                      //       .format(params, "utf-8");
                    //url += "?" + paramString;
                }
                Log.e("url in get service", url);

                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);

            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            httpResponses.setResponseData(response);
            httpResponses.setResponseCode(responseCode);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "Response code from server :  " + url+ " __ "+responseCode + ". response gotten from service:" + response);
        return httpResponses;
    }



}
