package xyz.sushant.excusesapp.base.network

import android.content.Context
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class ApiClient {

    companion object {

        // Actual service object
        private var _apiService : ApiService? = null

        /**
         * Getter for [ApiService] object, it uses lazy loading and always returns a non null object
         */
        @JvmStatic
        val apiService: ApiService
        get() {
            if(_apiService == null) {
                createService(null)
            }

            return _apiService!!
        }

        fun getRetrofit(context: Context? = null): Retrofit = Retrofit.Builder()
            .baseUrl("https://excuser.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(getHttpClient(context))
            .build()

        fun getHttpClient(context: Context? = null): OkHttpClient {
            val httpClientBuilder = OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.MINUTES)
                .addInterceptor(HeaderInterceptor())

            httpClientBuilder.addInterceptor(HeaderInterceptor())
            if(context != null) {
                httpClientBuilder.addInterceptor(ChuckInterceptor(context))
            }

            return httpClientBuilder.build()
        }

        @JvmStatic
        fun createService(app: Context?) {
            _apiService = getRetrofit(app).create(ApiService::class.java)
        }
    }
}