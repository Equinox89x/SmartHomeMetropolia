package com.example.smarthomeproject.Repositories

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

object ShutterRepository {
    object Model {
        data class Shutter(
            val shutter1:String?
        )
    }

    interface IShutterRepo {
        @POST("shutter1")
        fun potShutterState(@Body action: Model.Shutter): Call<Model.Shutter>
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.140:54691/api/smarthome/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service = retrofit.create(IShutterRepo::class.java)
}

val callbackShutter =object: Callback<ShutterRepository.Model.Shutter> {
    override fun onFailure(call: Call<ShutterRepository.Model.Shutter>, t: Throwable) {
    }

    override fun onResponse(
        call: Call<ShutterRepository.Model.Shutter>,
        response: Response<ShutterRepository.Model.Shutter>
    ) {
    }
}