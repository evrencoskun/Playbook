/*
 * Copyright (c) 4/14/19 5:19 AM Evren Coskun
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

package com.evrencoskun.playbook.ui.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.evrencoskun.playbook.R
import com.evrencoskun.playbook.repository.database.entity.PostDataSet
import com.evrencoskun.playbook.viewmodel.Intent
import com.evrencoskun.playbook.viewmodel.MainViewModel
import com.evrencoskun.playbook.viewmodel.State
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_posts.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Fragment containing the list of posts. Observes the [State] emitted by the [MainViewModel].
 */
class PostsFragment : Fragment() {

    private val mainViewModel: MainViewModel by sharedViewModel()
    private var postListAdapter: PostListAdapter? = null
    private var snackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupManualRefresh()
        setupRecyclerView()
        subscribeToOnClick()
        observeState()
    }

    private fun setupManualRefresh() {
        post_list_swipe_refresh_layout.setOnRefreshListener {
            mainViewModel.sendIntent(Intent.LoadPostData)
        }
    }

    private fun setupRecyclerView() {
        if (postListAdapter == null) {
            postListAdapter = PostListAdapter()
        }

        post_list_recyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postListAdapter
        }
    }

    private fun subscribeToOnClick() {
        postListAdapter?.onClick = {
            mainViewModel.sendIntent(Intent.SelectPostIntent(it))
        }
    }

    private fun observeState() {
        mainViewModel.state.removeObservers(this)
        mainViewModel.state.observe(this, Observer { state ->
            when (state) {
                is State.Error -> {
                    setLoadingSpinner(false)
                    showError()
                }
                is State.PostsLoaded -> {
                    setLoadingSpinner(false)
                    hideError()
                    updatePosts(state.posts)
                }
                State.DetailsLoading -> {
                    hideError()
                    navigateToDetail()
                }
                State.PostsLoading -> setLoadingSpinner(true)
            }
        })
    }

    private fun updatePosts(posts: List<PostDataSet>) {
        postListAdapter?.updatePosts(posts)
    }

    private fun showError() {
        snackbar = Snackbar.make(
            post_list_swipe_refresh_layout,
            resources.getString(R.string.error_string), Snackbar.LENGTH_INDEFINITE
        )
        snackbar?.show()
    }

    private fun hideError() {
        snackbar?.dismiss()
    }

    private fun navigateToDetail() {
        view?.findNavController()?.navigate(R.id.action_postsFragment_to_detailFragment)
    }

    private fun setLoadingSpinner(isLoading: Boolean) {
        post_list_swipe_refresh_layout.isRefreshing = isLoading
    }

}
