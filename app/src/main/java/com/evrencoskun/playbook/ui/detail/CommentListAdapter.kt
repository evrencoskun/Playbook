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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.playbook.R
import com.evrencoskun.playbook.repository.database.entity.Comment

class CommentListAdapter : RecyclerView.Adapter<CommentListAdapter.ViewHolder>() {
    private var commentList: List<Comment>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_detail_comments_item, parent, false)
        )

    override fun getItemCount(): Int = commentList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.commentBodyTextView.text = commentList?.get(position)?.body ?: ""
        holder.commentEmailTextView.text = commentList?.get(position)?.email ?: ""
    }

    fun setComments(commentList: List<Comment>?) {
        this.commentList = commentList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentBodyTextView: TextView = itemView.findViewById(R.id.detail_comment_recyclerview_body_text)
        val commentEmailTextView: TextView = itemView.findViewById(R.id.detail_comment_recyclerview_email_text)
    }
}