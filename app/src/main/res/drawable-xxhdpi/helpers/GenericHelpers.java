package com.itcrusaders.msaleh.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;


public class GenericHelpers {
    static final String HEXES = "0123456789ABCDEF";

    public static boolean checkInternetConenction(Context context) {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =(ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED ) {
            //Toast.makeText(context, " Connected ", Toast.LENGTH_LONG).show();
            return true;
        }else if (
                connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                        connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
                        connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED ) {
            return false;
        }
        return false;
    }
    public static String byteArrayToHexString(byte[] b){
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++){
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];
        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }

        return data;
    }

    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    public static boolean toggleGPS(Context context) {
        String provider = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps") == true || provider.contains("network")) {
            return true; // the GPS is already in the requested state
        }
        return false;

    }
    public static void askUserToOpenGPS(final Context mContext) {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(mContext);

        String provider = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.equals(""))
        {
            //GPS is enabled
            Toast.makeText(mContext, "GPS is enabled!", Toast.LENGTH_SHORT).show();
        }
        else {
            // Setting Dialog Title
            mAlertDialog.setTitle("Device Location Service not Active")
                    .setMessage("Activate Device Location before taking property Co-ordinates")
                    .setPositiveButton("Activate Location", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });
            mAlertDialog.show();
        }

    }
    public static String[] splitString(String whatToSplit, String determinant)
    {
        String[] result = whatToSplit.split(determinant);
        return result;
    }



}
