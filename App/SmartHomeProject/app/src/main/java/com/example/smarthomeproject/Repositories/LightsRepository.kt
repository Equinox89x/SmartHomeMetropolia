package com.example.smarthomeproject.Repositories

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

object LightRepository {
    object Model {
        data class Kitchen(
            val kitchenled:String?
        )
        data class Bedroom(
            val bedroomled:String?
        )
        data class Garage(
            val garageled:String?
        )
        data class Livingroom(
            val livingroomled:String?
        )
    }

    interface ILightRepo {
        @POST("kitchenled")
        fun postKitchenLight(@Body action: Model.Kitchen):Call<Model.Kitchen>

        @POST("bedroomled")
        fun postBedroomLight(@Body action: Model.Bedroom):Call<Model.Bedroom>

        @POST("livingroomled")
        fun postLivingroomLight(@Body action: Model.Livingroom):Call<Model.Livingroom>

        @POST("garageled")
        fun postGarageLight(@Body action: Model.Garage): Call<Model.Garage>
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.140:54691/api/smarthome/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service = retrofit.create(ILightRepo::class.java)
}


val callbackKitchen =object: Callback<LightRepository.Model.Kitchen> {
    override fun onFailure(call: Call<LightRepository.Model.Kitchen>, t: Throwable) {
        Log.d("ffs", t.toString())    }

    override fun onResponse(
        call: Call<LightRepository.Model.Kitchen>,
        response: Response<LightRepository.Model.Kitchen>
    ) {
    }
}


val callbackBedroom =object: Callback<LightRepository.Model.Bedroom> {
    override fun onFailure(call: Call<LightRepository.Model.Bedroom>, t: Throwable) {
    }

    override fun onResponse(
        call: Call<LightRepository.Model.Bedroom>,
        response: Response<LightRepository.Model.Bedroom>
    ) {
    }
}

val callbackGarage =object: Callback<LightRepository.Model.Garage> {
    override fun onFailure(call: Call<LightRepository.Model.Garage>, t: Throwable) {
    }

    override fun onResponse(
        call: Call<LightRepository.Model.Garage>,
        response: Response<LightRepository.Model.Garage>
    ) {
    }
}

val callbackLivingroom =object: Callback<LightRepository.Model.Livingroom> {
    override fun onFailure(call: Call<LightRepository.Model.Livingroom>, t: Throwable) {
    }

    override fun onResponse(
        call: Call<LightRepository.Model.Livingroom>,
        response: Response<LightRepository.Model.Livingroom>
    ) {
    }

}