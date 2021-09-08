package com.itcrusaders.msaleh.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import androidx.core.app.Fragment;
import androidx.core.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itcrusaders.msaleh.database.DeliveryDAO;
import com.itcrusaders.msaleh.database.EnrollDAO;
import com.itcrusaders.msaleh.R;
import com.itcrusaders.msaleh.StaffDB;
import com.itcrusaders.msaleh.VerifyDeliveryClass;
import com.itcrusaders.msaleh.helpers.App;
import com.itcrusaders.msaleh.helpers.DataDB;
import com.itcrusaders.msaleh.helpers.HttpConnectionHelper;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendDelivery2 extends Fragment {

    MaterialBetterSpinner MSBranch,MSStaffName;
    EditText Edpickupaddress,EdreceiverName,EdJobDesignation,Edphone1,Edaltphone1,Edmail,EdSurname,EdFirstname;

    public SendDelivery2() {
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
    public static SendDelivery2 newInstance(String param1, String param2) {
        SendDelivery2 fragment = new SendDelivery2();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
  //      args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    DeliveryDAO deliveryDAO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            deliveryDAO = getArguments().getParcelable("deliveryInfo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.send2, container, false);
    }


    Button BnextFragmet;


    String attendingStaffId="";
    App app;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        app = (App) getContext().getApplicationContext();

        Edmail = getView().findViewById(R.id.edt_del_receiver_mail);
        EdSurname = getView().findViewById(R.id.edt_del_reciver_surname);
        EdFirstname = getView().findViewById(R.id.edt_firstName);
        Edphone1 = getView().findViewById(R.id.edt_del_receiver_phone1);
        Edaltphone1 = getView().findViewById(R.id.edt_del_receiver_phone2);
        EdJobDesignation = getView().findViewById(R.id.edt_job_design);

        Edpickupaddress = getView().findViewById(R.id.edt_pckupadddress);
        MSBranch = getView().findViewById(R.id.sp_delivery_branch);
        MSStaffName = getView().findViewById(R.id.sp_delivery_att_staff);

        final DataDB dataDB = new DataDB();
        BnextFragmet= getView().findViewById(R.id.btn_nxt_frag);

        //MSStaffName.onItemClick();

        MSStaffName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String staffinfo_ = MSStaffName.getAdapter().getItem(i).toString();

                    //  Toast.makeText(getContext(),staffinfo_,Toast.LENGTH_LONG).show();
                    for (EnrollDAO dao :
                            inventoryDAOS) {
                        //    Toast.makeText(getContext(),dao.getStaffName(),Toast.LENGTH_LONG).show();
                        if (dao.getStaffName().equalsIgnoreCase(staffinfo_.trim())) {
                            //      Toast.makeText(getContext(),staffinfo_,Toast.LENGTH_LONG).show();
                            attendingStaffId = dao.getStaffId();
                        }
                    }
                }
            }catch(Exception s){
                Toast.makeText(getApplicationContext(),s.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        new LoadFromOnline().execute();

        Button BprevFragment= view.findViewById(R.id.btn_prev_frag);

        BprevFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  startActivity(new Intent(SendDelivery1.this,));
                //getActivity().finish();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment newFrag = new SendDelivery1();
                Bundle bundle = new Bundle();
              //  DeliveryDAO deliveryDAO = new DeliveryDAO();

                bundle.putSerializable("deliveryInfo",deliveryDAO);

                newFrag.setArguments(bundle);
                transaction.replace(R.id.fragMentManager, newFrag, "frag1");

                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        BnextFragmet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean _2proceed = verifyTransaction();
                if(_2proceed) {
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment newFrag = new AdminEnroll4();
                    Bundle bundle = new Bundle();
                    //deliveryDAO = new deliveryDAO();
                    deliveryDAO.setPickupbranch(MSBranch.getText().toString());
                    deliveryDAO.setAttendingStaffName(MSStaffName.getText().toString());
                    deliveryDAO.setAttendingStaffId(attendingStaffId);
                    deliveryDAO.setPickupaddress(Edpickupaddress.getText().toString());
                    deliveryDAO.setReceivername(EdSurname.getText().toString()+ " "+EdFirstname.getText().toString());
                    deliveryDAO.setReceiverdesignation(EdJobDesignation.getText().toString());
                    deliveryDAO.setReceivermail(Edmail.getText().toString());
                    deliveryDAO.setReceiverphone1(Edphone1.getText().toString());
                    deliveryDAO.setReceiverphone2(Edaltphone1.getText().toString());

                    new SendDeliveryNow().execute();
/**

                    ThreadLocalRandom random = ThreadLocalRandom.current();

                    long Id = dataDB.myConnection(getContext()).countRecords("deliveries") +
                            random.nextLong(10_00000L, 1000_0000L);


                    String deliveryId =  (app.getUsername()+""+""+Id).substring(0,10);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("equipmentbarcode",deliveryDAO.getEquipmentbarcode());
                    contentValues.put("clientname",deliveryDAO.getClientname());
                    contentValues.put("clientaddress",deliveryDAO.getClientname());
                    contentValues.put("purchasedate",deliveryDAO.getPurchasedate());
                    contentValues.put("deliveryinstructions",deliveryDAO.getDeliveryinstructions());
                    contentValues.put("officialmail",deliveryDAO.getOfficialmail());
                    contentValues.put("clientphone",deliveryDAO.getClientphone());
                    contentValues.put("altphone",deliveryDAO.getAltphone());
                    contentValues.put("receiptno",deliveryDAO.getReceiptno());
                    contentValues.put("pickupbranch",deliveryDAO.getPickupbranch());
                    contentValues.put("attendingStaffId",deliveryDAO.getAttendingStaffId());
                    contentValues.put("attendingStaffName",deliveryDAO.getAttendingStaffName());
                    contentValues.put("pickupaddress",deliveryDAO.getPickupaddress());
                    contentValues.put("receivername",deliveryDAO.getReceivername());
                    contentValues.put("receiverdesignation",deliveryDAO.getReceiverdesignation());
                    contentValues.put("receivermail",deliveryDAO.getReceivermail());
                    contentValues.put("receiverphone1",deliveryDAO.getReceiverphone1());
                    contentValues.put("receiverphone2",deliveryDAO.getReceiverphone2());
                   // contentValues.put("approvedby",deliveryDAO.getApprovedby());
                    contentValues.put("delivery_id",deliveryId);

                    long suc = dataDB.myConnection(getContext()).onInsertOrUpdate(contentValues,"deliveries");
                    if(suc>0){

                      //  bundle.putString("staffId",deliveryId);

                        Intent intent = new Intent(getActivity(), DeliveryFingerPrintVerify.class).putExtra("deliveryId",deliveryId);
                        startActivity(intent);
                    }

 **/
                }else{
                    Toast.makeText(getContext(),"Error, Please input all fields", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private boolean verifyTransaction() {
        String msg="";
       // Toast.makeText(getContext(),msg+" "+attendingStaffId,Toast.LENGTH_LONG).show();
        if(attendingStaffId.length()<2){
            msg="Please select a staff";
        }
        if(MSBranch.getText().toString().length()<2){
            msg="Please select a branch";
        }
        if(msg.length()>4)
            return false;
        else
            return true;
    }

    private class SendDeliveryNow extends AsyncTask<Void, Void, Void> {

        HttpConnectionHelper helper;
        HashMap<String, String> postDataParams ;
        int suc =0;
        String newDeliveryId="",response;

        JSONArray restulJsonArray;
        JSONObject returnJSONOutput = new JSONObject();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            helper = new HttpConnectionHelper(getActivity().getApplicationContext());
            try {
                suc = 1;

                HashMap<String, String> contentValues = new HashMap<String, String>();



                contentValues.put("equipmentbarcode", deliveryDAO.getEquipmentbarcode());
                contentValues.put("clientname", deliveryDAO.getClientname());
                contentValues.put("clientaddress", deliveryDAO.getClientname());
                contentValues.put("purchasedate", deliveryDAO.getPurchasedate());
                contentValues.put("deliveryinstructions", deliveryDAO.getDeliveryinstructions());
                contentValues.put("officialmail", deliveryDAO.getOfficialmail());
                contentValues.put("clientphone", deliveryDAO.getClientphone());
                contentValues.put("altphone", deliveryDAO.getAltphone());
                contentValues.put("receiptno", deliveryDAO.getReceiptno());
                contentValues.put("pickupbranch", deliveryDAO.getPickupbranch());
                contentValues.put("attendingStaffId", deliveryDAO.getAttendingStaffId());
                contentValues.put("attendingStaffName", deliveryDAO.getAttendingStaffName());
                contentValues.put("pickupaddress", deliveryDAO.getPickupaddress());
                contentValues.put("receivername", deliveryDAO.getReceivername());
                contentValues.put("receiverdesignation", deliveryDAO.getReceiverdesignation());
                contentValues.put("receivermail", deliveryDAO.getReceivermail());
                contentValues.put("receiverphone1", deliveryDAO.getReceiverphone1());
                contentValues.put("receiverphone2", deliveryDAO.getReceiverphone2());
                // contentValues.put("approvedby",deliveryDAO.getApprovedby());

                response = helper.sendRequest(getString(R.string.senddelivery),contentValues);

                JSONObject resultJsonObject = new JSONObject(response);
                newDeliveryId = resultJsonObject.getString("output");

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
                if (!newDeliveryId.isEmpty() || newDeliveryId.length() > 2 && !(newDeliveryId.contains("Already Registered"))) {
                    //ArrayAdapter listViewAdapter = new ArrayAdapter<String>(mContext, R.layout.mobile_name_listview);
                    try {
                        if(response.contains("Error Occured")){

                        }else{
                            startActivity(new Intent(getContext(), VerifyDeliveryClass.class).putExtra("deliveryId",newDeliveryId)
                                        .putExtra("branch",deliveryDAO.getPickupbranch()).putExtra("staffId",deliveryDAO.getAttendingStaffId()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(newDeliveryId.contains("Already Registered")){
                    Toast.makeText(getContext(),"Already registered", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getActivity(), StaffDB.class));
                    getActivity().finish();
                }
            }else{
                if(response.equalsIgnoreCase("No Internet Access")
                        || response.contains("Failed to connect")){
                    AlertDialog.Builder builder
                            = new AlertDialog.Builder(getActivity().getApplicationContext());
                    builder.setMessage("No Internet Access");
                    builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new SendDeliveryNow().execute();
                        }
                    });
                    builder.show();
                }
            }
        }
    }


    ProgressDialog progressDialog;
    ArrayList<EnrollDAO> inventoryDAOS;
    List<String> enrolls;

    private class LoadFromOnline extends AsyncTask<Void, Void, Void> {


        JSONArray restulJsonArray;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Retrieving products!\r\nPlease Wait");
            progressDialog.show();
        }


        HttpConnectionHelper helper;
        HashMap<String, String> postDataParams ;

        @Override
        protected Void doInBackground(Void... voids) {
            helper = new HttpConnectionHelper(getContext());
            try {
                suc =1;

                postDataParams = new HashMap<String, String>();
                postDataParams.put("HTTP_ACCEPT", "application/json");
                response = helper.sendRequest(getString(R.string.getStaff),postDataParams);
                JSONObject resultJsonObject = new JSONObject(response);
                restulJsonArray = resultJsonObject.getJSONArray("output");
                Log.e("response",response+";;;");
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
                    inventoryDAOS = new ArrayList<EnrollDAO>();
                    enrolls = new ArrayList<String>();
                    //ArrayAdapter listViewAdapter = new ArrayAdapter<String>(mContext, R.layout.mobile_name_listview);
                    for (int i = 0; i < restulJsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject = restulJsonArray.getJSONObject(i);
                            EnrollDAO messagesDAO =
                                    new EnrollDAO();

                            messagesDAO.setStaffId(jsonObject.get("staffId").toString());
                            messagesDAO.setBranch(jsonObject.get("branch").toString());
                            messagesDAO.setStaffMail(jsonObject.get("email").toString());
                            messagesDAO.setStaffName(jsonObject.get("surname").toString());
                            enrolls.add(jsonObject.get("surname").toString());


                            //    messagesDAO.setBarcode(jsonObject.get("barcode").toString());

                            inventoryDAOS.add(messagesDAO);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    DataDB dataDB = new DataDB();
                    Cursor branches =  dataDB.myConnection(getContext()).selectAllFromTable("select * from branch",true);
                    List<String> branches_;
                    branches_ = new ArrayList<String>();
                    if(branches.moveToFirst()){
                        do{
                            //Log.e("branch_info0",branches.getString(branches.getColumnIndex("branch_name")));
                            branches_.add(branches.getString(branches.getColumnIndex("branch_name")));
                        }while(branches.moveToNext());
                    }

                    ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.select_dialog_item, branches_);

                    countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the your spinner
                    MSBranch.setAdapter(countryAdapter);

                    countryAdapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.select_dialog_item, enrolls);

                    countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the your spinner
                    MSStaffName.setAdapter(countryAdapter);
                }
            }else{
                if(response.equalsIgnoreCase("No Internet Access")
                        || response.contains("Failed to connect")){
                    AlertDialog.Builder builder
                            = new AlertDialog.Builder(getContext());
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
            //setMessageData();
        }
    }

    int suc =0;
}