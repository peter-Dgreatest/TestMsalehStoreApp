package com.itcrusaders.msaleh.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by Hosanna_TechVibes on 04-Dec-17.
 */

public class MyJSON {


    //9999999999


    Context _context;
    String _email;
    private static final String TAG = "SyncDataAnalyzer";
    DataDB dataDB = new DataDB();

    Calendar timer = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

    public String AnalyzeAgentBalance(Context context, JSONArray jsonArray) {

        JSONArray responseJson = new JSONArray();
        StringBuffer myBufferedData = new StringBuffer();

        //check if we have data in jsonArray_ASSIGN
        if (jsonArray.length() > 0) {
            //repeat to get data from jsonarray
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject presentJSONObject = jsonArray.getJSONObject(i);
                    ContentValues table_builder = new ContentValues();


                    String userId =  presentJSONObject.getString("user_id");
                    String account =  presentJSONObject.getString("account");

                    float balance = Float.parseFloat(presentJSONObject.getString("balance"));

                    String synch_status =  presentJSONObject.getString("status");
                    String last_synched_date =  presentJSONObject.getString("last_update");
                    String authorizationID =  presentJSONObject.getString("payer_balance_id");
                    //String back_up =  presentJSONObject.getString("back_up");
                    String tax_id =  presentJSONObject.getString("tax_id");



                    ContentValues table_builder1 = new ContentValues();

                    table_builder1.put("idpayer_balance",authorizationID);
                    table_builder1.put("account",account);
                    table_builder1.put("balance",balance);
                    table_builder1.put("user_id",userId);


                    long lastInsertId1 = dataDB.myConnection(context).
                            onInsert(table_builder1,"initPayerBalance");

                    if (lastInsertId1>0) {

                        table_builder.put("idpayer_balance",authorizationID);
                        table_builder.put("tax_id",tax_id);
                        table_builder.put("account",account);
                        table_builder.put("balance",balance);
                        table_builder.put("synch_status",synch_status);
                        table_builder.put("last_synched_date",last_synched_date);
                        table_builder.put("authorization_id",authorizationID);
                        //table_builder.put("back_up",back_up);
                        table_builder.put("user_id",userId);

                        long lastInsertId = dataDB.myConnection(context).
                                onInsertOrUpdate(table_builder,"synch_payer_balance");
                        if (lastInsertId>0) {

                            Log.e("Apendin auth id list ", "for response  ");
                            myBufferedData.append(authorizationID + ",");
                            Log.e(TAG, i + " -synch_payerbalance Saved...");
                        }else {
                            Log.e("Apendin auth id list", "FAILED");

                        }

                    } else {
                        Log.e("Apendin auth id list", "FAILED");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        String revenuesBufferedDataToString = myBufferedData.toString();
        Log.e("buffer to string", "AFTER to string** " + revenuesBufferedDataToString);
        String replacedRevenueBufferedData = revenuesBufferedDataToString.replaceAll(",$", "");
        Log.e("Buffer reader", "AFTER string replace::" + replacedRevenueBufferedData);
        Log.e("Return to class", "Send employee info string back");

        return replacedRevenueBufferedData;
    }

    public String AnalyzeAssessments(Context context, JSONArray jsonArray) {

        JSONArray responseJson = new JSONArray();
        StringBuffer myBufferedData = new StringBuffer();

        //check if we have data in jsonArray_ASSIGN
        if (jsonArray.length() > 0) {
            //repeat to get data from jsonarray
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject presentJSONObject = jsonArray.getJSONObject(i);
                    ContentValues table_builder = new ContentValues();



                    table_builder.put("synchassessment_id", presentJSONObject.getString("id"));
                    table_builder.put("assessment_ref", presentJSONObject.getString("ref_no"));
                    table_builder.put("tax_payer_rin", presentJSONObject.getString("tin"));
                    table_builder.put("tax_payer_name", presentJSONObject.getString("taxpayer_name"));

                    //table_builder.put("description", presentJSONObject.getString("description"));//date_log
                    table_builder.put("assessment_amount", presentJSONObject.getString("amount"));
                    table_builder.put("assessment_amount_paid", presentJSONObject.getString("amount_paid"));
                    //table_builder.put("date_log", presentJSONObject.getString("date_log"));
                    //table_builder.put("day", presentJSONObject.getString("day"));
                    //                   table_builder.put("month", presentJSONObject.getString("month"));
                    String authorizationID = presentJSONObject.getString("id");
                    table_builder.put("service_id", presentJSONObject.getString("service_id"));//date_log
                    //                 table_builder.put("year", presentJSONObject.getString("year"));//date_log
                    table_builder.put("invoice_number", presentJSONObject.getString("invoice_number"));//date_log
                    table_builder.put("assessment_amount_remaining", presentJSONObject.getString("amount_remaining"));//date_log
                    table_builder.put("created_at", presentJSONObject.getString("registered_on"));//date_log
                    table_builder.put("assessment_date", presentJSONObject.getString("date_log"));//date_log

                    String synchassessment_id = presentJSONObject.getString("id");

                    // table_builder.put("back_up", back_up);
                    //table_builder.put("user_id", userId);

                    long lastInsertId = dataDB.myConnection(context).
                            onInsertOrUpdate(table_builder, "synch_assessments");
                    if (lastInsertId > 0) {

                        Log.e("Apendin auth id list ", "for response  ");
                        myBufferedData.append(synchassessment_id + ",");
                        Log.e(TAG, i + " -synch_assessments Saved...");
                    } else {
                        Log.e("Apendin auth id list", "FAILED");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        String revenuesBufferedDataToString = myBufferedData.toString();
        Log.e("buffer to string", "AFTER to string** " + revenuesBufferedDataToString);
        String replacedRevenueBufferedData = revenuesBufferedDataToString.replaceAll(",$", "");
        Log.e("Buffer reader", "AFTER string replace::" + replacedRevenueBufferedData);
        Log.e("Return to class", "Send synch_assessments info string back");

        return replacedRevenueBufferedData;
    }


    public String AnalyzeVehicles(Context context, JSONArray jsonArray) {

        JSONArray responseJson = new JSONArray();
        StringBuffer myBufferedData = new StringBuffer();

        //check if we have data in jsonArray_ASSIGN
        if (jsonArray.length() > 0) {
            //repeat to get data from jsonarray
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject presentJSONObject = jsonArray.getJSONObject(i);
                    ContentValues table_builder = new ContentValues();


                    String userId =  presentJSONObject.getString("user_id");
                    String vehicle_id =  presentJSONObject.getString("vehicle_id");
                    String vechicle_rin =  presentJSONObject.getString("vechicle_rin");
                    String vin =  presentJSONObject.getString("vin");
                    String vechicle_reg_no =  presentJSONObject.getString("vechicle_reg_no");
                    String service_id =  presentJSONObject.getString("service_id");
                    String taxpayer_rin =  presentJSONObject.getString("taxpayer_rin");
                    String taxpayer_name =  presentJSONObject.getString("taxpayer_name");
                    String registration_number =  presentJSONObject.getString("registration_number");
                    String organization_id =  presentJSONObject.getString("organization_id");
                    String synch_status =  presentJSONObject.getString("synch_status");
                    String last_synched_date =  presentJSONObject.getString("last_synched_date");
                    String back_up =  presentJSONObject.getString("back_up");
                    String authorizationID =  presentJSONObject.getString("synch_vehicle_id");

                    table_builder.put("id_synch_vehicle",authorizationID);
                    table_builder.put("vehicle_id",vehicle_id);
                    table_builder.put("vechicle_rin",vechicle_rin);
                    table_builder.put("vechicle_reg_no",vechicle_reg_no);
                    table_builder.put("service_id",service_id);
                    table_builder.put("taxpayer_rin",taxpayer_rin);
                    table_builder.put("taxpayer_name",taxpayer_name);
                    table_builder.put("registration_number",registration_number);
                    table_builder.put("organization_id",organization_id);
                    table_builder.put("vin",vin);
                    table_builder.put("synch_status",synch_status);
                    table_builder.put("last_synched_date",last_synched_date);
                    table_builder.put("authorization_id",authorizationID);
                    table_builder.put("back_up",back_up);
                    table_builder.put("user_id",userId);

                    long lastInsertId = dataDB.myConnection(context).
                            onInsertOrUpdate(table_builder,"synch_vehicles");
//                            Log.e(TAG, i+ " -Employee Saved...");
                    if (lastInsertId>0) {

                        Log.e("Apendin auth id list ", "for response  ");
                        myBufferedData.append(authorizationID + ",");
                        Log.e(TAG, i + " -synch_vehicles Saved...");

                    } else {
                        Log.e("Apendin auth id list", "FAILED");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        String revenuesBufferedDataToString = myBufferedData.toString();
        Log.e("buffer to string", "AFTER to string** " + revenuesBufferedDataToString);
        String replacedRevenueBufferedData = revenuesBufferedDataToString.replaceAll(",$", "");
        Log.e("Buffer reader", "AFTER string replace::" + replacedRevenueBufferedData);
        Log.e("Return to class", "Send employee info string back");

        return replacedRevenueBufferedData;
    }


    public String AnalyzeAssessmentItems(Context context, JSONArray jsonArray) {

        JSONArray responseJson = new JSONArray();
        StringBuffer myBufferedData = new StringBuffer();

        //check if we have data in jsonArray_ASSIGN
        if (jsonArray.length() > 0) {
            //repeat to get data from jsonarray
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject presentJSONObject = jsonArray.getJSONObject(i);
                    ContentValues table_builder = new ContentValues();

                    table_builder.put("assessment_item_id", presentJSONObject.getString("id"));
                    table_builder.put("id_synch_assessment_item", presentJSONObject.getString("id"));
//                    table_builder.put("item_ref", presentJSONObject.getString("ref_no"));
                    //table_builder.put("tax_payer_rin", presentJSONObject.getString("tin"));
                    table_builder.put("assessment_item_name", presentJSONObject.getString("revenue_name"));

                    //table_builder.put("description", presentJSONObject.getString("description"));//date_log
                    table_builder.put("tax_amount", presentJSONObject.getString("amount"));
                    //table_builder.put("assessment_amount_paid", presentJSONObject.getString("amount_paid"));
                    //table_builder.put("date_log", presentJSONObject.getString("date_log"));
                    //table_builder.put("day", presentJSONObject.getString("day"));
 //                   table_builder.put("month", presentJSONObject.getString("month"));
                    String authorizationID = presentJSONObject.getString("id");
                    table_builder.put("service_id", presentJSONObject.getString("service_id"));//date_log
   //                 table_builder.put("year", presentJSONObject.getString("year"));//date_log
                    //table_builder.put("invoice_number", presentJSONObject.getString("invoice_number"));//date_log
                    //table_builder.put("assessment_amount_remaining", presentJSONObject.getString("amount_remaining"));//date_log
                  //  table_builder.put("registered_on", presentJSONObject.getString("registered_on"));//date_log
                   // table_builder.put("assessment_date", presentJSONObject.getString("date_log"));//date_log



                         /*   //// TODO: 19/09/2015 Delete all records first
                            dataDB.myConnection(context).deleteRecords("admin_users");*/
                    // TODO: 19/09/2015 Call insert
                    long lastInsertId = dataDB.myConnection(context).onInsertOrUpdate(table_builder, "synch_assessment_items");
//                            Log.e(TAG, i+ " -Employee Saved...");
                    if (lastInsertId > 0) {

                        Log.i("Apendin auth id list ", "for response  ");
                        myBufferedData.append(authorizationID + ",");
                        Log.e(TAG, i + " -synch_apartment_types Saved...");

                    } else {
                        Log.i("Apendin auth id list", "FAILED");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        String revenuesBufferedDataToString = myBufferedData.toString();
        Log.e("buffer to string", "AFTER to string** " + revenuesBufferedDataToString);
        String replacedRevenueBufferedData = revenuesBufferedDataToString.replaceAll(",$", "");
        Log.e("Buffer reader", "AFTER string replace::" + replacedRevenueBufferedData);
        Log.e("Return to class", "Send employee info string back");

        return replacedRevenueBufferedData;
    }

}
