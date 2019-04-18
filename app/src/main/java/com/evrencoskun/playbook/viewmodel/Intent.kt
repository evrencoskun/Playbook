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

/**
 * Immutable object which represent an view's intent
 */
sealed class Intent {
    object LoadPostData : Intent()
    object LeaveDetailIntent : Intent()
    data class CommentTapped(val commentVisible: Boolean) : Intent()
    data class SelectPostIntent(val selectedPostIndex: Int) : Intent()

    /**
     * Maps [Intent]s to [Action]s
     */
    class IntentInterpreter : Interpreter<Intent, Action>() {
        override suspend fun interpret(input: Intent, callback: suspend (Action) -> Unit) {
            callback(
                when (input) {
                    is LoadPostData -> Action.LoadPostsAction
                    is SelectPostIntent -> Action.ShowDetailViewAction(
                        input.selectedPostIndex
                    )
                    LeaveDetailIntent -> Action.ShowPostsWithoutLoading
                    is CommentTapped -> Action.ShowOrHideComment(
                        input.commentVisible
                    )
                }
            )
        }
    }
}
