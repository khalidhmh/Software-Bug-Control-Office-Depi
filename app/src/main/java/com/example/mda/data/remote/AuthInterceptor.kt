package com.example.mda.data.remote


import com.example.mda.util.Constants.API_KEY
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // تعديل الطلب لإضافة الـ Bearer token
        val request = original.newBuilder()
            .addHeader("Authorization", API_KEY)
            .build()

        return chain.proceed(request)
    }
}
