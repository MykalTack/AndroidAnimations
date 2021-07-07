package com.mykalcuin.composeanimations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TopicDetailViewModel: ViewModel() {

    private val _isAnimating = MutableLiveData(SpiralAnimation.NOT_STARTED)
    val isAnimating: LiveData<SpiralAnimation> = _isAnimating

    fun loadSpiralAnimation() {
        _isAnimating.value = SpiralAnimation.RUNNING
    }

    fun finishSpiralAnimation() {
        _isAnimating.value = SpiralAnimation.FINISHED
    }
}