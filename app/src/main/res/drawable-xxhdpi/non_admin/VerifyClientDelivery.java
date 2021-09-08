package com.itcrusaders.msaleh.non_admin;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itcrusaders.msaleh.database.DeliveryDAO;
import com.itcrusaders.msaleh.LoginActivity;
import com.itcrusaders.msaleh.R;
import com.itcrusaders.msaleh.bioofly.MainActivityPrint2;
import com.itcrusaders.msaleh.helpers.App;
import com.itcrusaders.msaleh.helpers.DataDB;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class VerifyClientDelivery extends AppCompatActivity {

    EditText EdReceiveName,EdReceiverPhone,Edreceivermail;
    ImageView ImgPhoto1,ImgPhoto2,ImgPhoto3;
    App app;
    DataDB dataDB;
    String deliveryId;

    DeliveryDAO deliveryDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_client_delivery);

        app = (App) getApplicationContext();

        if(app.getAccount_type().length()<2){
            startActivity(new Intent(this, LoginActivity.class));
            VerifyClientDelivery.this.finish();
        }

        TextView TUserType = findViewById(R.id.tv_loginType);
        TextView TUsername = findViewById(R.id.tv_userName);

        TUsername.setText(app.getFirstname());
        TUserType.setText(app.getAccount_type());

        deliveryDAO = (DeliveryDAO) getIntent().getSerializableExtra("deliveryId");

        deliveryId = deliveryDAO.getDeliveryId();

        dataDB = new DataDB();

        EdReceiveName = findViewById(R.id.edt_rename);
        EdReceiverPhone = findViewById(R.id.edt_gphone);
        Edreceivermail = findViewById(R.id.edt_gemail);

        ImgPhoto1 = findViewById(R.id.buttonPhoto1);
        ImgPhoto2 = findViewById(R.id.buttonPhoto2);
        ImgPhoto3 = findViewById(R.id.buttonPhoto3);


        ImgPhoto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePic(requestCode1,requestCodeGallery1);
            }
        });

        ImgPhoto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePic(requestCode2,requestCodeGallery2);
            }
        });

        ImgPhoto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(SendOutOfBranch.this, MainActivityPrint2.class)
                        .putExtra("deliveryId",deliveryId));
              //  changePic(requestCode3,requestCodeGallery3);
            }
        });

        findViewById(R.id.btn_nxt_frag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int com = 0;
                ContentValues contentValues = new ContentValues();
                deliveryDAO.setReceivermail(Edreceivermail.getText().toString());
                deliveryDAO.setReceivername(EdReceiveName.getText().toString());
                deliveryDAO.setReceiverphone1(EdReceiverPhone.getText().toString());

                contentValues.put("receivername",EdReceiveName.getText().toString());
                contentValues.put("receiverphone1",EdReceiverPhone.getText().toString());
                contentValues.put("receivermail",Edreceivermail.getText().toString());

                long suc = dataDB.myConnection(getApplicationContext()).onUpdateOrIgnore(contentValues,"deliveries","delivery_id",deliveryId);

                if(suc>0) {
                    Cursor cursor = dataDB.myConnection(getApplicationContext()).selectAllFromTable("select *  from deliveries where delivery_id = '" + deliveryId + "'",
                            true);
                    if (cursor.moveToFirst()) {
                        com = Integer.parseInt(cursor.getString(cursor.getColumnIndex("letter"))) + Integer.parseInt(cursor.getString(cursor.getColumnIndex("rphoto")))
                                + Integer.parseInt(cursor.getString(cursor.getColumnIndex("rprint")));
                    }
                    if (com >= 2) {
                        findViewById(R.id.layout2show).setVisibility(View.GONE);
                        findViewById(R.id.deliveryStatusInfo).setVisibility(View.VISIBLE);

                        try {
                            Thread.sleep(7 * 1000);

                            startActivity(new Intent(VerifyClientDelivery.this, SendOutOfBranch.class).putExtra("deliveryinfo",deliveryDAO));
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Complete Receiver's Verification First", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private final int requestCode1 = 10000;
    private final int requestCode2 = 10001;
    private final int requestCode3 = 10002;
    private static final int requestCodeGallery1 = 2871;
    private static final int requestCodeGallery2 = 2872;
    private static final int requestCodeGallery3 = 2873;

    private void changePic(final int r1, final int rg1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(VerifyClientDelivery.this);
        builder.setMessage("Set action to perform");
        builder.setNegativeButton("Take Picture", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent photoCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCapture,r1);
            }
        });
        builder.setPositiveButton("Pick from Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent photoCapture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(photoCapture,rg1);
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        String tabletoupdate = "";
        String col2Update = "";
        if(this.requestCode1== requestCode && resultCode == RESULT_OK){
            bitmap = (Bitmap) data.getExtras().get("data");
            ImgPhoto1.setImageBitmap(bitmap);
            tabletoupdate="delivery_letters";
            col2Update="letter";
        }else if(this.requestCodeGallery1==requestCode && data!=null){
            InputStream inputStream = null;
            try {
                Uri uri = data.getData();
                inputStream = VerifyClientDelivery.this.getContentResolver().openInputStream(data.getData());

                bitmap = BitmapFactory.decodeStream(inputStream);

                ImgPhoto1.setImageBitmap(bitmap);
                tabletoupdate="delivery_letters";
                col2Update="letter";
                //            Picasso.get(ImageSelector.this).load(uri).fit().centerCrop().into(imageView);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }else if(this.requestCode2== requestCode && resultCode == RESULT_OK){
            bitmap = (Bitmap) data.getExtras().get("data");
            ImgPhoto2.setImageBitmap(bitmap);
            tabletoupdate="delivery_receivers_photo";
            col2Update="rphoto";
        }else if(this.requestCodeGallery2==requestCode && data!=null){
            InputStream inputStream = null;
            try {
                Uri uri = data.getData();
                inputStream = VerifyClientDelivery.this.getContentResolver().openInputStream(data.getData());

                bitmap = BitmapFactory.decodeStream(inputStream);

                ImgPhoto2.setImageBitmap(bitmap);
                tabletoupdate="delivery_receivers_photo";
                col2Update="rphoto";
                //            Picasso.get(ImageSelector.this).load(uri).fit().centerCrop().into(imageView);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }else if(this.requestCode3== requestCode && resultCode == RESULT_OK){
            bitmap = (Bitmap) data.getExtras().get("data");
            ImgPhoto3.setImageBitmap(bitmap);
            tabletoupdate="delivery_receivers_print";
            col2Update="rprint";
        }else if(this.requestCodeGallery3==requestCode && data!=null){
            InputStream inputStream = null;
            try {
                Uri uri = data.getData();
                inputStream = VerifyClientDelivery.this.getContentResolver().openInputStream(data.getData());

                bitmap = BitmapFactory.decodeStream(inputStream);

                ImgPhoto3.setImageBitmap(bitmap);
                tabletoupdate="delivery_receivers_print";
                col2Update="rprint";
                //            Picasso.get(ImageSelector.this).load(uri).fit().centerCrop().into(imageView);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

        byte[] picData = null;
        try {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            picData = stream.toByteArray();
            if (bitmap != null && !bitmap.isRecycled()) {
          //      bitmap.recycle();
            //    bitmap=null;
            }
            ContentValues contentValue = new ContentValues();
            String base64String = Base64.encodeToString(picData, Base64.NO_WRAP);
            Log.e("BYTELENGTH","The size in byte is " + picData.length);

            Log.e("BASE64LENGTH","The size in base64 is " + base64String.length());
            contentValue.put("img", base64String);
            contentValue.put("delivery_id", deliveryId);
            long insert_emp = dataDB.myConnection(getApplicationContext()).onInsertOrUpdate(contentValue,
                    tabletoupdate);
            if(insert_emp>0){
                contentValue = new ContentValues();
                contentValue.put(col2Update,1);
                dataDB.myConnection(getApplicationContext()).onUpdateOrIgnore(contentValue,
                        "deliveries","delivery_id",deliveryId);
            }
        }catch (Exception e){
            Toast.makeText(VerifyClientDelivery.this,"Error Occurred :- "+e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}