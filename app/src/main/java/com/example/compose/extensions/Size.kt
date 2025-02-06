package com.example.compose.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


inline val Float.pxToDp: Dp
	@Composable
	get() = (this / LocalDensity.current.density).dp

inline val Int.pxToDp: Dp
	@Composable
	get() = (this / LocalDensity.current.density).dp