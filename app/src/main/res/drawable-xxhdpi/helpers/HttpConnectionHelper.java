package com.itcrusaders.msaleh.helpers;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class HttpConnectionHelper {
    String response = "";
    URL url;
    HttpURLConnection conn = null;
    int responseCode = 0;

    Context mContext;

    public HttpConnectionHelper(Context context){
        this.mContext = context;
    }

    public String sendRequest(String path, HashMap<String, String> params) {
        try {
            Log.d("HttpConnectionHelper", "Starting process to connect path: " + path);
            url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("connection", "close");//Jellybean is having an issue on "Keep-Alive" connections
            conn.setReadTimeout(70000);
            conn.setConnectTimeout(70000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
        } catch (IOException ioe) {
            Log.d("HttpConnectionHelper", "Problem in getting connection.");
            response =  ioe.toString();
            //Toast.makeText(mContext,"HttpConnectionHelper!. Problem in getting connection.",//Toast.LENGTH_LONG).show();
            //ioe.printStackTrace();
        } catch (Exception e) {
            Log.d("HttpConnectionHelper", "Problem in getting connection. Safegaurd catch.");
            response =  e.toString();
            //Toast.makeText(mContext.getApplicationContext(),"HttpConnectionHelper!. Problem in getting connection. Safegaurd catch.", Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }

        OutputStream os = null;
        try {
            if (null != conn) {
                os = conn.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(params));
                writer.flush();
                writer.close();
                os.close();
                responseCode = conn.getResponseCode();
            }
        } catch(UnknownHostException e){
            return "No Internet Access";//Toast.makeText(mContext.getApplicationContext(),""+e.toString(),Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.d("HttpConnectionHelper",e.toString());
            return e.toString();
            //Log.d(mContext.getPackageName(),""+e.getMessage());
            //e.printStackTrace();
        }

        if (responseCode == HttpsURLConnection.HTTP_OK) {
            Log.d("HttpConnectionHelper", "Connection success to path: " + path);
            String line;
            BufferedReader br = null;

            //getting the reader instance from connection
            try {
                if (null != conn) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                }
            } catch (IOException e) {
                Log.d("HttpConnectionHelper", "Problem with opening reader.");
                response =  e.toString();
            //Toast.makeText(mContext,"Problem with opening reader.",//Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }

            //reading the response from stream
            try {
                if (null != br) {
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("HttpConnectionHelper", "output: " + line);
                    }
                }
            } catch (IOException e) {
                response = "";
                Log.d("HttpConnectionHelper", "Problem in extracting the result.");
                response =  e.toString();
                //Toast.makeText(mContext,"Problem in extracting the result.",//Toast.LENGTH_LONG).show();
                //e.printStackTrace();
            }
        } else if(responseCode == 404) {
            response = "An Error Occured";
        }else {

        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                //Log.d("HttpConnectionHelper", "entry.Key: " + entry.getKey());
                //Log.d("HttpConnectionHelper", "entry.Value: " + entry.getValue());
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        } catch (Exception e) {
            Log.d("HttpConnectionHelper", "Problem in getPostDataString while handling params."+e.toString());
            //Toast.makeText(mContext,"Problem in getPostDataString while handling params.",//Toast.LENGTH_LONG).show();
            //e.printStackTrace();
            return "";
        }

        return result.toString();
    }
}
