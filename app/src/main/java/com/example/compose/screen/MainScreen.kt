package com.example.compose.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.compose.extensions.pxToDp
import com.example.compose.ui.theme.ComposeDemoTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    val topAppBarState = rememberTopAppBarState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    var topBarHeight by remember { mutableIntStateOf(0) }

    var selectedPageIndex by remember { mutableIntStateOf(0) }

    val pages = PageType.entries.map { type ->
        Page(
            type = type,
            tabs = type.getTabs()
        )
    }

    val pagerStates = pages.map { page ->
        rememberPagerState(pageCount = { page.tabs.size })
    }

    val listStatesList = pages.map { page ->
        page.tabs.map {
            rememberLazyListState()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MyTopBar(scrollBehavior) { height ->
                topBarHeight = height
            }
        },
        bottomBar = {
            MyBottomBar(
                topAppBarState = topAppBarState,
                selectedPageIndex = selectedPageIndex
            ) { index ->
                selectedPageIndex = index
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topBarHeight.pxToDp)
        ) {

            MyTabRow(
                tabs = pages[selectedPageIndex].tabs,
                pagerState = pagerStates[selectedPageIndex],
                scrollToTop = { index ->
                    listStatesList[selectedPageIndex][index].animateScrollToItem(0)
                }
            )

            MyPager(
                page = pages[selectedPageIndex],
                pagerState = pagerStates[selectedPageIndex],
                listStates = listStatesList[selectedPageIndex]
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ScaffoldScreenPreview() {
    ComposeDemoTheme {
        MainScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onHeightsChanged: (height: Int) -> Unit
) {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { size ->
                onHeightsChanged(size.height)
            },
        title = { Text(text = "BooruHub") },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBottomBar(
    topAppBarState: TopAppBarState,
    selectedPageIndex: Int,
    onSelectedScreenChanged: (index: Int) -> Unit
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
        PageType.entries.forEachIndexed { index, type ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selectedPageIndex == index) type.getSelectedIcon() else type.getIcon(),
                        contentDescription = type.getDisplayName()
                    )
                },
                selected = selectedPageIndex == index,
                onClick = {
                    onSelectedScreenChanged(index)
                }
            )
        }
    }
}

@Composable
fun MyTabRow(
    tabs: List<String>,
    pagerState: PagerState,
    scrollToTop: suspend (index: Int) -> Unit
) {

    val animationScope = rememberCoroutineScope()

    CustomScrollableTabs(
        modifier = Modifier.fillMaxWidth(),
        selectedTabIndex = pagerState.currentPage,
        contentAlignment = Alignment.Center,
        tabs = tabs,
        onClickTab = { index ->
            if (pagerState.currentPage == index) {
                animationScope.launch {
                    scrollToTop(index)
                }
            } else {
                animationScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        }
    )

    HorizontalDivider(Modifier.fillMaxWidth())
}

@Composable
fun MyPager(
    page: Page,
    pagerState: PagerState,
    listStates: List<LazyListState>
) {

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { index ->

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listStates[index],
            contentPadding = PaddingValues(
                bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding()
            )
        ) {
            items((0..99).toList()) { item ->
                Text(modifier = Modifier.padding(8.dp), text = "${page.tabs[index]} $item")
            }
        }
    }
}