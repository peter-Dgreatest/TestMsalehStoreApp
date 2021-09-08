package com.itcrusaders.msaleh.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import androidx.core.app.Fragment;
import androidx.core.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itcrusaders.msaleh.database.EnrollDAO;
import com.itcrusaders.msaleh.ImagesCapture;
import com.itcrusaders.msaleh.R;
import com.itcrusaders.msaleh.StaffDB;
import com.itcrusaders.msaleh.helpers.App;
import com.itcrusaders.msaleh.helpers.DataDB;
import com.itcrusaders.msaleh.helpers.HttpConnectionHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class Enroll3 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";


    public Enroll3() {
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
    public static AdminEnroll3 newInstance(String param1, String param2) {
        AdminEnroll3 fragment = new AdminEnroll3();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
  //      args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    EnrollDAO enrollDAO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            enrollDAO = (EnrollDAO) getArguments().getSerializable("enrollInfo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.enroll3, container, false);
    }


    Button BnextFragmet;
    EditText Egtitle,Egname,Enaddress,EgPhone1,EgPhone2,Egmail,Entitle,Enname;

    App app;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        app = (App) getContext().getApplicationContext();

        Egmail = getView().findViewById(R.id.edt_gemail);
        Egname = getView().findViewById(R.id.edt_guarantor_name);
        EgPhone1 = getView().findViewById(R.id.edt_gphone1);
        EgPhone2 = getView().findViewById(R.id.edt_gphone2);
        Egtitle = getView().findViewById(R.id.sp_guarantor_title);

        Enaddress = getView().findViewById(R.id.edt_naddress);
        Enname = getView().findViewById(R.id.edt_nname);
        Entitle = getView().findViewById(R.id.sp_ntitle);

        final DataDB dataDB = new DataDB();
        BnextFragmet= getView().findViewById(R.id.btn_nxt_frag);

        BnextFragmet.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                boolean _2proceed = verifyTransaction();
                if(_2proceed) {
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment newFrag = new AdminEnroll4();
                    Bundle bundle = new Bundle();
                    //enrollDAO = new EnrollDAO();


                    enrollDAO.setGuarantorTitle(Egtitle.getText().toString());
                    enrollDAO.setGuarantorName(Egname.getText().toString());
                    enrollDAO.setGuarantorPhone1(EgPhone1.getText().toString());
                    enrollDAO.setGuarantorPhone2(EgPhone2.getText().toString());
                    enrollDAO.setGuarantormail(Egmail.getText().toString());

                    enrollDAO.setNxtOfKinTitle(Entitle.getText().toString());
                    enrollDAO.setNxtOfKinName(Enname.getText().toString());
                    enrollDAO.setNxtOfKinAddress(Enaddress.getText().toString());



                    new EnrollOnline().execute();
                }else{
                    Toast.makeText(getContext(),"Error, Please input all fields", Toast.LENGTH_LONG).show();
                }
            }
        });


        getView().findViewById(R.id.btn_prev_frag).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment newFrag = new AdminEnroll2();
                Bundle bundle = new Bundle();

                bundle.putSerializable("enrollInfo",enrollDAO);

                newFrag.setArguments(bundle);
                transaction.replace(R.id.fragMentManager, newFrag, "frag2");

                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private boolean verifyTransaction() {
        String msg="";
        //Toast.makeText(getContext(),msg+" "+branchValue,Toast.LENGTH_LONG).show();
        if(msg.equalsIgnoreCase("error"))
            return false;
        else
            return true;
    }


    private class EnrollOnline extends AsyncTask<Void, Void, Void> {
        JSONArray restulJsonArray;
        JSONObject returnJSONOutput = new JSONObject();

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Saving Staff Details!\r\nPlease Wait");
                progressDialog.show();
            }catch (Exception e){
                Toast.makeText(getContext(),e.toString(), Toast.LENGTH_LONG).show();
            }
        }


        HttpConnectionHelper helper;
        HashMap<String, String> postDataParams ;
        int suc =0;
        String newStaffId="";

        @Override
        protected Void doInBackground(Void... voids) {
            helper = new HttpConnectionHelper(getActivity().getApplicationContext());
            try {
                suc =1;

                postDataParams = new HashMap<String, String>();
                postDataParams.put("HTTP_ACCEPT", "application/json");
                if(enrollDAO.getStaffId()!=null)
                    postDataParams.put("staffId",enrollDAO.getStaffId());
                postDataParams.put("title",enrollDAO.getStaffTitle());
                postDataParams.put("surname",enrollDAO.getStaffName());
                postDataParams.put("firstname",enrollDAO.getStaffName());
                //postDataParams.put("title","");
                postDataParams.put("address",enrollDAO.getStaffaddress());
                postDataParams.put("phone1",enrollDAO.getStaffno());
                postDataParams.put("phone2",enrollDAO.getStaffno2());
                postDataParams.put("email",enrollDAO.getStaffMail());
                postDataParams.put("dob",enrollDAO.getStaffBday());
                postDataParams.put("stateOfOrigin",enrollDAO.getStaffStateOfOrigin());
                postDataParams.put("Lga",enrollDAO.getStaffLGA());
                postDataParams.put("maritalStatus",enrollDAO.getStaffMaritalStatus());

                postDataParams.put("branch",enrollDAO.getBranch());
                postDataParams.put("Job_description",enrollDAO.getJob_description());
            //    Log.e("response",postDataParams.toString()+";;;");
                response = helper.sendRequest(getString(R.string.enrollStaff),postDataParams);
                Log.e("response",response+";;;");
                JSONObject resultJsonObject = new JSONObject(response);
                newStaffId = resultJsonObject.getString("output");
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
                if (!newStaffId.isEmpty() || newStaffId.length() > 2 && !(newStaffId.contains("Already Registered"))) {
                    //ArrayAdapter listViewAdapter = new ArrayAdapter<String>(mContext, R.layout.mobile_name_listview);
                    try {


                        ContentValues contentValues = new ContentValues();
                        contentValues.put("title",enrollDAO.getStaffTitle());
                        contentValues.put("surname",enrollDAO.getStaffName());
                        contentValues.put("firstname",enrollDAO.getStaffName());
                        //contentValues.put("title","");
                        contentValues.put("address",enrollDAO.getStaffaddress());
                        contentValues.put("phone1",enrollDAO.getStaffno());
                        contentValues.put("phone2",enrollDAO.getStaffno2());
                        contentValues.put("email",enrollDAO.getStaffMail());
                        contentValues.put("dob",enrollDAO.getStaffBday());
                        contentValues.put("stateOfOrigin",enrollDAO.getStaffStateOfOrigin());
                        contentValues.put("Lga",enrollDAO.getStaffLGA());
                        contentValues.put("maritalStatus",enrollDAO.getStaffMaritalStatus());

                        contentValues.put("branch",enrollDAO.getBranch());
                        contentValues.put("Job_description",enrollDAO.getJob_description());

                        enrollDAO.setStaffId(newStaffId);

                        contentValues.put("staffId",newStaffId);
                        DataDB dataDB = new DataDB();
                        long suc = dataDB.myConnection(getContext()).onInsertOrUpdate(contentValues,"enroll");
                        if(suc>0) {
                            contentValues = new ContentValues();
                            contentValues.put("title",enrollDAO.getStaffTitle());
                            contentValues.put("name__",enrollDAO.getStaffName());
                            contentValues.put("staffId",newStaffId);
                            contentValues.put("phone1",enrollDAO.getStaffno());
                            contentValues.put("phone2",enrollDAO.getStaffno2());
                            contentValues.put("email",enrollDAO.getStaffMail());

                            long suc1 = dataDB.myConnection(getContext()).onInsertOrUpdate(contentValues,"guarantorstatus");
                            if(suc1>0){

                                contentValues = new ContentValues();
                                contentValues.put("title",enrollDAO.getStaffTitle());
                                contentValues.put("name__",enrollDAO.getStaffName());
                                contentValues.put("phone1",enrollDAO.getStaffno());
                                contentValues.put("address",enrollDAO.getStaffno2());
                                contentValues.put("staffId",newStaffId);
                                contentValues.put("email",enrollDAO.getStaffMail()+"");

                                long suc2 = dataDB.myConnection(getContext()).onInsertOrUpdate(contentValues,"nextofkin");
                                if(suc2>0){
                                    contentValues = new ContentValues();
                                    contentValues.put("staffId",newStaffId);

                                    dataDB.myConnection(getContext()).onInsertOrUpdate(contentValues,
                                            "staffsignature");

                                    dataDB.myConnection(getContext()).onInsertOrUpdate(contentValues,
                                            "staffidcard");

                                    dataDB.myConnection(getContext()).onInsertOrUpdate(contentValues,
                                            "stafffingerprint");

                                    dataDB.myConnection(getContext()).onInsertOrUpdate(contentValues,
                                            "staffphoto");


                                    Intent intent = new Intent(getActivity(), ImagesCapture.class).putExtra("staffId",newStaffId);
                                    startActivity(intent);
                                    getActivity().finish();

                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(newStaffId.contains("Already Registered")){
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
                            new EnrollOnline().execute();
                        }
                    });
                    builder.show();
                }
            }
        }
    }

}