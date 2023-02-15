package com.example.gainsbookjc

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gainsbookjc.screens.NavigationGraph
import com.example.gainsbookjc.ui.theme.GainsBookJCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fontFamily = FontFamily(
            Font(R.font.sassyfrass_regular, FontWeight.Normal),
        )
        setContent {
            GainsBookJCTheme {
                MainScreenView(fontFamily, lifecycleScope = lifecycleScope, context = applicationContext)
            }
        }
    }
}

@Composable
fun MainScreenView(fontFamily: FontFamily, lifecycleScope: LifecycleCoroutineScope, context: Context) {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 40.sp,
                            fontFamily = fontFamily
                        )
                    ) {
                        append("G")
                    }
                    append("ains")
                    withStyle(
                        style = SpanStyle(
                            fontSize = 30.sp,
                            fontFamily = fontFamily
                        )
                    ) {
                        append("B")
                    }
                    append("ook")
                },
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(MaterialTheme.colors.primary)
            )
        },
        bottomBar = { BottomNavigation(navController = navController) }
    ) {
        Column(modifier = Modifier.padding(it)) {
            NavigationGraph(navController = navController, lifecycleScope = lifecycleScope, context)
        }
    }
}

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.LogScreen,
        BottomNavItem.StatsScreen,
        BottomNavItem.TimerScreen,
        BottomNavItem.ProfileScreen
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    BottomNavigation() {
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 9.sp
                    )
                },
                alwaysShowLabel = true,
                selected = currentRoute == item.screen_route,
                onClick = {
                    navController.navigate(item.screen_route) {

                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
