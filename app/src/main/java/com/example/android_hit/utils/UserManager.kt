package com.example.android_hit.utils

import android.content.Context
import android.content.SharedPreferences

class UserManager( context: Context) {
    private val PREF_NAME = "sharedPrefUserBondoMan"
    private val sharedPref : SharedPreferences
    val editor : SharedPreferences.Editor

    init{
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPref.edit()
    }

    fun putEmail(key:String, value:String){
        editor.putString(key, value).apply()
    }

    fun getEmail(key: String) : String?{
        return sharedPref.getString(key,null)
    }

    fun putPassword(key: String, value:String){
        editor.putString(key, value).apply()
    }

    fun getPassword(key: String):String?{
        return sharedPref.getString(key,null)
    }

    fun deleteUser(){
        editor.clear().apply()
    }
}