package com.example.compose.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min


@Composable
fun CustomScrollableTabs(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    selectedTabIndex: Int,
    tabs: List<String>,
    onClickTab: (Int) -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment
    ) {
        Layout(
            content = {
                //This part will not be displayed, just used for calculating the whole with of all tabs
                tabs.forEach { tab ->
                    Text(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 14.dp
                        ),
                        text = tab,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }

                //This part is the real scrollable tab layout
                ScrollableTabRow(
                    modifier = Modifier.fillMaxWidth(),
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 0.dp,
                    divider = {},
                    indicator = { tabPositions ->
                        TabRowDefaults.PrimaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = index == selectedTabIndex,
                            onClick = { onClickTab(index) }
                        ) {
                            Text(
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 14.dp
                                ),
                                text = tab,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        ) { measurables, constraints ->
            //This value comes from androidx.compose.material3 TabRow.kt
            val minTabWidth = 90.dp.roundToPx()

            //find out the width of the screen
            val availableWidth = constraints.maxWidth

            //calculate the sum of all the data elements, the result should be the tab's actual width
            val elements = measurables.subList(0, measurables.size - 1).map { measurable ->
                measurable.measure(constraints)
            }
            val elementWidth = elements.sumOf {
                if (it.width < minTabWidth)
                    minTabWidth
                else
                    it.width
            }

            //let the sum-width above to be the new constraints which is used to measure the tabRow
            val width = min(availableWidth, elementWidth)
            val tabRow = measurables.last().measure(constraints.copy(maxWidth = width))
            val height = tabRow.height

            //report the width and height of this layout. It should only contains the tabs, all data
            //elements shouldn't been taken into account
            layout(width, height) {
                tabRow.placeRelative(0, 0)
            }
        }
    }
}