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

package com.evrencoskun.playbook.ui.detail

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.evrencoskun.playbook.R
import com.evrencoskun.playbook.repository.database.entity.PostDataSet
import com.evrencoskun.playbook.viewmodel.Intent
import com.evrencoskun.playbook.viewmodel.MainViewModel
import com.evrencoskun.playbook.viewmodel.State
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail_initial.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


/**
 * Fragment containing the Detail view of a given [Post]
 *
 * Observes the [State] emitted by the [MainViewModel].
 */
class DetailFragment : Fragment() {

    private val mainViewModel: MainViewModel by sharedViewModel()

    private val initialConstraint = ConstraintSet()
    private val commentsShownConstraint = ConstraintSet()
    private val commentsHiddenConstraint = ConstraintSet()
    var applyInitialConstraint = {
        commentsHiddenConstraint.applyTo(detail_constraintlayout)
    }

    private var snackbar: Snackbar? = null
    private val commentsAdapter = CommentListAdapter()
    private var commentsVisible = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail_initial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        disableRefresh()
        setupRecyclerView()
        observeViewModelState()
        setupAnimations(view)
        setupCommentClickListener()
    }

    private fun setupRecyclerView() {
        comments_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentsAdapter
        }
    }

    private fun disableRefresh() {
        detail_view_swipe_refresh_layout.setOnRefreshListener {
            if (detail_view_swipe_refresh_layout.isRefreshing) {
                detail_view_swipe_refresh_layout.isRefreshing = false
            }
        }
    }

    private fun observeViewModelState() {
        mainViewModel.state.removeObservers(this)
        mainViewModel.state.observe(this,
            Observer { state ->
                when (state) {
                    is State.Error -> {
                        setLoadingSpinner(false)
                        showError()
                    }
                    is State.DetailsLoaded -> {
                        hideError()
                        setLoadingSpinner(false)
                        setFields(state.post)
                    }
                    State.DetailsLoading -> {
                        hideError()
                        setLoadingSpinner(true)
                    }
                    is State.ShowComments -> {
                        setFields(state.post)
                        showComments()
                    }
                    is State.HideComments -> {
                        setFields(state.post)
                        hideComments()
                    }
                    is State.PostsLoaded -> {
                        // If we're in this state, it's because the app was restarted!
                        if (state.lastSelectedPost != null) {
                            setFields(state.lastSelectedPost)
                        } else {
                            showError()
                        }

                    }
                }
            })
    }

    private fun setupAnimations(view: View) {
        initialConstraint.clone(detail_constraintlayout)
        commentsShownConstraint.clone(context, R.layout.fragment_detail)
        commentsHiddenConstraint.clone(context, R.layout.fragment_detail_default)

        view.post {
            animateIn()
        }
    }

    private fun animateIn() {
        TransitionManager.beginDelayedTransition(detail_constraintlayout)
        applyInitialConstraint()
    }

    private fun setupCommentClickListener() {
        detail_hitbox.setOnClickListener {
            mainViewModel.sendIntent(Intent.CommentTapped(commentsVisible))
        }
    }

    private fun setFields(data: PostDataSet) {
        detail_name_text_view.text = resources.getString(R.string.authorString, data.userList?.get(0)?.name)
        detail_body_text_view.text = data.post?.body
        detail_title_text_view.text = data.post?.title

        Picasso.get()
            .load(data.photoList?.get(0)?.url)
            .into(detail_imageview)

        commentsAdapter.setComments(data.commentList)

        val numComments = data.commentList?.size ?: 0
        detail_num_comments_text_view.text = resources.getQuantityString(R.plurals.comments, numComments, numComments)
    }

    private fun showComments() {
        TransitionManager.beginDelayedTransition(detail_constraintlayout)
        applyInitialConstraint = {
            commentsShownConstraint.applyTo(detail_constraintlayout)
        }
        applyInitialConstraint()
        commentsVisible = true
    }

    private fun hideComments() {
        TransitionManager.beginDelayedTransition(detail_constraintlayout)
        applyInitialConstraint = {
            commentsHiddenConstraint.applyTo(detail_constraintlayout)
        }
        applyInitialConstraint()
        commentsVisible = false
    }

    private fun showError() {
        snackbar = Snackbar.make(
            detail_view_swipe_refresh_layout,
            resources.getString(R.string.error_string), Snackbar.LENGTH_INDEFINITE
        )
        snackbar?.show()
    }

    private fun hideError() {
        snackbar?.dismiss()
    }

    private fun setLoadingSpinner(isLoading: Boolean) {
        detail_view_swipe_refresh_layout.isRefreshing = isLoading
    }

    fun onBackPressed() {
        mainViewModel.sendIntent(Intent.LeaveDetailIntent)
    }

}
