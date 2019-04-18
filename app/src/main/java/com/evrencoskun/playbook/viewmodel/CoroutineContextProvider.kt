/*
 * Copyright (c) 4/14/19 8:27 PM Evren Coşkun
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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * CoroutineContextProvider
 *
 * Class used to abstract out the CouritineContext from users in order to be able to test coroutines
 */
open class CoroutineContextProvider {

    private val job = Job()

    open val main: CoroutineContext by lazy { Dispatchers.Main + job }
    open val io: CoroutineContext by lazy { Dispatchers.IO + job }

    open fun cancelJob() {
        job.cancel()
    }
}