/*
 * Copyright (c) 4/14/19 9:21 PM Evren Coskun
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

import android.content.Context
import androidx.room.Room
import org.koin.standalone.KoinComponent

/**
 * Factory for building a database.
 *
 * This class is meant to abstract database creation from the Application class, where it is injected into the rest
 * of the app
 */
class PostDatabaseFactory constructor(val context: Context) : KoinComponent {
    fun createDatabase() : PostDatabase {
        return Room.databaseBuilder(context, PostDatabase::class.java, "post-db").build()
    }
}