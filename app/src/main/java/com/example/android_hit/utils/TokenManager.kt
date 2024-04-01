package com.example.android_hit.utils

import android.content.Context
import android.content.SharedPreferences

class TokenManager( context: Context) {
    private val PREF_NAME = "sharedPrefBondoMan"
    private val sharedPref : SharedPreferences
    val editor : SharedPreferences.Editor

    init{
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPref.edit()
    }

    fun putToken(key:String, value:String){
        editor.putString(key, value).apply()
    }

    fun getToken(key: String) : String?{
        return sharedPref.getString(key,null)
    }

    fun putIsLogin(key: String, value:Boolean){
        editor.putBoolean(key,value).apply()
    }

    fun isLogin(key:String):Boolean{
        return sharedPref.getBoolean(key,false)
    }

    fun putNIM(key: String, value:String){
        editor.putString(key, value).apply()
    }

    fun getNIM(key: String):String?{
        return sharedPref.getString(key,null)
    }

    fun putIAT(key: String, value: Int){
        editor.putInt(key, value).apply()
    }

    fun getIAT(key: String):Int{
        return  sharedPref.getInt(key,0)
    }

    fun putEXP(key: String, value: Int){
        editor.putInt(key, value).apply()
    }

    fun getEXP(key: String):Int{
        return sharedPref.getInt(key,0)
    }

    fun deleteToken(){
        editor.clear().apply()
    }

    fun getToken(): String? {

        return sharedPref.getString("TOKEN", null)
    }
}