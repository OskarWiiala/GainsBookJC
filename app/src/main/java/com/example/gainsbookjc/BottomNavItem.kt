package com.example.gainsbookjc

sealed class BottomNavItem(var title: String, var icon: Int, var screen_route: String ) {
    object LogScreen : BottomNavItem(title = "Log", icon = R.drawable.log_icon_24, "log_screen")
    object StatsScreen : BottomNavItem(title = "Graph", icon = R.drawable.stats_icon_24, "stats_screen")
    object TimerScreen : BottomNavItem(title = "Timer", icon = R.drawable.timer_icon_24, "timer_screen")
    object ProfileScreen : BottomNavItem(title = "Profile", icon = R.drawable.profile_icon_24, "profile_screen")
}
