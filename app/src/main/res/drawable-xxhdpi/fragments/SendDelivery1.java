package com.itcrusaders.msaleh.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import androidx.core.app.Fragment;
import androidx.core.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itcrusaders.msaleh.database.DeliveryDAO;
import com.itcrusaders.msaleh.R;
import com.itcrusaders.msaleh.ScanCodeActivity;
import com.itcrusaders.msaleh.helpers.DataDB;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SendDelivery1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendDelivery1 extends Fragment {

    EditText EdclientName,EdBarcode,EdclientAddress,EdPurchaseate,EddeliveryInstructions,Edclientmail,EdclientPhone,
            Edclientaltphone,Edreceiptno;


    public SendDelivery1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SendDelivery1.
     */
    // TODO: Rename and change types and number of parameters
    public static SendDelivery1 newInstance(String param1, String param2) {
        SendDelivery1 fragment = new SendDelivery1();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.send1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EdclientName = view.findViewById(R.id.edt_client_name);
        EdBarcode = view.findViewById(R.id.edt_eqi_barcorde);
        EdclientAddress = view.findViewById(R.id.edt_client_address);
        EdPurchaseate = view.findViewById(R.id.edt_equi_purchase_date);
        EddeliveryInstructions = view.findViewById(R.id.edt_delivery_info);
        Edclientmail = view.findViewById(R.id.edt_client_phone);
        EdclientPhone = view.findViewById(R.id.edt_edt_client_phone);
        Edclientaltphone= view.findViewById(R.id.edt_email);
        Edreceiptno= view.findViewById(R.id.edt_receipt_no);


        BnextFragmet= view.findViewById(R.id.btn_nxt_frag);
        BprevFragment= view.findViewById(R.id.btn_prev_frag);

        BprevFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  startActivity(new Intent(SendDelivery1.this,));
                getActivity().finish();
            }
        });


        EdBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        EdBarcode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (EdBarcode.getRight() - EdBarcode.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        Toast.makeText(getContext(),"hey", Toast.LENGTH_LONG).show();
                        {
                            Intent scanCode = new Intent(getContext(), ScanCodeActivity.class);
                            startActivity(scanCode);
                        }
                        return true;
                    }
                }
                return false;
            }
        });



        BnextFragmet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean _2proceed = verifyTransaction();
                if(_2proceed) {
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment newFrag = new SendDelivery2();
                    Bundle bundle = new Bundle();
                    DeliveryDAO deliveryDAO = new DeliveryDAO();
                    deliveryDAO.setClientname(EdclientName.getText().toString());
                    deliveryDAO.setEquipmentbarcode(EdBarcode.getText().toString());
                    deliveryDAO.setClientaddress(EdclientAddress.getText().toString());
                    deliveryDAO.setPurchasedate(EdPurchaseate.getText().toString());
                    deliveryDAO.setDeliveryinstructions(EddeliveryInstructions.getText().toString());
                    deliveryDAO.setOfficialmail(Edclientmail.getText().toString());
                    deliveryDAO.setClientphone(EdclientPhone.getText().toString());
                    deliveryDAO.setAltphone(Edclientaltphone.getText().toString());
                    deliveryDAO.setReceiptno(Edreceiptno.getText().toString());

                    bundle.putSerializable("deliveryInfo",deliveryDAO);

                    newFrag.setArguments(bundle);
                    transaction.replace(R.id.fragMentManager, newFrag, "frag2");

                    transaction.addToBackStack(null);
                    transaction.commit();
                }else{
                    Toast.makeText(getContext(),"Error, Please input all fields", Toast.LENGTH_LONG).show();
                }
            }
        });
        

    }

    private boolean verifyTransaction() {

        return  true;
    }


    DataDB dataDB;
    Button BnextFragmet,BprevFragment;
}