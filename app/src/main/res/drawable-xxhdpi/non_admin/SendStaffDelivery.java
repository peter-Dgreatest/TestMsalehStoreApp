package com.itcrusaders.msaleh.non_admin;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.itcrusaders.msaleh.database.DeliveryDAO;
import com.itcrusaders.msaleh.LoginActivity;
import com.itcrusaders.msaleh.R;
import com.itcrusaders.msaleh.VerifyDeliveryPhoto;
import com.itcrusaders.msaleh.helpers.App;
import com.itcrusaders.msaleh.helpers.DataDB;

public class SendStaffDelivery extends AppCompatActivity {

    App app;
    DataDB dataDB;// = new DataDB();
    String deliveryId;

    TextView TvClientName,TvEquipmentName,TvEquipmentBarcode,TveliveryAddress,TvDeliveryInstructions,TvreceivingStaff,
                TvDateOfPurchase,TvClientPhone,TvReceipt,TvEmail;

    DeliveryDAO deliveryDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_staff_delivery);

        app = (App) getApplicationContext();

        if(app.getAccount_type().length()<2){
            startActivity(new Intent(this, LoginActivity.class));
        }

        TextView TUserType = findViewById(R.id.tv_loginType);
        TextView TUsername = findViewById(R.id.tv_userName);

        TUsername.setText(app.getFirstname());
        TUserType.setText(app.getAccount_type());

        deliveryDAO  = (DeliveryDAO) getIntent().getSerializableExtra("deliveryId");
        deliveryId=deliveryDAO.getDeliveryId();

        TvClientName = findViewById(R.id.equi_clientName);
        TvEquipmentName = findViewById(R.id.equiName);
        TvEquipmentBarcode = findViewById(R.id.equibarcode);
        TveliveryAddress = findViewById(R.id.equideladdress);
        TvDeliveryInstructions = findViewById(R.id.equidelInstructions);
        TvreceivingStaff = findViewById(R.id.equireceivingStaff);
        TvDateOfPurchase = findViewById(R.id.equipurchaseDate);
        TvClientPhone = findViewById(R.id.equiclient_phone);
        TvReceipt = findViewById(R.id.equiclientReceipt);
        TvEmail = findViewById(R.id.equiemail);

        TvClientName.setText(deliveryDAO.getClientname());
        TvEquipmentName.setText(deliveryDAO.getEquipmentName());
        TvEquipmentBarcode.setText(deliveryDAO.getEquipmentbarcode());
        TveliveryAddress.setText(deliveryDAO.getPickupaddress());
        TvDeliveryInstructions.setText(deliveryDAO.getDeliveryinstructions());
        TvreceivingStaff.setText(deliveryDAO.getAttendingStaffName());
        TvDateOfPurchase.setText(deliveryDAO.getPurchasedate());
        TvClientPhone.setText(deliveryDAO.getClientphone());
        TvReceipt.setText(deliveryDAO.getReceiptno());
        TvEmail.setText(deliveryDAO.getReceivermail());

        try{
            ContentValues cv = new ContentValues();
            cv.put("deliveryid",deliveryId);
            new DataDB().myConnection(getApplicationContext()).onInsertOrUpdate(cv,"deliveryimages");
        }catch (Exception ed){
            Toast.makeText(getApplicationContext(),"error: "+ed.getMessage(), Toast.LENGTH_LONG).show();
        }


        findViewById(R.id.btn_nxt_frag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendStaffDelivery.this, VerifyDeliveryPhoto.class).putExtra("deliveryId",deliveryDAO));
            }
        });

    }
}