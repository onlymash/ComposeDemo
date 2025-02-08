package com.example.compose.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun MyTabRow(
    tabs: List<String>,
    pagerState: PagerState,
    scrollToTop: suspend (index: Int) -> Unit
) {

    val animationScope = rememberCoroutineScope()

    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        modifier = Modifier.fillMaxWidth(),
        edgePadding = 0.dp,
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    if (pagerState.currentPage == index) {
                        animationScope.launch {
                            scrollToTop(index)
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
}