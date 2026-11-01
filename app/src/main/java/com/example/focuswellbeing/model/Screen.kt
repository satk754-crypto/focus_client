package com.example.focuswellbeing.model

sealed class Screen(val route: String, val title: String, val icon: String) {
    object Focus : Screen("focus_tab", "Focus", "🎯")
    object Health : Screen("health_tab", "Health", "❤️")
    object WalkingTrack : Screen("walking_tab", "Walking", "🚶")
    object Music : Screen("music_tab", "Music", "🎵")
}