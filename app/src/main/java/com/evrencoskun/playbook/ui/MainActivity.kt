/*
 * Copyright (c) 4/14/19 5:21 AM Evren Coşkun
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

package com.evrencoskun.playbook.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.evrencoskun.playbook.R
import com.evrencoskun.playbook.ui.detail.DetailFragment
import com.evrencoskun.playbook.viewmodel.MainViewModel
import com.evrencoskun.playbook.viewmodel.State
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    // ViewModel must be injected here to allow the same instance to be shared with the fragments
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            return
        }

        val host = NavHostFragment.create(R.navigation.nav_graph)

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_activity_fragment_container, host)
            .setPrimaryNavigationFragment(host)
            .commit()

        logErrors()
    }

    // TODO: Include errors in any future analytics
    private fun logErrors() {
        viewModel.state.observe(this,
            Observer<State> { state ->
                when(state) {
                    is State.Error -> Log.e(javaClass.simpleName, state.message)
                }
            })
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach {
            if (it is NavHostFragment) {
                it.childFragmentManager.fragments.forEach {
                    if (it is DetailFragment) {
                        it.onBackPressed()
                    }
                }
            }
        }
        super.onBackPressed()
    }
}
