/*
 * Copyright (c) 4/13/19 8:33 PM Evren Coskun
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

package com.evrencoskun.playbook

import android.app.Application
import com.evrencoskun.playbook.repository.Repository
import com.evrencoskun.playbook.repository.database.DatabaseRepository
import com.evrencoskun.playbook.repository.database.PostDatabaseFactory
import com.evrencoskun.playbook.repository.network.NetworkRepository
import com.evrencoskun.playbook.repository.network.RetrofitWrapper
import com.evrencoskun.playbook.viewmodel.*
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext

/**
 * Class used primarily for creating and holding the Koin Module for Dependency Injection
 */
@Suppress("unused")
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        StandAloneContext.startKoin(listOf(module {
            single { Intent.IntentInterpreter() }
            single { Action.ActionInterpreter(get()) }
            single { Result.ResultInterpreter() }
            single { RetrofitWrapper.getJsonPlaceholderService() }
            single { NetworkRepository(get()) }
            single { DatabaseRepository(PostDatabaseFactory(applicationContext).createDatabase()) }
            single { Repository(get(), get(), get()) }
            single { CoroutineContextProvider() }
            viewModel { MainViewModel(get(), get(), get(), get()) }
        }))
    }
}