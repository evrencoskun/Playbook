/*
 * Copyright (c) 4/13/19 9:18 PM Evren Coskun
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

import androidx.room.*
import com.evrencoskun.playbook.repository.database.entity.*

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePosts(posts: List<Post>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUsers(posts: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveComments(posts: List<Comment>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePhotos(photos: List<Photo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLastLoadedPost(lastLoadedPostIndex: LastLoadedPostIndex)

    @Query("DELETE FROM lastLoadedPostIndex")
    fun deleteLastLoadedPosts()

    @Transaction
    fun replaceLastLoadedPost(lastLoadedPostIndex: LastLoadedPostIndex) {
        deleteLastLoadedPosts()
        saveLastLoadedPost(lastLoadedPostIndex)
    }

    @Query("SELECT * FROM post")
    fun loadPosts(): List<Post>

    @Query("SELECT * FROM user")
    fun loadUsers(): List<User>

    @Query("SELECT * FROM comment")
    fun loadComments(): List<Comment>

    @Query("SELECT * from post")
    fun getPostWithCommentsAndUser(): List<PostDataSet>

    @Query("SELECT * from photo")
    fun loadPhotos(): List<Photo>

    @Query("SELECT * from lastLoadedPostIndex LIMIT 1")
    fun getLastLoadedPostIndex(): LastLoadedPostIndex?

}