package com.example.gainsbookjc.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gainsbookjc.AddNewYearButton
import com.example.gainsbookjc.SelectMonthDropdown
import com.example.gainsbookjc.SelectYearDropdown
import com.example.gainsbookjc.viewmodels.SupportViewModel
import com.example.gainsbookjc.viewmodels.supportViewModelFactory

@Composable
fun StatsScreen(
    context: Context,
    navController: NavController
) {
    val TAG = "StatsScreen"
    val supportViewModel: SupportViewModel = viewModel(factory = supportViewModelFactory {
        SupportViewModel(context)
    })
    // Top bar elements
    StatsTopBar(supportViewModel = supportViewModel)
}

@Composable
fun StatsTopBar(supportViewModel: SupportViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary),
        horizontalArrangement = Arrangement.End
    ) {
        // + new year button
        AddNewYearButton(supportViewModel = supportViewModel)
        Spacer(modifier = Modifier.width(10.dp))
        SelectMonthDropdown(supportViewModel = supportViewModel, logViewModel = null, screen = "StatsScreen")
        Spacer(modifier = Modifier.width(10.dp))
        SelectYearDropdown(supportViewModel = supportViewModel, logViewModel = null, screen = "StatsScreen")
        Spacer(modifier = Modifier.width(10.dp))
    }
}

