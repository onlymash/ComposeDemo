package com.example.compose.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


inline val Float.pxToDp: Dp
    @Composable
    get() = (this / LocalDensity.current.density).dp

inline val Int.pxToDp: Dp
    @Composable
    get() = (this / LocalDensity.current.density).dp

inline val Dp.dpToPx: Float
    @Composable
    get() = this.value * LocalDensity.current.density

inline val Dp.dpToSp: TextUnit
    @Composable
    get() = (this.value * LocalDensity.current.density / LocalDensity.current.fontScale).sp

inline val Int.pxToSp: TextUnit
    @Composable
    get() = (this / LocalDensity.current.density / LocalDensity.current.fontScale).sp

inline val Float.pxToSp: TextUnit
    @Composable
    get() = (this / LocalDensity.current.density / LocalDensity.current.fontScale).sp

