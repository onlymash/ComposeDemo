package com.example.compose.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


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