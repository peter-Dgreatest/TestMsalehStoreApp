package com.itcrusaders.msaleh.fragments;

import android.content.Context;
import android.database.Cursor;
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
import android.widget.Toast;

import com.itcrusaders.msaleh.database.EnrollDAO;
import com.itcrusaders.msaleh.R;
import com.itcrusaders.msaleh.helpers.DataDB;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Enroll1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class
Enroll1 extends Fragment {

    DataDB dataDB;
    Context mContext;
    MaterialBetterSpinner sp_branch,sp_designation,sp_description,sp_supervisor;
    String branchValue,designationValue,descriptionValue,supervisorValue;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Enroll1() {
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
    public static Enroll1 newInstance(String param1, String param2) {
        Enroll1 fragment = new Enroll1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    EnrollDAO enrollDAO = new EnrollDAO();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            enrollDAO = getArguments().getSerializable("enrollInfo");
            //  mParam2 = getArguments().getString(ARG_PARAM2);
           // Toast.makeText(getContext(),enrollDAO.getBranch()+";;;",Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.enroll, container, false);
    }

    Button BnextFragmet;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        dataDB= new DataDB();
        sp_branch= getView().findViewById(R.id.sp_branch);
        sp_designation= getView().findViewById(R.id.sp_department);
        sp_description= getView().findViewById(R.id.sp_job_descri);
        sp_supervisor= getView().findViewById(R.id.sp_immediate_supervisor);

        if (getArguments() != null) {
            enrollDAO = getArguments().getSerializable("enrollInfo");
            sp_branch.setText(enrollDAO.getBranch());
            sp_description.setText(enrollDAO.getJob_description());
            sp_designation.setText(enrollDAO.getDepart());
            sp_supervisor.setText(enrollDAO.getSupervisor());
            //  mParam2 = getArguments().getString(ARG_PARAM2);
            // Toast.makeText(getContext(),enrollDAO.getBranch()+";;;",Toast.LENGTH_LONG).show();

        }
        sp_designation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(sp_designation.getAdapter().getItem(i).toString().equalsIgnoreCase("supervisor")){
                    sp_supervisor.setVisibility(View.GONE);
                }
                if(sp_designation.getAdapter().getItem(i).toString().contains("chief")){
                    sp_supervisor.setVisibility(View.GONE);
                    sp_branch.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        BnextFragmet= getView().findViewById(R.id.btn_nxt_frag);
        getView().findViewById(R.id.btn_prev_frag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        BnextFragmet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean _2proceed = verifyTransaction();
                if(_2proceed) {
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment newFrag = new AdminEnroll2();
                    Bundle bundle = new Bundle();
                    if(enrollDAO.getStaffId()==null || enrollDAO.getStaffName()==null)
                        enrollDAO = new EnrollDAO();
                    enrollDAO.setBranch(branchValue);
                    enrollDAO.setSupervisor(supervisorValue);
                    enrollDAO.setDepart(descriptionValue);
                    enrollDAO.setJob_description(descriptionValue);

                    bundle.putSerializable("enrollInfo",enrollDAO);

                    newFrag.setArguments(bundle);
                    transaction.replace(R.id.fragMentManager, newFrag, "frag2");

                    transaction.addToBackStack(null);
                    transaction.commit();
                }else{
                    Toast.makeText(getContext(),"Error, Please input all fields", Toast.LENGTH_LONG).show();
                }
            }
        });

        sp_branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        sp_branch.setAdapter(countryAdapter);

        Cursor designation =  dataDB.myConnection(getContext()).selectAllFromTable("select * from designation",true);
        //List<String> branches_;
        branches_ = new ArrayList<String>();
        if(designation.moveToFirst()){
            do{
                Log.e("branch_info0",designation.getString(designation.getColumnIndex("designation")));
                branches_.add(designation.getString(designation.getColumnIndex("designation")));
            }while(designation.moveToNext());
        }

        countryAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.select_dialog_item, branches_);

        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the your spinner
        sp_designation.setAdapter(countryAdapter);

        Cursor description =  dataDB.myConnection(getContext()).selectAllFromTable("select * from  job_description",true);
        //List<String> branches_;
        branches_ = new ArrayList<String>();
        if(description.moveToFirst()){
            do{
                branches_.add(description.getString(description.getColumnIndex("description")));
            }while(description.moveToNext());
        }

        countryAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.select_dialog_item, branches_);

        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the your spinner
        sp_description.setAdapter(countryAdapter);


        Cursor supervisor =  dataDB.myConnection(getContext()).selectAllFromTable("select * from supervisor",true);
        //List<String> branches_;
        branches_ = new ArrayList<String>();
        if(supervisor.moveToFirst()){
            do{
                branches_.add(supervisor.getString(supervisor.getColumnIndex("supervisor")));
            }while(supervisor.moveToNext());
        }

        countryAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.select_dialog_item, branches_);

        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the your spinner
        sp_supervisor.setAdapter(countryAdapter);
    }

    private boolean verifyTransaction() {
        branchValue = sp_branch.getText().toString();
        descriptionValue = sp_description.getText().toString();
        designationValue = sp_designation.getText().toString();
        supervisorValue = sp_supervisor.getText().toString();
        String msg="";

        if(branchValue.equalsIgnoreCase("branch") || branchValue.length()<3){
            msg="error";
        }if(designationValue.equalsIgnoreCase("department") || descriptionValue.length()<3){
            msg="error";
        }if(designationValue.equalsIgnoreCase("job description") || designationValue.length()<3){
            msg="error";
        }if(supervisorValue.equalsIgnoreCase("immediate supervisor") || supervisorValue.length()<3){
            msg="error";
        }

        //Toast.makeText(getContext(),msg+" "+branchValue,Toast.LENGTH_LONG).show();
       // if(msg.equalsIgnoreCase("error"))
         //   return false;
        //else
            return true;
    }
}