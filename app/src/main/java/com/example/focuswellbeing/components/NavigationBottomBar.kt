package com.example.focuswellbeing.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@Composable
fun NavBottomBar(index: Int, onClick: (String) -> Unit) {
    val selectedTab = remember {
        mutableStateOf<Int>(0)
    };
    NavigationBar(containerColor = Color.White) {
        val items = listOf("Home", "Focus", " Limits", "Insights")
        val icons = listOf(
            Icons.Default.Home, Icons.Default.Timer,
            Icons.Default.LockClock,
            Icons.Default.Analytics
        )
        items.forEachIndexed { index, title ->
            NavigationBarItem(
                selected = selectedTab.value == index,
                onClick = {
                    selectedTab.value = index
                    if (index != 0) {
                        onClick(title.lowercase())
                    }
                },
                icon = {
                    Icon(
                        imageVector = icons[index],
                        contentDescription = title
                    )
                },
                label = { Text(title) }
            )
        }
    }
}