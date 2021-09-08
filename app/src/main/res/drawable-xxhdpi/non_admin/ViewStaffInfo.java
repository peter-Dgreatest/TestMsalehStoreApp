package com.itcrusaders.msaleh.non_admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itcrusaders.msaleh.database.EnrollDAO;
import com.itcrusaders.msaleh.Enroll;
import com.itcrusaders.msaleh.LoginActivity;
import com.itcrusaders.msaleh.R;
import com.itcrusaders.msaleh.helpers.App;
import com.itcrusaders.msaleh.helpers.HttpConnectionHelper;

import java.io.InputStream;
import java.util.HashMap;

public class ViewStaffInfo extends AppCompatActivity {

    String staffId;
    EnrollDAO enrollDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_staff_info);

        App app = (App) getApplicationContext();

        if(app.getAccount_type().length()<2){
            startActivity(new Intent(this, LoginActivity.class));
        }

        TextView TUserType = findViewById(R.id.tv_loginType);
        TextView TUsername = findViewById(R.id.tv_userName);

        TUsername.setText(app.getFirstname());
        TUserType.setText(app.getAccount_type());

        enrollDAO = (EnrollDAO) getIntent().getSerializableExtra("staffId");
        getIds();
        setInfo();
        new getStaffImgOnline().execute();

        findViewById(R.id.btn_edit_staff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewStaffInfo.this, Enroll.class).putExtra("staffId",enrollDAO.getStaffId()).putExtra("enrollDAO",enrollDAO));
            }
        });
    }

    TextView mstaffName,mstaffBranch,mstaffEmail,mstaffAddress,mstaffNxtOfKin,mstaffGuarantor;
    ImageView imageView;

    private void getIds() {
        mstaffName = findViewById(R.id.staffName);
        mstaffBranch = findViewById(R.id.staffbranch);
        mstaffEmail = findViewById(R.id.staffEmail);
        mstaffAddress = findViewById(R.id.staffAddress);
        mstaffNxtOfKin = findViewById(R.id.staffNxtOfKin);
        mstaffGuarantor = findViewById(R.id.staffGuarantor);

        imageView = findViewById(R.id.imageView);



    }


    private Bitmap getImage(String img) {
        byte[] decoded64String = android.util.Base64.decode(img, android.util.Base64.NO_WRAP);
        Bitmap bmp = BitmapFactory.decodeByteArray(decoded64String, 0, decoded64String.length);
        int screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = this.getResources().getDisplayMetrics().heightPixels;
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Notice that width and height are reversed //screenHeight, screenWidth,
            Bitmap scaled = Bitmap.createScaledBitmap(bmp, 150, 150, true);
            int w = scaled.getWidth();
            int h = scaled.getHeight();
            // Setting post rotate to 90
            Matrix mtx = new Matrix();
            mtx.postRotate(0);
            // Rotating Bitmap
            bmp = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
        } else {// LANDSCAPE MODE
            //No need to reverse width and height
            Bitmap scaled = Bitmap.createScaledBitmap(bmp, screenWidth, screenHeight, true);
            bmp = scaled;
        }

        return bmp;
    }

    class getStaffImgOnline extends AsyncTask<Void, Void, Void> {

        HttpConnectionHelper helper;
        HashMap<String, String> postDataParams;
        int suc =0;
        String response;

        @Override
        protected Void doInBackground(Void... voids) {
            helper = new HttpConnectionHelper(getApplicationContext());
            try {
                suc =1;

                postDataParams = new HashMap<String, String>();
                postDataParams.put("HTTP_ACCEPT", "application/json");
                postDataParams.put("staffId", enrollDAO.getStaffId());
                response = helper.sendRequest(getString(R.string.getStaffImage),postDataParams);
            }catch(Exception ex){
                suc=0;
                Log.e("error",ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (suc == 1) {
                try {
                    imageView.setImageBitmap(getImage(response));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                if(response.equalsIgnoreCase("No Internet Access")
                        || response.contains("Failed to connect")){
                    AlertDialog.Builder builder
                            = new AlertDialog.Builder(ViewStaffInfo.this);
                    builder.setMessage("No Internet Access");
                    builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            imageView.setImageBitmap(getBitmapFromUrl("https://thesmsplace.com.ng/msalah/api/staffphoto/"+enrollDAO.getStaffId()+".jpg"));
                          //  new getStaffImgOnline().execute();
                        }
                    });
                    builder.show();
                }
            }
        }
    }

    private void setInfo() {
        mstaffEmail.setText(enrollDAO.getStaffMail());
        mstaffName.setText(enrollDAO.getStaffName());
        mstaffAddress.setText(enrollDAO.getStaffaddress());
        mstaffBranch.setText(enrollDAO.getBranch());
        mstaffNxtOfKin.setText(enrollDAO.getNxtOfKinName()+"   "+enrollDAO.getNxtOfKinAddress());
        mstaffEmail.setText(enrollDAO.getGuarantorName()+"   "+enrollDAO.getGuarantorPhone1());
    }

    public static Bitmap getBitmapFromUrl(String filePath)
    {
        Bitmap img = null;

        try{
            InputStream inputStream = new java.net.URL(filePath).openStream();
            img = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.d("dddfff",e.toString());
        }

        return img;
    }

}