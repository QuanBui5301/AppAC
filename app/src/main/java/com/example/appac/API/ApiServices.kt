package com.example.movieapp.API

import com.example.appac.database.dataP
import com.example.appac.database.graphday
import com.example.appac.database.weather_data
import com.example.movieapp.utils.Constants.API_KEY
import com.example.myapplication.database.database
import retrofit2.Call
import retrofit2.http.*

interface ApiServices {

    @GET("v1/sensordatas")
    fun getData(@Query("latest") latest : Boolean): Call<database>
    @PUT("v1/devices/{id}")
    fun controlDt(@Path("id") id:String) : Call<dataP>
    @GET("v1/devices/{id}")
    fun confirm(@Path("id") id:String) : Call<dataP>
    @GET("v1/sensordatas")
    fun getDayData(@Query("createdAt[gte]") day : String) : Call<graphday>
    @GET("v1/forecast.json")
    fun getWeaData(@Query("q") area : String, @Query("aqi") aqi : String, @Query("alerts") alerts : String) : Call<weather_data>
}