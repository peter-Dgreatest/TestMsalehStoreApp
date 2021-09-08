package com.itcrusaders.msaleh.non_admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.itcrusaders.msaleh.database.DeliveryDAO;
import com.itcrusaders.msaleh.LoginActivity;
import com.itcrusaders.msaleh.R;
import com.itcrusaders.msaleh.SplashActivity;
import com.itcrusaders.msaleh.helpers.App;
import com.itcrusaders.msaleh.helpers.DataDB;
import com.itcrusaders.msaleh.helpers.HttpConnectionHelper;
import com.itcrusaders.msaleh.non_admin.adapters.DeliveryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class StaffDeliveries extends AppCompatActivity implements DeliveryAdapter.StaffDeliveriesOnClickHandler {

    App app;
    DataDB dataDB;

    ArrayList<DeliveryDAO> deliveryDAOS,deliveryDAOS2;
    DeliveryAdapter inventoryAdapter;
    RecyclerView recyclerView,recyclerView2;
    TextView pending_delivery_tag,delivered_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_deliveries);

        app = (App) getApplicationContext();

        if(app.getAccount_type().length()<2){
            startActivity(new Intent(this, LoginActivity.class));
        }

        TextView TUserType = findViewById(R.id.tv_loginType);
        TextView TUsername = findViewById(R.id.tv_userName);

        TUsername.setText(app.getFirstname());
        TUserType.setText(app.getAccount_type());

        findViewById(R.id.tv_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("com.itcrusaders.msaleh",
                        MODE_PRIVATE);

                sharedPreferences.edit().clear().apply();

                startActivity(new Intent(StaffDeliveries.this, SplashActivity.class));

                StaffDeliveries.this.finish();

            }
        });
        app = (App) getApplication();
        dataDB = new DataDB();
        deliveryDAOS = new ArrayList<DeliveryDAO>();



        Cursor cursor = dataDB.myConnection(getApplicationContext())
                .selectAllFromTable("select * from deliveries where attendingStaffId = '"+app.getActiveEmp_no()+"'",true);

        if(cursor.moveToFirst()){
            do{
                DeliveryDAO deliveryDAO = new DeliveryDAO();
                deliveryDAO.setAttendingStaffId(app.getActiveEmp_no());
                deliveryDAO.setDeliveryId(cursor.getString(cursor.getColumnIndex("delivery_id")));
                deliveryDAO.setDeliveryId(cursor.getString(cursor.getColumnIndex("pickupaddress")));
                deliveryDAO.setDeliveryId(cursor.getString(cursor.getColumnIndex("pickupbranch")));
                deliveryDAO.setDeliveryId(cursor.getString(cursor.getColumnIndex("clientname")));
                deliveryDAO.setDeliveryId(cursor.getString(cursor.getColumnIndex("equipmentbarcode")));
                deliveryDAO.setDeliveryId(cursor.getString(cursor.getColumnIndex("receivername")));
                deliveryDAO.setApprovedby(cursor.getString(cursor.getColumnIndex("approvedby")));

                deliveryDAOS.add(deliveryDAO);
            }while(cursor.moveToNext());
        }

        new LoadFromOnline().execute();

        recyclerView= findViewById(R.id.pendingRecyclerView);
        recyclerView2= findViewById(R.id.deliveredRecyclerView);
        inventoryAdapter = new DeliveryAdapter(this,deliveryDAOS,this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView2.setLayoutManager(linearLayoutManager1);

        recyclerView.setAdapter(inventoryAdapter);

        recyclerView.setHasFixedSize(true);

        inventoryAdapter.setMessageData(deliveryDAOS);
        inventoryAdapter.notifyDataSetChanged();

        delivered_tag= findViewById(R.id.delivered_tag);
        pending_delivery_tag= findViewById(R.id.pending_delivery_tag);


        pending_delivery_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inventoryAdapter.setMessageData(inventoryDAOS2);
                inventoryAdapter.notifyDataSetChanged();
                pending_delivery_tag.setBackgroundResource(R.drawable.buttonshapebackground4_2);
                delivered_tag.setBackgroundResource(R.drawable.buttonshapebackground5);

            }
        });

        delivered_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inventoryAdapter.setMessageData(inventoryDAOS);
                inventoryAdapter.notifyDataSetChanged();
                pending_delivery_tag.setBackgroundResource(R.drawable.buttonshapebackground5);
                delivered_tag.setBackgroundResource(R.drawable.buttonshapebackground4_2);
            }
        });


    }

    @Override
    public void clickHandler(String Params) {
        if(!inventoryDAOS.get(Integer.parseInt(Params)).getApprovedby().equalsIgnoreCase("1")) {
            startActivity(new Intent(StaffDeliveries.this, SendStaffDelivery.class).putExtra("deliveryId", inventoryDAOS.get(Integer.parseInt(Params))));
        }else{
            Toast.makeText(getApplicationContext(),"Cant make changes to successfull deliveries", Toast.LENGTH_LONG).show();
        }
    }


    int suc =0;
    ProgressDialog progressDialog;

   // InventoryAdapter inventoryAdapter;
    ArrayList<DeliveryDAO> inventoryDAOS,inventoryDAOS2;


    private class LoadFromOnline extends AsyncTask<Void, Void, Void> {


        JSONArray restulJsonArray,restulJsonArray2;
        JSONObject returnJSONOutput = new JSONObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(StaffDeliveries.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Retrieving products!\r\nPlease Wait");
            progressDialog.show();
        }


        HttpConnectionHelper helper;
        HashMap<String, String> postDataParams ;

        @Override
        protected Void doInBackground(Void... voids) {
            helper = new HttpConnectionHelper(getApplicationContext());
            try {
                suc =1;
                SharedPreferences sharedPreferences = getSharedPreferences("com.itcrusaders.msaleh",
                        MODE_PRIVATE);

                postDataParams = new HashMap<String, String>();
                postDataParams.put("HTTP_ACCEPT", "application/json");
                postDataParams.put("user_id", sharedPreferences.getString("userxx_id",null));


                response = helper.sendRequest(getString(R.string.getDelivery),postDataParams);
                Log.e("response",response+";;;"+postDataParams.toString());
                JSONObject resultJsonObject = new JSONObject(response);


                restulJsonArray = resultJsonObject.getJSONArray("output");

            }catch(Exception ex){
                suc=0;
                Log.e("error",ex.toString());
            }
            return null;
        }

        String response;

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing()) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {

                }
            }
            if (suc == 1) {
                if (null != restulJsonArray) {
                    inventoryDAOS = new ArrayList<>();
                    inventoryDAOS2 = new ArrayList<>();
                    //ArrayAdapter listViewAdapter = new ArrayAdapter<String>(mContext, R.layout.mobile_name_listview);
                    for (int i = 0; i < restulJsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject = restulJsonArray.getJSONObject(i);
                            Log.d("JSONINFO",jsonObject.toString()+"dddd");
                            DeliveryDAO messagesDAO =
                                    new DeliveryDAO();


                            messagesDAO.setAttendingStaffId(jsonObject.get("attendingStaffId").toString());
                            messagesDAO.setEquipmentbarcode(jsonObject.get("equipmentbarcode").toString());
                            messagesDAO.setClientname(jsonObject.get("clientname").toString());
                            messagesDAO.setDeliveryId(jsonObject.get("delivery_id").toString());
                            messagesDAO.setClientaddress(jsonObject.get("clientaddress").toString());
                            messagesDAO.setPurchasedate(jsonObject.get("purchasedate").toString());
                            messagesDAO.setDeliveryinstructions(jsonObject.get("deliveryinstructions").toString());
                            messagesDAO.setOfficialmail(jsonObject.get("officialmail").toString());
                            messagesDAO.setClientphone(jsonObject.get("clientphone").toString());
                            messagesDAO.setAltphone(jsonObject.get("altphone").toString());
                            messagesDAO.setReceiptno(jsonObject.get("receiptno").toString());
                            messagesDAO.setPickupbranch(jsonObject.get("pickupbranch").toString());
                            messagesDAO.setPickupaddress(jsonObject.get("pickupaddress").toString());
                            messagesDAO.setReceivername(jsonObject.get("receivername").toString());
                            messagesDAO.setReceivermail(jsonObject.get("receivermail").toString());
                            messagesDAO.setReceiverdesignation(jsonObject.get("receiverdesignation").toString());
                            messagesDAO.setApprovedby(jsonObject.get("approvedby").toString());
                            //    messagesDAO.setBarcode(jsonObject.get("barcode").toString());

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("delivery_id",messagesDAO.getDeliveryId());
                            Log.e("hjg",messagesDAO.getPickupaddress()+";;;;");

                            new DataDB().myConnection(getApplicationContext()).onInsertOrUpdate(contentValues,"deliveries");

                            if(messagesDAO.getApprovedby().equalsIgnoreCase("1")) {
                                inventoryDAOS.add(messagesDAO);
                                Log.e("hjgg",messagesDAO.getPickupaddress()+";;;;");
                            }else {
                                inventoryDAOS2.add(messagesDAO);
                                Log.e("hjg",messagesDAO.getPickupaddress()+";;;;");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("hjgg",e.getMessage()+";;;;");
                        }
                    }
                }
            }else{
                if(response.equalsIgnoreCase("No Internet Access")
                        || response.contains("Failed to connect")){
                    AlertDialog.Builder builder
                            = new AlertDialog.Builder(StaffDeliveries.this);
                    builder.setMessage("No Internet Access");
                    builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new LoadFromOnline().execute();
                        }
                    });
                    builder.show();
                }
            }
            setMessageData();
        }
    }



    private void setMessageData() {
        this.inventoryAdapter.setMessageData(this.inventoryDAOS2);
//        for (DeliveryDAO dao: inventoryDAOS) {
          //  Log.e("info",dao.getName());
  //      }
        this.inventoryAdapter.notifyDataSetChanged();
    }

}