package com.example.msalehstoreapp.domain

data class Users(val userName : String,
                 val passWord : String,
                 val userType : String,
                 val name:String,
                 val phone:String,
                 val id : Long) {
}

data class LoginCred(val userName: String
,val pass : String) {

}