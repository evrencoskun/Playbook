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

package com.evrencoskun.playbook.repository.database

import com.evrencoskun.playbook.repository.Repository
import com.evrencoskun.playbook.repository.database.entity.*
import org.koin.standalone.KoinComponent

/**
 * Repository for communicating with the Room database
 */
open class DatabaseRepository constructor(private val database: PostDatabase): KoinComponent {

    suspend fun <T,R> processData(
        data: List<T>,
        success: (List<T>) -> R,
        error: suspend (String) -> R) : R {

        return if (!data.isNullOrEmpty()) {
            success(data)
        } else {
            error("${javaClass.simpleName}: Load from Database Error")
        }
    }

    open suspend fun getPosts(success: (List<Post>) -> Repository.RequestResult,
                              error: suspend (String) -> Repository.RequestResult)
            : Repository.RequestResult =
        processData(database.postDao().loadPosts(), success, error)

    open suspend fun getUsers(success: (List<User>) -> Repository.RequestResult,
                              error: suspend (String) -> Repository.RequestResult)
            : Repository.RequestResult =
        processData(database.postDao().loadUsers(), success, error)

    open suspend fun getComments(success: (List<Comment>) -> Repository.RequestResult,
                                 error: suspend (String) -> Repository.RequestResult)
            : Repository.RequestResult =
        processData(database.postDao().loadComments(), success, error)

    open suspend fun getPhotos(success: (List<Photo>) -> Repository.RequestResult,
                               error: suspend (String) -> Repository.RequestResult)
            : Repository.RequestResult =
        processData(database.postDao().loadPhotos(), success, error)

    open suspend fun getPostsWithMetadata(success: (List<PostDataSet>) -> Repository.RequestResult,
                                          error: suspend (String) -> Repository.RequestResult)
            : Repository.RequestResult =
        processData(database.postDao().getPostWithCommentsAndUser(), success, error)

    open fun getLastLoadedPostIndex(): Int? {
        return database.postDao().getLastLoadedPostIndex()?.index
    }

    open fun savePosts(posts: List<Post>) {
        database.postDao().savePosts(posts)
    }

    fun saveUsers(users: List<User>) {
        database.postDao().saveUsers(users)
    }

    fun saveComments(comments: List<Comment>) {
        database.postDao().saveComments(comments)
    }

    fun savePhotos(photos: List<Photo>) {
        database.postDao().savePhotos(photos)
    }

    fun saveLastLoadedPostIndex(selectedPostIndex: Int) {
        database.postDao().replaceLastLoadedPost(LastLoadedPostIndex(selectedPostIndex))
    }

}
