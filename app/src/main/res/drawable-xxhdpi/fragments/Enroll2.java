package com.itcrusaders.msaleh.fragments;

import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import androidx.core.app.Fragment;
import androidx.core.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.itcrusaders.msaleh.database.EnrollDAO;
import com.itcrusaders.msaleh.R;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Enroll2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    //private Parcelable mParam1;
   // private String mParam2;

    public Enroll2() {
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
    public static AdminEnroll2 newInstance(String param1, String param2) {
        AdminEnroll2 fragment = new AdminEnroll2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    EnrollDAO enrollDAO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            enrollDAO = (EnrollDAO) getArguments().getSerializable("enrollInfo");
          //  mParam2 = getArguments().getString(ARG_PARAM2);
         //   Toast.makeText(getContext(),enrollDAO.getBranch()+";;;",Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.enroll2, container, false);
    }



    Button BnextFragmet;
    EditText Etitle,Ename,Eaddress,EPhone1,EPhone2,Email,Edob,ELGA;
    MaterialBetterSpinner MSSOOrigin,MSMaritalStatus;


    final Calendar myCalendar = Calendar.getInstance();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MSSOOrigin = getView().findViewById(R.id.sp_stateOfOrigin);
        MSMaritalStatus = getView().findViewById(R.id.sp_maritalStatus);

        Ename = getView().findViewById(R.id.edt_name);
        Eaddress = getView().findViewById(R.id.edt_address);
        ELGA = getView().findViewById(R.id.sp_lga);
        EPhone1 = getView().findViewById(R.id.edt_phone1);
        EPhone2 = getView().findViewById(R.id.edt_phone2);
        Email = getView().findViewById(R.id.edt_email);
        Edob = getView().findViewById(R.id.txt_staff_bday);
        Etitle = getView().findViewById(R.id.sp_title);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };


        Edob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {

               //     Toast.makeText(getContext(),"hey",Toast.LENGTH_LONG).show();
                    new DatePickerDialog(getContext(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        Edob.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (Edob.getRight() - Edob.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                  //      Toast.makeText(getContext(),"hey",Toast.LENGTH_LONG).show();
                         {
                            new DatePickerDialog(getContext(), date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        }
                        return true;
                    }
                }
                return false;
            }
        });


        String[] states = {"Abia",
                "Adamawa",
                "Akwa Ibom",
                "Anambra",
                "Bauchi",
                "Bayelsa",
                "Benue",
                "Borno",
                "Cross River",
                "Delta",
                "Ebonyi",
                "Edo",
                "Ekiti",
                "Enugu",
                "FCT - Abuja",
                "Gombe",
                "Imo",
                "Jigawa",
                "Kaduna",
                "Kano",
                "Katsina",
                "Kebbi",
                "Kogi",
                "Kwara",
                "Lagos",
                "Nasarawa",
                "Niger",
                "Ogun",
                "Ondo",
                "Osun",
                "Oyo",
                "Plateau",
                "Rivers",
                "Sokoto",
                "Taraba",
                "Yobe",
                "Zamfara"};


        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.select_dialog_item, states);

        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MSSOOrigin.setAdapter(countryAdapter);

        String[] statuses = {"single","married"};

        countryAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.select_dialog_item, statuses);

        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MSMaritalStatus.setAdapter(countryAdapter);


        BnextFragmet= getView().findViewById(R.id.btn_nxt_frag);

        BnextFragmet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean _2proceed = verifyTransaction();
                if(_2proceed) {
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment newFrag = new AdminEnroll3();
                    Bundle bundle = new Bundle();
                    //enrollDAO = new EnrollDAO();
                    enrollDAO.setStaffTitle(Etitle.getText().toString());
                    enrollDAO.setStaffName(Ename.getText().toString());
                    enrollDAO.setStaffaddress(Eaddress.getText().toString());
                    enrollDAO.setStaffno(EPhone1.getText().toString());
                    enrollDAO.setStaffno2(EPhone2.getText().toString());
                    enrollDAO.setStaffMail(Email.getText().toString());
                    enrollDAO.setStaffBday(Edob.getText().toString());
                    enrollDAO.setStaffStateOfOrigin(MSSOOrigin.getText().toString());
                    enrollDAO.setStaffLGA(ELGA.getText().toString());
                    enrollDAO.setStaffMaritalStatus(MSMaritalStatus.getText().toString());

                    bundle.putSerializable("enrollInfo",enrollDAO);

                    newFrag.setArguments(bundle);
                    transaction.replace(R.id.fragMentManager, newFrag, "frag3");

                    transaction.addToBackStack(null);
                    transaction.commit();
                }else{
                    Toast.makeText(getContext(),"Error, Please input all fields", Toast.LENGTH_LONG).show();
                }
            }
        });

        getView().findViewById(R.id.btn_prev_frag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment newFrag = new Enroll1();
                Bundle bundle = new Bundle();

                bundle.putSerializable("enrollInfo",enrollDAO);

                newFrag.setArguments(bundle);
                transaction.replace(R.id.fragMentManager, newFrag, "frag1");

                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd MMM YYYY"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        Edob.setText(sdf.format(myCalendar.getTime()));
    }

    private boolean verifyTransaction() {
        String msg="";
        //Toast.makeText(getContext(),msg+" "+branchValue,Toast.LENGTH_LONG).show();
        if(msg.equalsIgnoreCase("error"))
            return false;
        else
            return true;
    }

}