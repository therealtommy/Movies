package com.example.movies.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ApiInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        // Добавляем ключ в URL как параметр запроса
        val urlWithApiKey: HttpUrl = originalRequest.url().newBuilder()
            .addQueryParameter("apikey", apiKey)
            .build()
        // Создаем новый запрос с обновленным URL
        val newRequest = originalRequest.newBuilder()
            .url(urlWithApiKey)
            .build()
        return chain.proceed(newRequest)
    }
}
