package com.example.smarthomeproject.Repositories

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object AlarmRepository {
    object Model {
        data class Alarm(
            val alarm:String?
        )
    }

    interface IAlarmRepo {
        @GET("alarm")
        fun getAlarm(): Call<Model.Alarm>
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.140:54691/api/smarthome/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service = retrofit.create(IAlarmRepo::class.java)
}

object TemperatureRepository {
    object Model {
        data class Temperature(
            val temperature:String?
        )
    }

    interface ITempRepo {
        @GET("temperature")
        fun getTemperature():Call<Model.Temperature>
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.140:54691/api/smarthome/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service = retrofit.create(ITempRepo::class.java)
}