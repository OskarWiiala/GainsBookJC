package com.example.gainsbookjc.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gainsbookjc.database.AppDatabase

class StatsViewModel(context: Context): ViewModel() {
    val dao = AppDatabase.getInstance(context).appDao
}

inline fun <VM : ViewModel> statsViewModelFactory(crossinline f: () -> VM) =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = f() as T
    }