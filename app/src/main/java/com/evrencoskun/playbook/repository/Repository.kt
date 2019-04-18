/*
 * Copyright (c) 4/13/19 9:16 PM Evren Coskun
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

package com.evrencoskun.playbook.repository

import com.evrencoskun.playbook.repository.database.DatabaseRepository
import com.evrencoskun.playbook.repository.database.entity.PostDataSet
import com.evrencoskun.playbook.repository.network.NetworkRepository
import com.evrencoskun.playbook.viewmodel.CoroutineContextProvider
import com.evrencoskun.playbook.viewmodel.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import org.koin.standalone.KoinComponent
import kotlin.coroutines.CoroutineContext

/**
 * Repository used to fetch data from either the database or the network.
 *
 * The general rule is that if data cannot be found in the database, it should resort to a network call and
 * finally an error.
 *
 * All requests for data come from the database to ensure a single source of truth.
 */
open class Repository constructor(
    private val databaseRepository: DatabaseRepository,
    private val networkRepository: NetworkRepository,
    private val contextPool: CoroutineContextProvider
) : CoroutineScope, KoinComponent {

    override val coroutineContext: CoroutineContext
        get() = contextPool.io
    
    open fun updatePosts() = async {
        databaseRepository.getPosts({ RequestResult.Success }) {
            networkRepository.getPosts({
                databaseRepository.savePosts(it)
                RequestResult.Success
            }) {
                RequestResult.Error(it)
            }
        }
    }

    open fun updateUsers() = async {
        databaseRepository.getUsers({ RequestResult.Success }) {
            networkRepository.getUsers({
                databaseRepository.saveUsers(it)
                RequestResult.Success
            }) {
                RequestResult.Error(it)
            }
        }
    }

    open fun updateComments() = async {
        databaseRepository.getComments({ RequestResult.Success }) {
            networkRepository.getComments({
                databaseRepository.saveComments(it)
                RequestResult.Success
            }) {
                RequestResult.Error(it)
            }
        }
    }

    open fun updatePhotos() = async {
        databaseRepository.getPhotos({ RequestResult.Success }) {
            networkRepository.getPhotos({
                databaseRepository.savePhotos(it)
                RequestResult.Success
            }) {
                RequestResult.Error(it)
            }
        }
    }

    /**
     * Updates database and retrieves a [PostDataSet] object, later passed through to the result
     */
    suspend fun loadPosts(): PostsRequestResult {
        val dataUpdateError = updateData()

        if (dataUpdateError != null) {
            return dataUpdateError
        }

        var postsWithMetadata: List<PostDataSet>? = null
        val result = databaseRepository.getPostsWithMetadata(
            {
                postsWithMetadata = it
                RequestResult.Success
            })
        { RequestResult.Error(it) }

        if (result is RequestResult.Error) {
            return PostsRequestResult.Error(result.message)
        }

        return PostsRequestResult.Success(postsWithMetadata!!)
    }

    suspend fun updateData(): PostsRequestResult? {
        val postsResult = updatePosts().await()
        val usersResult = updateUsers().await()
        val commentsResult = updateComments().await()
        val photosResult = updatePhotos().await()


        if (postsResult is RequestResult.Error) {
            return PostsRequestResult.Error(postsResult.message)
        }

        if (usersResult is RequestResult.Error) {
            return PostsRequestResult.Error(usersResult.message)
        }

        if (commentsResult is RequestResult.Error) {
            return PostsRequestResult.Error(commentsResult.message)
        }

        if (photosResult is RequestResult.Error) {
            return PostsRequestResult.Error(photosResult.message)
        }

        return null
    }

    suspend fun loadPost(selectedPostIndex: Int): Result {
        val loadPostsResult = loadPosts()
        return when (loadPostsResult) {
            is PostsRequestResult.Success -> {
                databaseRepository.saveLastLoadedPostIndex(selectedPostIndex)
                Result.PostLoadedResult(loadPostsResult.posts[selectedPostIndex])
            }
            is PostsRequestResult.Error -> Result.PostsLoadingError(loadPostsResult.message)
        }
    }

    open suspend fun lastLoadedPost(): PostDataSet? {
        val index = databaseRepository.getLastLoadedPostIndex()
        val result = index?.let { loadPost(it) }

        return when (result) {
            is Result.PostLoadedResult -> result.post
            else -> null
        }
    }

    sealed class RequestResult {
        object Success : RequestResult()
        data class Error(val message: String) : RequestResult()
    }

    sealed class PostsRequestResult {
        data class Success(val posts: List<PostDataSet>) : PostsRequestResult()
        data class Error(val message: String) : PostsRequestResult()
    }
}
