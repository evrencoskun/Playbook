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
 * State class representing the state of the MainActivity
 */
sealed class State {
    data class Error(val message: String) : State()
    data class PostsLoaded(val posts: List<PostDataSet>, val lastSelectedPost: PostDataSet?) : State()
    data class DetailsLoaded(val post: PostDataSet) : State()
    object PostsLoading : State()
    object DetailsLoading : State()
    data class ShowComments(val post: PostDataSet) : State()
    data class HideComments(val post: PostDataSet) : State()
}