package com.itcrusaders.msaleh.helpers;

/**
 * Created by EbukaProf on 29/09/2016.
 */
public class AuthorizationHttpResponse {
    int responseCode;
    String responseData;
    byte[] responseByteData;

    public void setResponseCode(int _responseCode)
    {
        this.responseCode = _responseCode;
    }
    public int getResponseCode()
    {
        return responseCode;
    }

    public void setResponseData(String _responseData)
    {
        this.responseData = _responseData;
    }
    public String getResponseData()
    {
        return responseData;
    }

    public byte[] getResponseByteData() {
        return responseByteData;
    }

    public void setResponseByteData(byte[] responseByteData) {
        this.responseByteData = responseByteData;
    }
}
