package xyz.sushant.excusesapp.base.network

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val request = original.newBuilder()
            .header("auth", "test-auth-key")
            .header("content-Type", "application/json")
            .build()

        return chain.proceed(request)
    }
}
