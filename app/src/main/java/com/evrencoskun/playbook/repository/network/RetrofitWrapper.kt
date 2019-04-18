/*
 * Copyright (c) 4/13/19 9:21 PM Evren Coskun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.evrencoskun.playbook.repository.network

import com.evrencoskun.playbook.repository.database.entity.Comment
import com.evrencoskun.playbook.repository.database.entity.Photo
import com.evrencoskun.playbook.repository.database.entity.Post
import com.evrencoskun.playbook.repository.database.entity.User
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Wrapper class for obtaining a Retrofit service
 */
class RetrofitWrapper {
    companion object {
        fun getJsonPlaceholderService(): JsonPlaceholderService = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildLoggingClient())
            .build().create(JsonPlaceholderService::class.java)

        private fun buildLoggingClient(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
            return OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
        }
    }

    interface JsonPlaceholderService {
        @GET("/posts")
        fun getPosts(): Deferred<Response<List<Post>>>

        @GET("/users")
        fun getUsers(): Deferred<Response<List<User>>>

        @GET("/comments")
        fun getComments(): Deferred<Response<List<Comment>>>

        @GET("/photos")
        fun getPhotos(): Deferred<Response<List<Photo>>>
    }
}

