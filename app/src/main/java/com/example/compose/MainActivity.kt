package com.example.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.window.layout.DisplayFeature
import com.example.compose.ui.theme.ComposeDemoTheme
import com.google.accompanist.adaptive.calculateDisplayFeatures
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			ComposeDemoTheme {
				val windowSize = calculateWindowSizeClass(this)
				val displayFeatures = calculateDisplayFeatures(this)
				DemoApp(windowSize, displayFeatures)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true)
@Composable
fun DemoAppPreview() {
	ComposeDemoTheme {
		DemoApp(
			windowSize = WindowSizeClass.calculateFromSize(DpSize(400.dp, 900.dp)),
			displayFeatures = emptyList()
		)
	}
}

@Composable
fun DemoApp(
	windowSize: WindowSizeClass,
	displayFeatures: List<DisplayFeature>
) {
	CollapsingScaffold()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingScaffold() {
	// 使用记住的值来保存高度
	var topBarHeight by remember { mutableFloatStateOf(0f) }
	var bottomBarHeight by remember { mutableFloatStateOf(0f) }

	// 记录当前偏移（范围：0（完全显示）到负的组件高度（完全隐藏））
	var topBarOffset by remember { mutableFloatStateOf(0f) }
	// BottomNavigation 隐藏时向下偏移
	var bottomBarOffset by remember { mutableFloatStateOf(0f) }

	// 创建一个 NestedScrollConnection 用于拦截滚动事件
	val nestedScrollConnection = remember {
		object : NestedScrollConnection {
			// 在子组件处理滚动前调用
			override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
				val delta = available.y
				// 当往上滚动（delta < 0），隐藏 TopAppBar；往下滚动则显示
				val newTopOffset = (topBarOffset + delta).coerceIn(-topBarHeight, 0f)
				val consumedY = newTopOffset - topBarOffset
				topBarOffset = newTopOffset

				// BottomNavigation 的逻辑与 TopAppBar 相反：向下滚动隐藏，向上滚动显示
				val newBottomOffset = (bottomBarOffset - delta).coerceIn(0f, bottomBarHeight)
				bottomBarOffset = newBottomOffset

				// 返回已消费的滚动量（这里只消费 y 轴部分）
				return Offset(0f, consumedY)
			}
		}
	}

	// 使用 Accompanist Pager 示例（也可以使用其他 Pager 组件）
	val pagerState = rememberPagerState(pageCount = { 2 })

	Box(
		modifier = Modifier.fillMaxSize(),
		// 通过 Modifier.offset 根据 topBarOffset 实现动态偏移（隐藏效果）
	) {
		// HorizontalPager 也要加上 nestedScroll 修饰符，这样内部滚动事件能传递到 NestedScrollConnection
		HorizontalPager(
			state = pagerState,
			modifier = Modifier
				.fillMaxSize()
				.nestedScroll(nestedScrollConnection)
				.padding(
					top = ((topBarHeight + topBarOffset ) / LocalDensity.current.density).dp,
					bottom = ((bottomBarHeight - bottomBarOffset) / LocalDensity.current.density).dp
				)
		) { page ->
			// 每个页面内部使用可滚动组件，如 LazyColumn
			LazyColumn(modifier = Modifier.fillMaxSize()) {
				items(50) { index ->
					Text(
						text = "页面 $page - 项目 $index",
						modifier = Modifier
							.fillMaxWidth()
							.padding(16.dp)
					)
				}
			}
		}

		TopAppBar(
			modifier = Modifier
				.fillMaxWidth()
				.align(Alignment.TopCenter)
				.offset { IntOffset(x = 0, y = topBarOffset.roundToInt()) }
				.onSizeChanged { size ->
					topBarHeight = size.height.toFloat()
				},
			title = { Text("我的应用") },
			actions = {
				IconButton(onClick = {}) {
					Icon(Icons.Default.Search, contentDescription = "搜索")
				}
			}
		)

		NavigationBar(
			modifier = Modifier
				.fillMaxWidth()
				.align(Alignment.BottomCenter)
				.offset { IntOffset(x = 0, y = bottomBarOffset.roundToInt()) }
				.onSizeChanged { size ->
					bottomBarHeight = size.height.toFloat()
				}
		) {
			NavigationBarItem(
				icon = { Icon(Icons.Default.Home, contentDescription = "首页") },
				selected = true,
				onClick = {}
			)
			NavigationBarItem(
				icon = { Icon(Icons.Default.Favorite, contentDescription = "收藏") },
				selected = false,
				onClick = {}
			)
		}
	}
}
