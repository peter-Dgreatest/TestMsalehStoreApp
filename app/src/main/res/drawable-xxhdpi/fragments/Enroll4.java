package com.itcrusaders.msaleh.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import androidx.core.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itcrusaders.msaleh.R;

public class Enroll4 extends Fragment {


    public Enroll4() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Enroll1.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminEnroll4 newInstance(String param1, String param2) {
        AdminEnroll4 fragment = new AdminEnroll4();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    String staffId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            staffId = getArguments().getString("staffId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.enroll4, container, false);
    }

    TextView StaffIdInfo;
    ImageView Img_fingerPrintView,Img_photoView,Img_id_cardView,Img_signatureView;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        StaffIdInfo = getView().findViewById(R.id.tv_staff_id);

        Img_fingerPrintView = getView().findViewById(R.id.buttonfingerprint);
        Img_id_cardView = getView().findViewById(R.id.buttonIdcard);
        Img_signatureView = getView().findViewById(R.id.signature);
        Img_photoView = getView().findViewById(R.id.buttonPhoto);


        StaffIdInfo.setText(staffId);


        Img_photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), ImageSelector.class).putExtra("staffId",staffId)
                .putExtra("type","photo"),1000);
            }
        });

        Img_signatureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), ImageSelector.class).putExtra("staffId",staffId)
                        .putExtra("type","signature"),1000);
            }
        });

        Img_id_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), ImageSelector.class).putExtra("staffId",staffId)
                        .putExtra("type","id_card"),1000);
            }
        });

        Img_fingerPrintView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(getContext(), ImageSelector.class).putExtra("staffId",staffId)
                        .putExtra("type","fingerprint"),1000);
            }
        });




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (1000 == requestCode && resultCode == Activity.RESULT_OK) {
                //picData=(byte[]) data.getExtras().get("data");
                Bitmap bitmap = getImage(data.getStringExtra("img"));
                String imgType = data.getStringExtra("type");
                switch (imgType) {
                    case "photo":
                        Img_photoView.setImageBitmap(bitmap);
                        break;
                    case "id_card":
                        Img_id_cardView.setImageBitmap(bitmap);
                        break;
                    case "signature":
                        Img_signatureView.setImageBitmap(bitmap);
                        break;
                    case "fingerprint":
                        Img_fingerPrintView.setImageBitmap(bitmap);
                        break;
                }


            }
        }catch (Exception ex){
            Toast.makeText(getContext(),"Error Occurred :- "+ex.getMessage(), Toast.LENGTH_LONG).show();
        }
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
}