package com.chiris.app.restaurant_rate.data.network

import com.chiris.app.restaurant_rate.utils.Constants
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val okHttpClient = okhttp3.OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .build()

    val retrofit: retrofit2.Retrofit = retrofit2.Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
