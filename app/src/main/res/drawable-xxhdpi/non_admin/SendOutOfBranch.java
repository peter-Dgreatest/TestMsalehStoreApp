package com.itcrusaders.msaleh.non_admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itcrusaders.msaleh.AdminDashboard;
import com.itcrusaders.msaleh.database.DeliveryDAO;
import com.itcrusaders.msaleh.LoginActivity;
import com.itcrusaders.msaleh.R;
import com.itcrusaders.msaleh.helpers.App;
import com.itcrusaders.msaleh.helpers.HttpConnectionHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class SendOutOfBranch extends AppCompatActivity {

    EditText EedtSenderName,EdedtSenddermail,EedtSenderphone,Eedtvcode;
    String deliveryId;
    DeliveryDAO enrollDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_out_of_branch2);

        App app = (App) getApplicationContext();

        if(app.getAccount_type().length()<2){
            startActivity(new Intent(this, LoginActivity.class));
        }

        TextView TUserType = findViewById(R.id.tv_loginType);
        TextView TUsername = findViewById(R.id.tv_userName);

        TUsername.setText(app.getFirstname());
        TUserType.setText(app.getAccount_type());

        EedtSenderName= findViewById(R.id.edt_senderName);
        EedtSenderphone= findViewById(R.id.edt_sender_phone);
        EdedtSenddermail= findViewById(R.id.edt_sender_mail);
        Eedtvcode= findViewById(R.id.edt_receiverVcode);



        enrollDAO= (DeliveryDAO) getIntent().getSerializableExtra("deliveryinfo");
        deliveryId=enrollDAO.getDeliveryId();


        new RequestVerifyOnline().execute();



        findViewById(R.id.btn_finish_frag1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new VerifyOnline().execute();
            }
        });
    }

    class VerifyOnline extends AsyncTask<Void, Void, Void> {

        HttpConnectionHelper helper;
        HashMap<String, String> postDataParams ;
        int suc =0;
        String newDeliveryId="",response;

        JSONArray restulJsonArray;
        JSONObject returnJSONOutput = new JSONObject();


        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SendOutOfBranch.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Registering product!\r\nPlease Wait");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            helper = new HttpConnectionHelper(getApplicationContext());
            try {
                suc = 1;

                HashMap<String, String> contentValues = new HashMap<String, String>();
                contentValues.put("vcode",Eedtvcode.getText().toString());
                contentValues.put("senderName",EedtSenderName.getText().toString());
                contentValues.put("senderphone",EedtSenderphone.getText().toString());
                contentValues.put("deliveryid",enrollDAO.getDeliveryId());
                contentValues.put("sendermail",EdedtSenddermail.getText().toString());


//                response = helper.sendRequest(getString(R.string.senddelivery),contentValues);
                response = helper.sendRequest(getString(R.string.verifyCodeR),contentValues);
                Log.e("response",response+";;;"+contentValues.toString());

            } catch (Exception ex) {
                suc = 0;
                Log.e("error", ex.toString());
            }
            return null;
        }

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
                if (!response.isEmpty() || response.length() > 2 && !(response.contains("Already Registered"))) {
                    //ArrayAdapter listViewAdapter = new ArrayAdapter<String>(mContext, R.layout.mobile_name_listview);
                    try {
                        if(response.contains("Error Occured")){

                        }else if(response.contains("updated successfully")){
                            Toast.makeText(getApplicationContext(),"Success", Toast.LENGTH_LONG).show();try {
                                Thread.sleep(7 * 1000);

                                startActivity(new Intent(SendOutOfBranch.this, AdminDashboard.class));
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else{
                if(response.equalsIgnoreCase("No Internet Access")
                        || response.contains("Failed to connect")){
                    AlertDialog.Builder builder
                            = new AlertDialog.Builder(getApplicationContext());
                    builder.setMessage("No Internet Access");
                    builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new VerifyOnline().execute();
                        }
                    });
                    builder.show();
                }
            }
        }
    }


    class RequestVerifyOnline extends AsyncTask<Void, Void, Void> {

        HttpConnectionHelper helper;
        HashMap<String, String> postDataParams ;
        int suc =0;
        String newDeliveryId="",response;

        @Override
        protected Void doInBackground(Void... voids) {
            helper = new HttpConnectionHelper(getApplicationContext());
            try {
                suc = 1;

                HashMap<String, String> contentValues = new HashMap<String, String>();
                contentValues.put("deliveryId",deliveryId);
                contentValues.put("receiverMail",enrollDAO.getReceivermail());


//                response = helper.sendRequest(getString(R.string.senddelivery),contentValues);
                response = helper.sendRequest(getString(R.string.sendreceiverVcode),contentValues);

                Log.e("response",response+";;;;"+contentValues);
            } catch (Exception ex) {
                suc = 0;
                Log.e("error", ex.toString());
            }
            return null;
        }
    }

}