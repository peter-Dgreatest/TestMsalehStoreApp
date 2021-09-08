package com.example.msalehstoreapp.ui.auth.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.msalehstoreapp.R
import com.example.msalehstoreapp.database.AppDatabase
import com.example.msalehstoreapp.databinding.FragmentForgotPasswordBinding
import com.example.msalehstoreapp.viewmodel.auth.ForgotPasswordViewModel
import com.example.msalehstoreapp.viewmodel.auth.ForgotPasswordViewModelFactory
import com.example.msalehstoreapp.network.helpers.Resource
import net.simplifiedcoding.ui.handleApiError
import net.simplifiedcoding.ui.visible

class ForgotPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding : FragmentForgotPasswordBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_forgot_password,
            container,
            false)


        val application = requireNotNull(this.activity).application
//
        val dataSource = AppDatabase.getInstance(application).loginDatabaseDao
//
        val viewModelFactory = ForgotPasswordViewModelFactory(dataSource, application)

        val fpViewModel =
            ViewModelProvider(
                this,viewModelFactory).get(ForgotPasswordViewModel::class.java)

        binding.rpviewmodel = fpViewModel

        binding.setLifecycleOwner(this)

        binding.progressbar.visible(false)
        binding.btnLogin.setOnClickListener(View.OnClickListener {
            this.findNavController().navigateUp()
        })


        binding.linkVcode.setOnClickListener(View.OnClickListener {
            this.findNavController()
                .navigate(ForgotPasswordFragmentDirections
                    .actionForgotPasswordFragmentToResetPasswordFragment(""))
        })

        fpViewModel.forgotPasswordResponse.observe(viewLifecycleOwner, Observer {
            binding.progressbar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    Log.e("error",it.toString())
                    fpViewModel.navigate()
                }
                is Resource.Failure -> handleApiError(it) {
                    Log.e("error",it.toString()) }
            }
        })

        fpViewModel.success.observe(viewLifecycleOwner, Observer {
            if(it){
                try {
                    if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED)
                    this.findNavController().navigate(
                        ForgotPasswordFragmentDirections
                            .actionForgotPasswordFragmentToResetPasswordFragment(
                                binding.edtUsername.text.toString()
                            )

                    )
                    fpViewModel.doneNavigating()
                }catch (exception:Throwable){
                    Log.e("errortttt",exception.stackTraceToString())
                }
            }
        })


        binding.btnGetResetCode.setOnClickListener(View.OnClickListener {
            fpViewModel.getvcode(binding.edtUsername.text.toString())
        })

        return binding.root
    }
//
//    override fun onDestroy() {
//        Log.e("ddd","destroyed")
//        super.onDestroy()
//    }
//
//    override fun onDetach() {
//        Log.e("ddd","detached")
//        super.onDetach()
//    }
//
//    override fun onAttach(context: Context) {
//        Log.e("ddd","attached")
//        super.onAttach(context)
//    }
//
//    override fun onDestroyView() {
//        Log.e("ddd","destroyedView")
//        super.onDestroyView()
//    }
//
//    override fun onStop() {
//        Log.e("ddd","stopped")
//        super.onStop()
//    }
//    override fun onStart() {
//        Log.e("ddd","started")
//        super.onStart()
//    }
}