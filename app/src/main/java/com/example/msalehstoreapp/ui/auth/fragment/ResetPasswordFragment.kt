package com.example.msalehstoreapp.ui.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.msalehstoreapp.R
import com.example.msalehstoreapp.database.AppDatabase
import com.example.msalehstoreapp.databinding.FragmentResetPasswordBinding
import com.example.msalehstoreapp.viewmodel.auth.ResetPasswordViewModel
import com.example.msalehstoreapp.viewmodel.auth.ResetPasswordViewModelFactory
import com.example.msalehstoreapp.network.helpers.Resource
import net.simplifiedcoding.ui.handleApiError
import net.simplifiedcoding.ui.visible


class ResetPasswordFragment : Fragment() {


    lateinit var binding : FragmentResetPasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = DataBindingUtil.inflate(
             inflater,
             R.layout.fragment_reset_password,
             container,
             false
         )


        // Get args using by navArgs property delegate
        val fragArgs by navArgs<ResetPasswordFragmentArgs>()


        val application = requireNotNull(this.activity).application

        val dataSource = AppDatabase.getInstance(application).loginDatabaseDao

        val viewModelFactory = ResetPasswordViewModelFactory(
            dataSource,
            application,
            fragArgs.emailString
        )

        val resetPasswordViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(ResetPasswordViewModel::class.java)

        binding.resetViewModel = resetPasswordViewModel

        binding.setLifecycleOwner(this)

        binding.progressbar.visible(false)
        binding.btnReturntoLogin.setOnClickListener(View.OnClickListener {
            this.findNavController()
                .navigate(ResetPasswordFragmentDirections.actionResetPasswordFragmentToLoginFragment())
        })


        resetPasswordViewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            binding.progressbar.visible(it is Resource.Loading)
            if(this.findNavController().currentDestination?.label==this::class.java.simpleName)
            when (it) {
                is Resource.Success -> {
                    if (it.value.contains("success")) {
                        this.findNavController().navigate(
                            ResetPasswordFragmentDirections.actionResetPasswordFragmentToLoginFragment()
                        )
                        Toast.makeText(
                            application.applicationContext,
                            "Password reset successfully!Proceeed to login", Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            application.applicationContext,
                            "Error!"+it.value, Toast.LENGTH_LONG
                        ).show()
                    }

                }
                is Resource.Failure -> handleApiError(it) { }
            }
        })

        binding.btnReset.setOnClickListener(View.OnClickListener {
            if (binding.edtPassword1.text.toString() ==
                binding.edtPassword2.text.toString()
            ) {
                resetPasswordViewModel.resetPass(
                    binding.edtUsername.text.toString(),
                    binding.edtResetCode.text.toString(),
                    binding.edtPassword1.text.toString(),
                    binding.edtPassword2.text.toString()
                )
            } else
                Toast.makeText(
                    application.applicationContext,
                    "Error, Password do not match", Toast.LENGTH_LONG
                ).show()
        })

        return binding.root
    }


}