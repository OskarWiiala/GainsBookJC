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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gainsbookjc.screens.NavigationGraph
import com.example.gainsbookjc.ui.theme.GainsBookJCTheme

/**
 * @author Oskar Wiiala
 * Main activity of the app
 *
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fontFamily = FontFamily(
            Font(R.font.medievalsharp_regular, FontWeight.Normal),
        )
        setContent {
            GainsBookJCTheme {
                MainScreenView(fontFamily = fontFamily, context = applicationContext)
            }
        }
    }
}

/**
 * @author Oskar Wiiala
 * @param fontFamily
 * @param context
 * Hosts top app bar and bottom naviagtion
 */
@Composable
fun MainScreenView(fontFamily: FontFamily, context: Context) {
    val navController = rememberNavController()
    Scaffold(
        // Title of the app
        topBar = { TopAppBar(fontFamily = fontFamily) },
        // Bottom navigation
        bottomBar = { BottomNavigation(navController = navController) }
    ) {
        Column(modifier = Modifier.padding(it)) {
            NavigationGraph(navController = navController, context = context)
        }
    }
}

/**
 * @author Oskar Wiiala
 * @param fontFamily
 * Top app bar
 */
@Composable
fun TopAppBar(fontFamily: FontFamily) {
    // Annotated string to showcase ease of use in Jetpack Compose
    Box(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontSize = 25.sp,
                        fontFamily = fontFamily
                    )
                ) {
                    append("G")
                }
                append("ains")
                withStyle(
                    style = SpanStyle(
                        fontSize = 25.sp,
                        fontFamily = fontFamily
                    )
                ) {
                    append("B")
                }
                append("ook")
            },
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.secondary,
            fontSize = 24.sp,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
    }
}

/**
 * @author Oskar Wiiala
 * @param navController
 * This composable hosts the bottom navigation and its items
 */
@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.LogScreen,
        BottomNavItem.StatsScreen,
        BottomNavItem.TimerScreen,
        BottomNavItem.ProfileScreen
    )

    // Collects the navigation back stack entry as state
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomNavigation(contentColor = MaterialTheme.colors.secondary, backgroundColor = Color.White) {
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
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
