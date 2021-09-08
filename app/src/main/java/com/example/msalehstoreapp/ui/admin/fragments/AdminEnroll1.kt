package com.example.msalehstoreapp.ui.admin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.msalehstoreapp.R
import com.example.msalehstoreapp.database.AppDatabase
import com.example.msalehstoreapp.database.entities.BranchDB
import com.example.msalehstoreapp.databinding.AdminEnrollfragBinding
import com.example.msalehstoreapp.viewmodel.adminEnrollViewModels.AdminEnroll1ViewModel
import com.example.msalehstoreapp.viewmodel.adminEnrollViewModels.AdminEnroll1ViewModelFactory
import kotlinx.android.synthetic.main.admin_enrollfrag.*

class AdminEnroll1 : Fragment() {
//    val application = requireNotNull(this.activity).application

    private lateinit var viewModel : AdminEnroll1ViewModel


    private lateinit var binding: AdminEnrollfragBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        try {
            binding = DataBindingUtil.inflate(
                inflater,
                R.layout.admin_enrollfrag,
                container,
                false
            )



            val application = requireNotNull(this.activity).application

            val dataSource = AppDatabase.getInstance(application).branchDAO
            viewModel = ViewModelProvider(this, AdminEnroll1ViewModelFactory(dataSource,application
            )).get(AdminEnroll1ViewModel::class.java)
            binding.enr1viewModel = viewModel
            binding.setLifecycleOwner(viewLifecycleOwner)


            viewModel._navigateToNextFragment.observe(viewLifecycleOwner, Observer {
                if (it) {
                    this.findNavController()
                        .navigate(AdminEnroll1Directions.actionAdminEnroll1ToAdminEnroll2())
                    viewModel._navigateToNextFragment.value = false
                } else {

                }
            })


//            viewModel.branches.observe(viewLifecycleOwner, Observer {
//                if(it.size>0){
////                    var branchAdapter = ArrayAdapter(this.requireContext(),
////                        android.R.layout.select_dialog_item,
////                        it.map { it.branch_name })
////                    branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
////                    sp_branch.setAdapter(branchAdapter)
//                }
//            })

            //binding.enroll1viewModel._navigateToNextFragment//   = adminenroll1ViewModel
        }catch (e: Exception ){
            Log.e("exception",e.toString())
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}