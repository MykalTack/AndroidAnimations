package com.mykalcuin.composeanimations

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mykalcuin.composeanimations.ui.theme.androidDark
import com.mykalcuin.composeanimations.ui.theme.iosDark

class TopicListViewModel: ViewModel() {

    val androidTopics: Array<HashMap<String, Any>> = arrayOf(hashMapOf("icon" to R.drawable.android_logo, "title" to "Introduction to Kotlin",
        "shortDescription" to "Intro to Kotlin use in Android.", "description" to "Google is pushing Kotlin as the goto language for development by making it a first party supported language."), hashMapOf("icon" to R.drawable.android_logo, "title" to
            "Android Animations", "shortDescription" to "Animating view in Android.", "description" to "Google offers multiple ways to build and use animations to make your app interactive and fun to use.")
    )

    val iOSTopics: Array<HashMap<String, Any>> = arrayOf(hashMapOf("icon" to R.drawable.apple_logo, "title" to "Introduction to Swift",
        "shortDescription" to "Intro to Swift in iOS Development.", "description" to "Swift is Apple's self developed language and current choice for iOS development."),
        hashMapOf("icon" to R.drawable.apple_logo, "title" to "iOS Storyboards", "shortDescription" to "Visualizing layouts with Storyboards.",
            "description" to "Xcode's layout builder tool is the preferred way for developers to visualize their layouts as they are building them."))

    private val _currentTopic = MutableLiveData(androidTopics)
    val currentTopic: LiveData<Array<HashMap<String, Any>>> = _currentTopic

    private val _currentBackground = MutableLiveData(androidDark)
    val currentBackground: LiveData<Color> = _currentBackground

    fun onTopicChange(tab: Int) {
        when (tab) {
            0 -> {
                _currentTopic.value = androidTopics
                _currentBackground.value = androidDark
            }

            1 -> {
                _currentTopic.value = iOSTopics
                _currentBackground.value = iosDark
            }
        }
    }
}