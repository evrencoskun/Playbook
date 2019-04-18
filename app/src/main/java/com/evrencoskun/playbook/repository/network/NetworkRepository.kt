/*
 * Copyright (c) 4/13/19 9:17 PM Evren Coskun
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

import com.evrencoskun.playbook.repository.Repository
import com.evrencoskun.playbook.repository.database.entity.Comment
import com.evrencoskun.playbook.repository.database.entity.Photo
import com.evrencoskun.playbook.repository.database.entity.Post
import com.evrencoskun.playbook.repository.database.entity.User
import kotlinx.coroutines.Deferred
import org.koin.standalone.KoinComponent
import retrofit2.Response

/**
 * Repository for communicating with the Network via the JsonPlaceHolder API
 */
open class NetworkRepository constructor(private val service: RetrofitWrapper.JsonPlaceholderService) : KoinComponent {

    suspend fun <T, R> processData(
        deferredData: Deferred<Response<List<T>>>,
        success: suspend (List<T>) -> R,
        error: (String) -> R
    ): R {

        val data: Response<List<T>>

        try {
            data = deferredData.await()
        } catch (e: Exception) {
            return error("${javaClass.simpleName}: ${e.message}")
        }

        val body = data.body()

        return if (data.isSuccessful && !body.isNullOrEmpty()) {
            success(body)
        } else {
            error("${javaClass.simpleName}: Fetch Data Unsuccessful")
        }
    }

    open suspend fun getPosts(
        success: suspend (List<Post>) -> Repository.RequestResult,
        error: (String) -> Repository.RequestResult
    ): Repository.RequestResult =
        processData(service.getPosts(), success, error)

    open suspend fun getUsers(
        success: suspend (List<User>) -> Repository.RequestResult,
        error: (String) -> Repository.RequestResult
    ): Repository.RequestResult =
        processData(service.getUsers(), success, error)

    open suspend fun getComments(
        success: suspend (List<Comment>) -> Repository.RequestResult,
        error: (String) -> Repository.RequestResult
    ): Repository.RequestResult =
        processData(service.getComments(), success, error)

    open suspend fun getPhotos(
        success: suspend (List<Photo>) -> Repository.RequestResult,
        error: (String) -> Repository.RequestResult
    ): Repository.RequestResult =
        processData(service.getPhotos(), success, error)
}