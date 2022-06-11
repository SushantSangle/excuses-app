package xyz.sushant.excusesapp.base.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {

    companion object {
        val apiService by lazy {
            val httpClient = OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .build()

            Retrofit.Builder()
                .baseUrl("https://excuser.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build().create(ApiService::class.java)
        }
    }
}