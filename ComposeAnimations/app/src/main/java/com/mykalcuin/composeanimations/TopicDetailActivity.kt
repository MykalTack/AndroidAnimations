package com.mykalcuin.composeanimations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mykalcuin.composeanimations.ui.theme.ComposeAnimationsTheme
import kotlinx.coroutines.launch
import kotlin.math.log

enum class SpiralAnimation {
    NOT_STARTED,
    RUNNING,
    FINISHED
}

class TopicDetailActivity : ComponentActivity() {

    private val topicDetailViewModel: TopicDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val topic = intent.getSerializableExtra("topic") as  HashMap<*, *>
        setContent {
            ComposeAnimationsTheme {
                Scaffold(modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()) {
                    DetailLogo(logoId = topic["icon"] as Int)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        topicDetailViewModel.loadSpiralAnimation()
    }
}



data class Position(val x: Dp, val y: Dp)

@Composable
fun DetailLogo(logoId: Int, topicDetailViewModel: TopicDetailViewModel = viewModel()) {
    val isAnimatingSpiral by topicDetailViewModel.isAnimating.observeAsState(SpiralAnimation.NOT_STARTED)
    val context = LocalConfiguration.current
    val dataLoadAnimationRoutine = rememberCoroutineScope()
    val logoPosition = remember { mutableStateOf(Position(x = (0.dp), y = (context.screenHeightDp / 2).dp)) }
    val alpha = remember { Animatable(0f) }
    val logoSize = remember { mutableStateOf(48.dp) }
    val animateLogoMove: State<Position> = animateValueAsState(targetValue = if (isAnimatingSpiral == SpiralAnimation.FINISHED) Position(24.dp, 24.dp) else Position(context.screenWidthDp.dp /2, context.screenHeightDp.dp / 2),
        typeConverter = TwoWayConverter(convertToVector = {
            AnimationVector2D(it.x.value, it.y.value)
        }, convertFromVector = {
            logoPosition.value = Position(it.v1.dp, it.v2.dp)
            logoPosition.value
        }), animationSpec = tween(3000, easing = FastOutSlowInEasing))
    val targetPosition = Position((LocalConfiguration.current.screenWidthDp / 2).dp, y = (LocalConfiguration.current.screenHeightDp / 2).dp)
    val animateSpiralPosition: State<Position> = animateValueAsState(targetValue = if (isAnimatingSpiral == SpiralAnimation.RUNNING) targetPosition else logoPosition.value,
        typeConverter = TwoWayConverter(convertToVector = {
            AnimationVector2D(it.x.value, it.y.value)
        }, convertFromVector = {
            logoPosition.value = Position(it.v1.dp, it.v2.dp)
            logoPosition.value
        }), animationSpec = keyframes {
            durationMillis = 5000
            Position(x = (context.screenWidthDp / 2).dp, y = 0.dp) at 454
            Position(x = context.screenWidthDp.dp, y = (context.screenHeightDp / 2).dp) at 908
            Position(x = (context.screenWidthDp / 2).dp, y = context.screenHeightDp.dp) at 1362
            Position(x = (context.screenWidthDp * 0.2).dp, y = (context.screenHeightDp / 2).dp) at 1816
            Position(x = (context.screenWidthDp / 2).dp, y = (context.screenHeightDp * 0.2).dp) at 2270
            Position(x = (context.screenWidthDp * 0.8).dp, y = (context.screenHeightDp / 2).dp) at 2724
            Position(x = (context.screenWidthDp / 2).dp, y = (context.screenHeightDp * 0.8).dp) at 3178
            Position(x = (context.screenWidthDp * 0.4).dp, y = (context.screenHeightDp / 2).dp) at 3632
            Position(x = (context.screenWidthDp / 2).dp, y = (context.screenHeightDp * 0.4).dp) at 4086
            Position(x = (context.screenWidthDp * 0.6).dp, y = (context.screenHeightDp / 2).dp) at 4540
            Position(x = (context.screenWidthDp / 2).dp, y = (context.screenHeightDp * 0.55).dp) at 4994
        }, finishedListener = {
            dataLoadAnimationRoutine.launch {
                logoPosition.value = Position(context.screenWidthDp.dp / 2, context.screenHeightDp.dp / 2)
                topicDetailViewModel.finishSpiralAnimation()
                launch {
                    logoSize.value = 96.dp
                }
                launch {
                    alpha.animateTo(targetValue = 1f, animationSpec = tween(3000))
                }
            }
        })
    Column() {
        Row(modifier = Modifier
            .padding(8.dp)
            .wrapContentHeight()
            .fillMaxWidth()) {
            Icon(
                painter = painterResource(id = logoId),
                contentDescription = null,
                modifier = Modifier
                    .offset(
                        x = if (topicDetailViewModel.isAnimating.value != SpiralAnimation.FINISHED) {
                            animateSpiralPosition.value.x - 24.dp
                        } else {
                            animateLogoMove.value.x - 24.dp
                               },
                        y = if (topicDetailViewModel.isAnimating.value != SpiralAnimation.FINISHED) {
                            animateSpiralPosition.value.y - 24.dp
                        } else {
                            animateLogoMove.value.y - 24.dp
                        }
                    )
                    .size(logoSize.value)
                    .animateContentSize(animationSpec = tween(3000))
            )
            Column(modifier = Modifier
                .alpha(alpha.value)
                .padding(8.dp)
                .wrapContentHeight()
                .fillMaxWidth()) {
                Text(text = "Title")
                Text(text = "Short Description")
            }
        }
        Text(text = "Long Description", modifier = Modifier
            .alpha(alpha.value)
            .padding(16.dp)
            .wrapContentHeight()
            .fillMaxWidth())
    }
}

@Composable
fun Title() {
    Row(modifier = Modifier
        .wrapContentHeight()
        .fillMaxWidth()) {
        Text(text = "Title")
    }
}

@Composable
fun ShortDescription() {
    Row(modifier = Modifier
        .wrapContentHeight()
        .wrapContentWidth()) {
        Text(text = "Short Description")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    ComposeAnimationsTheme {
        Column(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()) {
            DetailLogo(logoId = R.drawable.android_logo)
        }
    }
}