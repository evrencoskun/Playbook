/*
 * Copyright (c) 4/14/19 5:28 AM Evren Coşkun
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

package com.evrencoskun.playbook.viewmodel

import com.evrencoskun.playbook.repository.database.entity.PostDataSet

/**
 * The Result of an [Action]s operation
 */
sealed class Result {
    data class PostsLoadResult(val posts: List<PostDataSet>, val lastSelectedPost: PostDataSet?): Result()
    data class PostLoadedResult(val post: PostDataSet): Result()
    data class PostsLoadingError(val message: String) : Result()
    data class DetailsLoadingError(val message: String) : Result()
    object PostsLoading : Result()
    object NavigateToDetails : Result()
    data class HideComments(val post: PostDataSet) : Result()
    data class ShowComments(val post: PostDataSet) : Result()

    /**
     * Maps [Result]s to [State]s
     *
     * Note that same results end up with the same state, namely [State.Error]
     */
    class ResultInterpreter : Interpreter<Result, State>() {
        override suspend fun interpret(input: Result, callback: suspend (State) -> Unit) {
            callback(
                when(input) {
                    is PostsLoadResult -> State.PostsLoaded(
                        input.posts,
                        input.lastSelectedPost
                    )
                    is PostsLoading -> State.PostsLoading
                    is NavigateToDetails -> State.DetailsLoading
                    is PostsLoadingError -> State.Error(
                        input.message
                    )
                    is PostLoadedResult -> State.DetailsLoaded(
                        input.post
                    )
                    is DetailsLoadingError -> State.Error(
                        input.message
                    )
                    is HideComments -> State.HideComments(
                        input.post
                    )
                    is ShowComments -> State.ShowComments(
                        input.post
                    )
                }
            )
        }
    }
}