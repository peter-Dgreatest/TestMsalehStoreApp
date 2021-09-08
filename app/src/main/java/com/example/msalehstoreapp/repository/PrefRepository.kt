package com.example.msalehstoreapp.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.msalehstoreapp.util.*

class PrefRepository (val context: Context){
    private val pref: SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    private val editor = pref.edit()

   // private val gson = Gson()

    private fun String.put(long: Long) {
        editor.putLong(this, long)
        editor.commit()
    }

    private fun String.put(int: Int) {
        editor.putInt(this, int)
        editor.commit()
    }

    private fun String.put(string: String) {
        editor.putString(this, string)
        editor.commit()
    }

    private fun String.put(boolean: Boolean) {
        editor.putBoolean(this, boolean)
        editor.commit()
    }

    private fun String.getLong() = pref.getLong(this, 0)

    private fun String.getInt() = pref.getInt(this, 0)

    private fun String.getString() = pref.getString(this, "")!!

    private fun String.getBoolean() = pref.getBoolean(this, false)


    fun setLoggedIn(isLoggedIn: Boolean) {
        PREF_LOGGED_IN.put(isLoggedIn)
    }

    fun setLoggedIn() = PREF_LOGGED_IN.getBoolean()

    fun setShareMsg(msg: String) {
        PREF_SHARE_MESSAGE.put(msg)
    }



    fun setUserName(name: String) {
        PREF_USERNAME.put(name)
    }


    fun setMail(accountType: String) {
        PREF_CONTACT_EMAIL.put(accountType)
    }

    fun setName(name: String) {
        PREFERENCE_NAME.put(name)
    }


    fun getUserName() =
        PREF_USERNAME.getString()



    fun getAcountType() =
        PREF_ACCOUNTTYPE.getString()



    fun getMail() =
        PREF_CONTACT_EMAIL.getString()


    fun getName() =
        PREFERENCE_NAME.getString()

    fun getLogin() = PREF_LOGGED_IN.getBoolean()

    fun setAcountType(accountType: String) {
        PREF_ACCOUNTTYPE.put(accountType)
    }

    fun clearData() {
        editor.clear()
        editor.commit()
    }
}