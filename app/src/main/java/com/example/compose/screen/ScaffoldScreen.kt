package com.example.compose.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.compose.extensions.pxToDp
import com.example.compose.ui.theme.ComposeDemoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldScreen() {

    val topAppBarState = rememberTopAppBarState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    val tabsText = listOf("Menu", "Favorite")
    val tabsIcons = listOf(Icons.Default.Home, Icons.Default.Favorite)

    var topBarHeight by remember { mutableFloatStateOf(0f) }
    val pagerState = rememberPagerState(pageCount = { 2 })

    val animationScope = rememberCoroutineScope()

    val listStates = List(2) { rememberLazyListState() }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MyTopBar(scrollBehavior) { height ->
                topBarHeight = height
            }
        },
        bottomBar = {
            MyBottomBar(
                tabsText = tabsText,
                tabsIcons = tabsIcons,
                pagerState = pagerState,
                animationScope = animationScope,
                listStates = listStates,
                topAppBarState = topAppBarState
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topBarHeight.pxToDp)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabsText.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            if (pagerState.currentPage == index) {
                                animationScope.launch {
                                    listStates[index].scrollToItem(0)
                                }
                            } else {
                                animationScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state = listStates[page],
                    contentPadding = PaddingValues(
                        bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding()
                    )
                ) {
                    items((0..99).toList()) { item ->
                        Text(modifier = Modifier.padding(8.dp), text = "Page $page -- Item $item")
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ScaffoldScreenPreview() {
    ComposeDemoTheme {
        ScaffoldScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onHeightsChanged: (height: Float) -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { size ->
                onHeightsChanged(size.height.toFloat())
            },
        title = { Text(text = "Scroll Behavior Test") },
        navigationIcon = {
            IconButton(onClick = {

            }) {
                Icon(imageVector = Icons.Default.Home, contentDescription = "")
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBottomBar(
    tabsText: List<String>,
    tabsIcons: List<ImageVector>,
    pagerState: PagerState,
    animationScope: CoroutineScope,
    listStates: List<LazyListState>,
    topAppBarState: TopAppBarState
) {
    var navBarHeight by remember { mutableIntStateOf(0) }
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { size ->
                navBarHeight = size.height
            }
            .offset {
                IntOffset(x = 0, y = (navBarHeight * topAppBarState.collapsedFraction).roundToInt())
            },
    ) {
        tabsText.forEachIndexed { index, name ->
            NavigationBarItem(
                icon = { Icon(tabsIcons[index], contentDescription = name) },
                selected = pagerState.currentPage == index,
                onClick = {
                    if (pagerState.currentPage == index) {
                        animationScope.launch {
                            listStates[index].scrollToItem(0)
                        }
                    } else {
                        animationScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                }
            )
        }
    }
}