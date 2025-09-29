package com.example.mda.data.remote


import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import com.example.mda.BuildConfig

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val newUrl = original.url.newBuilder()
            .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
            .build()

        val request = original.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(request)
    }
}
