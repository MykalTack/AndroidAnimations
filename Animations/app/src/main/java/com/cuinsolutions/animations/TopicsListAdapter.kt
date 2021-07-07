package com.cuinsolutions.animations

import android.animation.ObjectAnimator
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import com.cuinsolutions.animations.databinding.ItemTopicListBinding

class TopicsListAdapter(var topicList: Array<HashMap<String, Any>>, var firstLoad: Boolean): RecyclerView.Adapter<TopicsListAdapter.TopicViewHolder>() {

    var detailListener: ((position: Int) -> Unit)? = null

    inner class TopicViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = ItemTopicListBinding.bind(view)
        val icon = binding.topicListItemIcon
        val title = binding.topicListItemTitle
        val shortDescription = binding.topicListItemShortDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        return TopicViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_topic_list, parent, false))
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.icon.setImageResource(topicList[position]["icon"] as Int)
        holder.title.text = topicList[position]["title"] as String
        holder.shortDescription.text = topicList[position]["shortDescription"] as String
        if (!firstLoad) {
            val fadeInAnimation = AlphaAnimation(0.0f, 1.0f)
            fadeInAnimation.duration = 1500
            holder.itemView.startAnimation(fadeInAnimation)
        }
        holder.itemView.setOnClickListener {
            detailListener!!.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return topicList.count()
    }

    fun showDetail(detailListener: ((position: Int) -> Unit)) {
        this.detailListener = detailListener
    }
}