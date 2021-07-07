package com.mykalcuin.composeanimations

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mykalcuin.composeanimations.ui.theme.ComposeAnimationsTheme
import com.mykalcuin.composeanimations.ui.theme.androidDark
import com.mykalcuin.composeanimations.ui.theme.iosDark
import kotlinx.coroutines.launch

enum class LogoState {
    Android,
    iOS
}

class LogoData(x: State<Dp>, backgroundColor: State<Color>) {
    val x by x
    val backgroundColor by backgroundColor
}

class MainActivity : ComponentActivity() {

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeAnimationsTheme {
                Scaffold(floatingActionButton = { AddButton() }) {
                    Column {
                        PlatformTabs()
                        TopicList()
                    }
                }
            }
        }
    }
}

@Composable
fun PlatformTabs(topicListViewModel: TopicListViewModel = viewModel()) {
    var currentTab by remember{mutableStateOf(0)}
    var moveActive by remember { mutableStateOf(false) }
    var currentIcon by remember { mutableStateOf(R.drawable.apple_logo) }
    var firstLoad by remember { mutableStateOf(true) }
    var currentLogoState by remember { mutableStateOf(LogoState.Android) }
    val logoData = logoTransition(logoState = currentLogoState, moveActive = moveActive, firstLoad = firstLoad)
    val logoAnimationsScope = rememberCoroutineScope()
    ConstraintLayout {

        val (logo, tabs) = createRefs()

        TabRow(
            selectedTabIndex = currentTab, modifier = Modifier
                .constrainAs(tabs) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
                .height(48.dp)
                .fillMaxWidth()
                .background(logoData.backgroundColor)
        ) {
            Tab(
                selected = true, onClick = {
                    logoAnimationsScope.launch {
                        moveActive = true
                    }
                    moveActive = false
                    currentTab = 0
                    currentLogoState = LogoState.Android
                    topicListViewModel.onTopicChange(currentTab)
                    currentIcon = R.drawable.android_logo
                }, modifier = Modifier
                    .background(logoData.backgroundColor)
            ) {
                Text(text = "Android")
            }
            Tab(
                selected = false, onClick = {
                    logoAnimationsScope.launch {
                        moveActive = true
                    }
                    moveActive = false
                    currentTab = 1
                    currentLogoState = LogoState.iOS
                    topicListViewModel.onTopicChange(currentTab)
                    currentIcon = R.drawable.apple_logo
                    if (firstLoad) {
                        firstLoad = false
                    }
                }, modifier = Modifier
                    .background(logoData.backgroundColor)
            ) {
                Text(text = "iOS")
            }
        }
        Box(modifier = Modifier
            .constrainAs(logo) {
                top.linkTo(tabs.top)
                bottom.linkTo(tabs.bottom)
            }
            .offset(x = logoData.x)) {
            Logo(currentIcon)
        }
    }
}

@Composable
fun TopicList(topicListViewModel: TopicListViewModel = viewModel()) {
    val currentTopics: Array<HashMap<String, Any>> by topicListViewModel.currentTopic.observeAsState(arrayOf())
    val currentBackground: Color by topicListViewModel.currentBackground.observeAsState(Color.Transparent)
    val backgroundColor by animateColorAsState(targetValue = currentBackground, animationSpec = tween(durationMillis = 1500))
    LazyColumn(modifier = Modifier
        .background(backgroundColor)
        .fillMaxWidth()
        .fillMaxHeight()) {
        items(currentTopics, itemContent = {
            TopicCell(topic = it)
        })
    }
}

@Composable
fun TopicCell(topic: HashMap<String, Any>) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(corner = CornerSize(4.dp)), elevation = 2.dp,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable {
                val detailIntent = Intent(context, TopicDetailActivity::class.java)
                detailIntent.putExtra("topic", topic)
                context.startActivity(detailIntent)
            }
    ) {
        Row {
            Image(
                painter = painterResource(id = topic["icon"] as Int),
                contentDescription = "Topic Icon",
                modifier = Modifier
                    .size(75.dp)
                    .align(Alignment.CenterVertically)
            )
            Column {
                Text(
                    text = topic["title"] as String, modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
                )
                Text(
                    text = topic["shortDescription"] as String, modifier = Modifier
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                )
            }
        }
    }
    //}
}

@Composable
fun AddButton(topicListViewModel: TopicListViewModel = viewModel()) {
    val currentBackground: Color by topicListViewModel.currentBackground.observeAsState(Color.Transparent)
    val backgroundColor by animateColorAsState(targetValue = currentBackground, animationSpec = tween(durationMillis = 1500))
    FloatingActionButton(onClick = { /*TODO*/ }, backgroundColor = backgroundColor, modifier = Modifier
        .size(56.dp)) {
        Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = null, tint = Color.White)
    }
}

@Composable
fun Logo(logoId: Int) {
    Box {
        Image(
            painter = painterResource(id = logoId),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
        )
    }
}

@Composable
fun logoTransition(logoState: LogoState, moveActive: Boolean, firstLoad: Boolean): LogoData {
    val logoTransition = updateTransition(targetState = logoState, label = "logoTransition")
    val logoMovement = logoTransition.animateDp(transitionSpec = {tween(durationMillis = 1500)},
        label = "logoXTransition"
    ) { state ->
        when(state) {
            LogoState.Android -> {
                if (firstLoad) {
                    (-48).dp
                } else {
                    if (moveActive) (-48).dp else LocalConfiguration.current.screenWidthDp.dp + 48.dp
                }
            }
            LogoState.iOS -> {
                if (moveActive) LocalConfiguration.current.screenWidthDp.dp + 48.dp else (-48).dp
            }
        }
    }
    val logoBackground = logoTransition.animateColor(transitionSpec = { tween(durationMillis = 1500) },
        label = "logoColorTransition"
    ) {
        when(it) {
            LogoState.Android -> {
                androidDark
            }
            LogoState.iOS -> {
                iosDark
            }
        }
    }

    return remember(logoTransition) {
        LogoData(logoMovement, logoBackground)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeAnimationsTheme {
        Scaffold(floatingActionButton = { AddButton() }) {
            Column {
                PlatformTabs()
                TopicList()
            }
        }
    }
}