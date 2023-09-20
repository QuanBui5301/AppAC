package com.example.appac.API;

import com.example.appac.HomeFragment.Companion.ON_KEY
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Constants.POST_URL;
import com.example.movieapp.utils.Constants.Password
import okhttp3.Interceptor

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class RegisterClient {
    private lateinit var retrofit: Retrofit

    private val requestInterceptor = Interceptor { chain ->
        val url = chain.request()
            .url
            .newBuilder()
            .addQueryParameter("reported", Password)
            .build()

        val request = chain.request()
            .newBuilder()
            .url(url)
            .build()
        return@Interceptor chain.proceed(request)
    }


    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(requestInterceptor)
        .connectTimeout(60,TimeUnit.SECONDS)
        .build()

    fun getClient(): Retrofit {
        retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }
}