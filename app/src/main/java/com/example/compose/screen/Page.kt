package com.example.compose.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.ui.graphics.vector.ImageVector

enum class PageType {
    HOME, BOORUS, DOWNLOADS
}

data class Page(
    val type: PageType,
    val tabs: List<String>
)

fun PageType.getTabs(): List<String> {
    return when (this) {
        PageType.HOME -> listOf("Post", "Popular", "Pools", "Tags")
        PageType.BOORUS -> listOf("All", "Danbooru", "Moebooru", "Gelbooru")
        PageType.DOWNLOADS -> listOf("All", "Completed", "Queued", "Failed")
    }
}

fun PageType.getDisplayName(): String {
    return when (this) {
        PageType.HOME -> "Home"
        PageType.BOORUS -> "Boorus"
        PageType.DOWNLOADS -> "Downloads"
    }
}

fun PageType.getSelectedIcon(): ImageVector {
    return when (this) {
        PageType.HOME -> Icons.Default.Home
        PageType.BOORUS -> Icons.Default.Inbox
        PageType.DOWNLOADS -> Icons.Default.Download
    }
}

fun PageType.getIcon(): ImageVector {
    return when (this) {
        PageType.HOME -> Icons.Outlined.Home
        PageType.BOORUS -> Icons.Outlined.Inbox
        PageType.DOWNLOADS -> Icons.Outlined.Download
    }
}