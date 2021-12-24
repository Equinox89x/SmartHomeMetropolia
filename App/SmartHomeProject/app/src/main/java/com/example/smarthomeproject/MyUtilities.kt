package com.example.smarthomeproject

import android.content.Context
import android.net.ConnectivityManager

object MyUtilities {

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnected == true
    }
}