package com.example.smarthomeproject.Repositories

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log


object RepositoryStates{

    private const val SHARED_STATES: String = "mysharedstates"
    private lateinit var sharedPreferences : SharedPreferences

    var isKitchenLedOn : Boolean
        get() = getBool("isKitchenLedOn")
        set(b) = writeBool("isKitchenLedOn", b)

    var isBedroomLedOn : Boolean
        get() = getBool("isBedroomLedOn")
        set(b) = writeBool("isBedroomLedOn", b)

    var isGarageLedOn
        get() = getBool("isGarageLedOn")
        set(b) = writeBool("isGarageLedOn", b)

    var isLivingroomLedOn
        get() = getBool("isLivingroomLedOn")
        set(b) = writeBool("isLivingroomLedOn", b)

    var isShutter1Open
        get() = getBool("isShutter1Open")
        set(b) = writeBool("isShutter1Open", b)

    var isUnset
        get() = getBool("isUnset")
        set(b) = writeBool("isUnset", b)

    var manualIntervention
        get() = getBool("manualIntervention")
        set(b) = writeBool("manualIntervention", b)


    // important initialisation!
    fun setActivity(a : Activity){
        //TODO write better singleton
        sharedPreferences = a.getSharedPreferences(SHARED_STATES, Context.MODE_PRIVATE)
    }

    private fun getBool(key : String) : Boolean{
        Log.d(key, ""+sharedPreferences.getBoolean(key, false))
        return sharedPreferences.getBoolean(key, false)
    }

    private fun writeBool(key: String, bool : Boolean){
        Log.d(key, ""+bool)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, bool)
        editor.apply()
    }
}

