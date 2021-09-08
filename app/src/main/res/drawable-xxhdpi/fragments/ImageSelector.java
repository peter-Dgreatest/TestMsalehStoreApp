package com.itcrusaders.msaleh.fragments;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itcrusaders.msaleh.R;
import com.itcrusaders.msaleh.helpers.DataDB;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageSelector extends AppCompatActivity {

    String staffId,imgtype;
    TextView TvcntrolTxt;
    AppCompatImageButton Bselect,Bchange,Bcancel;
    String tabletoupdate,col2update;
    ImageView imageView;
    DataDB dataDB;
    byte[] picData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector);

        TvcntrolTxt = findViewById(R.id.txt_control);
        Bcancel = findViewById(R.id.btn_cancel);
        Bselect = findViewById(R.id.btn_check);
        Bchange = findViewById(R.id.btn_edit);

        imageView = findViewById(R.id.imageView);
        dataDB = new DataDB();

        if(getIntent()!=null){
            staffId = getIntent().getStringExtra("staffId");
            imgtype = getIntent().getStringExtra("type");

            TvcntrolTxt.setText("Select an Image for Staff "+imgtype);

            if(imgtype.equalsIgnoreCase("photo")){
                tabletoupdate = "staffphoto";
                col2update = "Img";
            }else if(imgtype.equalsIgnoreCase("id_card")){
                tabletoupdate = "staffidcard";
                col2update = "idCardImg";
            }else if(imgtype.equalsIgnoreCase("fingerprint")){
                tabletoupdate = "stafffingerprint";
                col2update = "fingerImg";
            }if(imgtype.equalsIgnoreCase("signature")){
                tabletoupdate = "staffsignature";
                col2update = "signatureImg";
            }

            col2update = "Img";

            try {
                String imgData;
                Cursor cursor = dataDB.myConnection(getApplicationContext()).selectAllFromTable("select * from " +
                        tabletoupdate+" where staffId = '" + staffId + "'", true);


                if (cursor.moveToFirst()) {
                    imgData = cursor.getString(cursor.getColumnIndex("Img"));
                    byte[] decoded64String = Base64.decode(imgData, Base64.NO_WRAP);
                    Bitmap bmp = BitmapFactory.decodeByteArray(decoded64String, 0, decoded64String.length);

                    imageView.setImageBitmap(bmp);
                }
            }catch (Exception ex){
           //     Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
            }


            Bselect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();


                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        picData = stream.toByteArray();
                        if (bm != null && !bm.isRecycled()) {
                            bm.recycle();
                            bm=null;
                        }
                    }catch (Exception e){
                        Toast.makeText(ImageSelector.this,"Error Occurred :- "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    ContentValues contentValue = new ContentValues();
                    String base64String = Base64.encodeToString(picData, Base64.NO_WRAP);
                    Log.e("BYTELENGTH","The size in byte is " + picData.length);

                    Log.e("BASE64LENGTH","The size in base64 is " + base64String.length());
                    contentValue.put(col2update, base64String);
                    contentValue.put("synchedstatus", 1);
                    long insert_emp = dataDB.myConnection(getApplicationContext()).onUpdateOrIgnore(contentValue,
                            tabletoupdate,"staffId",staffId);
                    if (insert_emp > 0) {
                        Log.e("saving +++++: " ,":}");

                        //app.setCameraByte(picData);
                        Toast.makeText(getApplicationContext(), "Picture saved successfully", Toast.LENGTH_LONG).show();

                        Intent output = new Intent();
                        output.putExtra("type",imgtype);
                   //     output.putExtra("img",base64String);
                        setResult(RESULT_OK, output);
                        ImageSelector.this.finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Error saving picture. Please try again", Toast.LENGTH_LONG).show();
                    }
                }
            });

            Bcancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageSelector.this.finish();
                }
            });

            Bchange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        changePic();
                    }catch (Exception ex){
                        Toast.makeText(ImageSelector.this,"Error Occurred :- Cant Start Camera --"+ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private final int requestCode = 10000;
    private static final int requestCodeGallery = 287;
    private void changePic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ImageSelector.this);
        builder.setMessage("Set action to perform");
        builder.setNegativeButton("Take Picture", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent photoCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCapture,requestCode);
            }
        });
        builder.setPositiveButton("Pick from Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent photoCapture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(photoCapture,requestCodeGallery);
            }
        });
        builder.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(this.requestCode== requestCode && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }else if(this.requestCodeGallery==requestCode && data!=null){
            InputStream inputStream = null;
            try {
                Uri uri = data.getData();
                inputStream = ImageSelector.this.getContentResolver().openInputStream(data.getData());

                Bitmap original = BitmapFactory.decodeStream(inputStream);

                imageView.setImageBitmap(original);
    //            Picasso.get(ImageSelector.this).load(uri).fit().centerCrop().into(imageView);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}