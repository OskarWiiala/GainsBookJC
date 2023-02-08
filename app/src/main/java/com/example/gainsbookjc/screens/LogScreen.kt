package com.example.gainsbookjc.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gainsbookjc.R

@Composable
fun LogScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "settings, month, and year here",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 20.sp,
            )
        }
        Row() {
            LazyColumn(modifier = Modifier.fillMaxWidth(0.75f)) {
                /*itemsIndexed(
                    listOf()
                ) { index, string ->
                    Text(
                        text = string,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }*/
                items(20) {
                    WorkoutCard()
                    /*Text(
                        // it refers to the item, which is an Int.
                        // Might as well consider it the index
                        text = "Item $it",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    )*/
                }
            }
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(bottom = 16.dp, start = 16.dp),
                onClick = { Log.d("fab", "fab clicked") },
                contentColor = Color.White,
            ) {
                Text(text = "+", fontSize = 30.sp)
            }
        }
    }
}

@Composable
fun WorkoutCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 5.dp,
        border = BorderStroke(2.dp, MaterialTheme.colors.primary),
    ) {
        Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
            ) {
                Text(text = "8.2.2023", textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold)
                Text(text = "Bench press: 5x5 80 kg 3mr")
                Text(text = "pull up: 10, 8, 6 0 kg 3mr")
                Text(text = "Bicep curl: 12, 10, 8 30 kg 2mr")
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = { Log.d("delete", "clicked delete") }) {
                    Icon(
                        modifier = Modifier.height(32.dp).width(32.dp),
                        painter = painterResource(id = R.drawable.delete_icon_24),
                        contentDescription = "Delete workout"
                    )
                }
                IconButton(onClick = { Log.d("edit", "clicked edit") }) {
                    Icon(
                        modifier = Modifier.height(32.dp).width(32.dp),
                        painter = painterResource(id = R.drawable.edit_icon_24),
                        contentDescription = "Edit workout"
                    )
                }
            }
        }
    }
}
