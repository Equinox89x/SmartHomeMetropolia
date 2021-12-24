package com.example.smarthomeproject.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.smarthomeproject.Home
import com.example.smarthomeproject.HomeDB

class SmarthomeViewModel(application: Application): AndroidViewModel(application) {
    private val homeinfo: LiveData<Home> = HomeDB.get(getApplication()).homeDao().getInfo()
    fun getHomeInfo() = homeinfo
}