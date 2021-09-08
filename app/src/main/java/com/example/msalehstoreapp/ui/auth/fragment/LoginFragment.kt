package com.example.msalehstoreapp.ui.auth.fragment

import android.content.Intent
import android.os.Bundle
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
import com.example.msalehstoreapp.databinding.FragmentLoginBinding
import com.example.msalehstoreapp.domain.LoginCred
import com.example.msalehstoreapp.ui.admin.AdminDasboardActivity
import com.example.msalehstoreapp.viewmodel.auth.LoginViewModel
import com.example.msalehstoreapp.viewmodel.auth.LoginViewModelFactory
import com.example.msalehstoreapp.network.helpers.Resource
import net.simplifiedcoding.ui.handleApiError
import net.simplifiedcoding.ui.visible

class LoginFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentLoginBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false)



        val application = requireNotNull(this.activity).application

        val dataSource = AppDatabase.getInstance(application).loginDatabaseDao

        val viewModelFactory = LoginViewModelFactory(dataSource, application)

        val loginViewModel =
            ViewModelProvider(
                this, viewModelFactory).get(LoginViewModel::class.java)

        binding.viewModel = loginViewModel
        binding.setLifecycleOwner(this)


        binding.progressbar.visible(false)
        binding.tvForgotPassword.setOnClickListener(View.OnClickListener {
            if(viewLifecycleOwner.lifecycle.currentState==Lifecycle.State.RESUMED)
            this.findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
            loginViewModel.doneNavigatingLogin()
        })

        loginViewModel.loginResponse.observe(viewLifecycleOwner, Observer{
            binding.progressbar.visible(it is Resource.Loading)
            if(viewLifecycleOwner.lifecycle.currentState==Lifecycle.State.RESUMED)
            when (it) {
                is Resource.Success -> {
                        loginViewModel.saveAccessTokens(
                            it.value
                        )
                        startActivity(Intent(application.applicationContext,AdminDasboardActivity::class.java))
                    }
                is Resource.Failure -> handleApiError(it) {  }
            }
                loginViewModel.doneNavigatingLogin()

        })

        binding.btnLogin.setOnClickListener(View.OnClickListener {
            loginViewModel.loginUser(LoginCred(binding.edtUsername.text.toString(),
            binding.edtPassword.text.toString()))
        })

        return binding.root
    }



    fun loginUser(){
    }
}