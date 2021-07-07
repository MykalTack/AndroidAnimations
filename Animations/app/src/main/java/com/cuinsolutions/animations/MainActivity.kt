package com.cuinsolutions.animations

import android.animation.*
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuinsolutions.animations.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    enum class PLATFORM {
        ANDROID,
        IOS
    }

    lateinit var binding: ActivityMainBinding
    lateinit var currentTopics: Array<HashMap<String, Any>>
    lateinit var detailAdapter: TopicsListAdapter
    lateinit var currentPlatform: PLATFORM

    val androidTopics: Array<HashMap<String, Any>> = arrayOf(hashMapOf("icon" to R.drawable.android_logo, "title" to "Introduction to Kotlin",
        "shortDescription" to "Intro to Kotlin use in Android.", "description" to "Google is pushing Kotlin as the goto language for development by making it a first party supported language."), hashMapOf("icon" to R.drawable.android_logo, "title" to
        "Android Animations", "shortDescription" to "Animating view in Android.", "description" to "Google offers multiple ways to build and use animations to make your app interactive and fun to use.")
    )

    val iOSTopics: Array<HashMap<String, Any>> = arrayOf(hashMapOf("icon" to R.drawable.apple_logo, "title" to "Introduction to Swift",
        "shortDescription" to "Intro to Swift in iOS Development.", "description" to "Swift is Apple's self developed language and current choice for iOS development."),
        hashMapOf("icon" to R.drawable.apple_logo, "title" to "iOS Storyboards", "shortDescription" to "Visualizing layouts with Storyboards.",
        "description" to "Xcode's layout builder tool is the preferred way for developers to visualize their layouts as they are building them."))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.platformTabsLayout.setSelectedTabIndicatorColor(getColor(android.R.color.black))
        binding.platformTabsLayout.setTabTextColors(getColor(R.color.ios_dark), getColor(android.R.color.black))
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        detailAdapter = TopicsListAdapter(androidTopics, true)
        currentTopics = androidTopics
        currentPlatform = PLATFORM.ANDROID
        binding.platformTopicList.layoutManager = layoutManager
        binding.platformTopicList.adapter = detailAdapter
        detailAdapter.showDetail { position ->
            detailIntent(position)
        }
        binding.platformTopicList.viewTreeObserver.addOnPreDrawListener {

            for (child in 0 until binding.platformTopicList.childCount) {
                val topic = binding.platformTopicList.getChildAt(child)
                val fadeInAnimator = ObjectAnimator.ofFloat(topic, View.ALPHA, 1f)
                fadeInAnimator.duration = 2000
                fadeInAnimator.start()
            }
            true
        }

        binding.platformTabsLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> {
                        moveAndroid()
                        currentTopics = androidTopics
                        detailAdapter.topicList = androidTopics
                        detailAdapter.firstLoad = false
                        currentPlatform = PLATFORM.ANDROID
                        detailAdapter.notifyDataSetChanged()
                        val actionBarAnimator = ValueAnimator.ofObject(ArgbEvaluator(), getColor(R.color.ios_primary), getColor(R.color.android_primary))
                        actionBarAnimator.addUpdateListener {
                            supportActionBar!!.setBackgroundDrawable(ColorDrawable(it.animatedValue as Int))
                        }
                        val tabBarBackgroundAnimator = ObjectAnimator.ofArgb(binding.platformTabsLayout, "backgroundColor",
                            getColor(R.color.ios_primary), getColor(R.color.android_primary))
                        val detailsAnimator = ObjectAnimator.ofArgb(binding.platformTopicList, "backgroundColor",
                            getColor(R.color.ios_primary), getColor(R.color.android_primary))
                        val addFabAnimator = ValueAnimator.ofObject(ArgbEvaluator(), getColor(R.color.ios_dark), getColor(R.color.android_dark))
                        addFabAnimator.addUpdateListener {
                            binding.addTopic.backgroundTintList = ColorStateList.valueOf(it.animatedValue as Int)
                        }
                        val animationSet = AnimatorSet()
                        animationSet.playTogether(actionBarAnimator, tabBarBackgroundAnimator, detailsAnimator, addFabAnimator)
                        animationSet.duration = 1500
                        animationSet.interpolator = AccelerateInterpolator(1f)
                        animationSet.start()
                        binding.platformTabsLayout.setTabTextColors(getColor(R.color.ios_dark), getColor(android.R.color.black))
                    }
                    1 -> {
                        moveApple()
                        currentTopics = iOSTopics
                        detailAdapter.topicList = iOSTopics
                        detailAdapter.firstLoad = false
                        currentPlatform = PLATFORM.IOS
                        detailAdapter.notifyDataSetChanged()
                        val actionBarAnimator = ValueAnimator.ofObject(ArgbEvaluator(), getColor(R.color.android_primary), getColor(R.color.ios_primary))
                        actionBarAnimator.addUpdateListener {
                            supportActionBar!!.setBackgroundDrawable(ColorDrawable(it.animatedValue as Int))
                        }
                        val tabBarBackgroundAnimator = ObjectAnimator.ofArgb(binding.platformTabsLayout, "backgroundColor",
                        getColor(R.color.android_primary), getColor(R.color.ios_primary))
                        val detailsAnimator = ObjectAnimator.ofArgb(binding.platformTopicList, "backgroundColor",
                        getColor(R.color.android_primary), getColor(R.color.ios_primary))
                        val addFabAnimator = ValueAnimator.ofObject(ArgbEvaluator(), getColor(R.color.android_dark), getColor(R.color.ios_dark))
                        addFabAnimator.addUpdateListener {
                            binding.addTopic.backgroundTintList = ColorStateList.valueOf(it.animatedValue as Int)
                        }

                        val animationSet = AnimatorSet()
                        animationSet.playTogether(actionBarAnimator, tabBarBackgroundAnimator, detailsAnimator, addFabAnimator)
                        animationSet.duration = 1500
                        animationSet.interpolator = AccelerateInterpolator(1f)
                        animationSet.start()
                        binding.platformTabsLayout.setTabTextColors(getColor(R.color.android_dark), getColor(android.R.color.black))
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    private fun moveAndroid() {
        val androidMover = ObjectAnimator.ofFloat(binding.androidLogo, View.TRANSLATION_X,
             binding.androidLogo.width.toFloat(), (binding.platformTabsLayout.width.toFloat() + binding.androidLogo.width.toFloat()) * -1)
        androidMover.interpolator = AccelerateInterpolator(1f)
        androidMover.duration = 1500
        androidMover.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                tabsEnabled(false)
            }
            override fun onAnimationEnd(animation: Animator?) {
                tabsEnabled(true)
                binding.androidLogo.x = binding.platformTabsLayout.width.toFloat()
            }
        })
        androidMover.start()
    }

    private fun moveApple() {
        val appleMover = ObjectAnimator.ofFloat(binding.appleLogo, View.TRANSLATION_X,
            binding.appleLogo.x, binding.platformTabsLayout.width.toFloat() + binding.appleLogo.width.toFloat())
        appleMover.interpolator = AccelerateInterpolator(1f)
        appleMover.duration = 1500
        appleMover.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                tabsEnabled(false)
            }
            override fun onAnimationEnd(animation: Animator?) {
                tabsEnabled(true)
                binding.appleLogo.x = binding.platformTabsLayout.x - binding.appleLogo.width
            }
        })
        appleMover.start()
    }

    private fun tabsEnabled(enabled: Boolean) {
        binding.platformTabsLayout.getTabAt(0)?.view?.isEnabled = enabled
        binding.platformTabsLayout.getTabAt(1)?.view?.isEnabled = enabled
    }

    private fun detailIntent(position: Int) {
        val detailIntent = Intent(this, TopicDetailsActivity::class.java)
        detailIntent.putExtra("topic", currentTopics[position])
        detailIntent.putExtra("platform", currentPlatform)
        startActivity(detailIntent)
    }
}