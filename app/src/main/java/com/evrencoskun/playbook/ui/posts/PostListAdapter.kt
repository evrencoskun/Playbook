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


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.playbook.R
import com.evrencoskun.playbook.repository.database.entity.PostDataSet
import com.squareup.picasso.Picasso

class PostListAdapter : RecyclerView.Adapter<PostListAdapter.ViewHolder>() {

    var posts: List<PostDataSet> = listOf()

    var onClick: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_post_recyclerview_item, parent, false)
        )

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (posts.isNotEmpty()) {
            holder.data = posts[position]
            holder.postTitleTextView.text = "${holder.data.post?.title}"
            holder.postAuthorTextView.text = holder.itemView.context.getString(
                R.string.authorString,
                holder.data.userList?.get(0)?.name
            )

            Picasso.get()
                .load(holder.data.photoList?.get(0)?.thumbnailUrl)
                .into(holder.postImageView)

            holder.clickListener {
                onClick?.invoke(holder.data.post!!.id - 1)
            }
        }
    }

    fun updatePosts(posts: List<PostDataSet>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var data: PostDataSet
        val postTitleTextView: TextView = itemView.findViewById(R.id.recyclerview_item_textview_title)
        val postAuthorTextView: TextView = itemView.findViewById(R.id.recyclerview_item_textview_user)
        val postImageView: ImageView = itemView.findViewById(R.id.recyclerview_item_imageview)

        fun clickListener(callback: () -> Unit) {
            itemView.setOnClickListener { callback() }
        }
    }
}
