/*
 * Copyright (c) 4/14/19 5:28 AM Evren Coskun
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

import com.evrencoskun.playbook.repository.Repository

/**
 * Action which signifies the action the Activity should take upon receiving an Intent
 */
sealed class Action {
    object LoadPostsAction : Action()
    data class ShowDetailViewAction(val selectedPostIndex: Int) : Action()
    object ShowPostsWithoutLoading : Action()
    data class ShowOrHideComment(val commentVisible: Boolean) : Action()

    /**
     * Interprets [Action]s and returns a result of the action in the form of a [Result]. These actions are largely
     * performed Asynchronously using coroutines.
     */
    class ActionInterpreter(val repository: Repository) : Interpreter<Action, Result>() {

        override suspend fun interpret(input: Action, callback: suspend (Result) -> Unit) {
            callback(
                when (input) {
                    is LoadPostsAction -> {
                        callback(Result.PostsLoading)
                        postRequestResultToResult(repository.loadPosts())
                    }
                    is ShowDetailViewAction -> {
                        callback(Result.NavigateToDetails)
                        repository.loadPost(input.selectedPostIndex)
                    }
                    is ShowPostsWithoutLoading ->
                        postRequestResultToResult(repository.loadPosts())
                    is ShowOrHideComment -> {

                        // We load the last loaded post from the database, as the activity might have been destroyed
                        val lastLoadedPost = repository.lastLoadedPost()

                        when {
                            lastLoadedPost == null -> Result.PostsLoadingError("Last Loading Post is null")
                            input.commentVisible -> Result.HideComments(
                                lastLoadedPost
                            )
                            else -> Result.ShowComments(lastLoadedPost)
                        }
                    }
                }
            )
        }

        private suspend fun postRequestResultToResult(result: Repository.PostsRequestResult): Result {
            return when (result) {
                is Repository.PostsRequestResult.Success ->
                    Result.PostsLoadResult(result.posts, repository.lastLoadedPost())
                is Repository.PostsRequestResult.Error ->
                    Result.PostsLoadingError(result.message)
            }
        }

    }
}