package com.cuinsolutions.animations

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.constraintlayout.motion.widget.MotionLayout
import com.cuinsolutions.animations.databinding.ActivityTopicDetailsBinding

class TopicDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityTopicDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        when(intent.getSerializableExtra("platform")) {
            MainActivity.PLATFORM.ANDROID -> {
                supportActionBar!!.setBackgroundDrawable(ColorDrawable(getColor(R.color.android_primary)))
            }
            MainActivity.PLATFORM.IOS -> {
                supportActionBar!!.setBackgroundDrawable(ColorDrawable(getColor(R.color.ios_primary)))
            }
        }
        val topic = intent.getSerializableExtra("topic") as HashMap<*, *>
        binding.detailLoadIcon.setImageResource(topic["icon"] as Int)
        binding.detailTitle.text = topic["title"].toString()
        binding.detailSubtitle.text = topic["shortDescription"].toString()
        binding.detailDescription.text = topic["description"].toString()
        binding.detailsMotionLayout.post {
            binding.detailsMotionLayout.addTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    when(p1) {
                        R.id.spiral_end -> {
                            binding.detailsMotionLayout.transitionToState(R.id.icon_translate_end)
                        }
                    }
                }

                override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
            })
            binding.detailsMotionLayout.transitionToEnd()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }

        return true
    }
}