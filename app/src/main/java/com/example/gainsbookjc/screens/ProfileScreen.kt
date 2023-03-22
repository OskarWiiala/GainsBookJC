package com.example.gainsbookjc.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gainsbookjc.R
import com.example.gainsbookjc.database.entities.Profile
import com.example.gainsbookjc.viewmodels.ProfileViewModel
import com.example.gainsbookjc.viewmodels.profileViewModelFactory

/**
 * This composable acts as the profile screen of the app.
 * It includes a profile picture, username and about section
 * @param context
 * @author Oskar Wiiala
 */
@Composable
fun ProfileScreen(context: Context) {
    val profileViewModel: ProfileViewModel = viewModel(factory = profileViewModelFactory {
        ProfileViewModel(context)
    })

    val profile by profileViewModel.profile.collectAsState()
    val profilePicture by profileViewModel.profilePicture.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        AsyncImage(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colors.primary,
                    shape = RoundedCornerShape(50)
                )
                .clip(CircleShape),
            model = Uri.parse(profilePicture),
            contentDescription = "profile picture",
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(50.dp),
            text = "${profile.firstOrNull()?.username}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Column(modifier = Modifier
            .fillMaxSize(0.5f)
            .verticalScroll(scrollState)) {
            Text(
                text = "About",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 15.dp, bottom = 25.dp),
                textAlign = TextAlign.Center,
            )
            Text(text = "${profile.firstOrNull()?.description}")
        }
        EditProfileButton(profileViewModel = profileViewModel)
    }
}

/**
 * This button shows a dialog where user can change profile information.
 * @author Oskar Wiiala
 * @param profileViewModel
 */
@Composable
fun EditProfileButton(profileViewModel: ProfileViewModel) {
    // used for showing/hiding dialog for changing profile info
    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        SetProfileDialog(
            profileViewModel = profileViewModel,
            setShowDialog = {
                showDialog = it
            })
    }

    Button(
        contentPadding = PaddingValues(10.dp),
        onClick = { showDialog = true }) {
        Row {
            Icon(
                painter = painterResource(id = R.drawable.edit_icon_24),
                contentDescription = "Edit profile information",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "EDIT", fontSize = 20.sp)
        }
    }
}

/**
 * Dialog where user can change profile information.
 * This includes a profile picture, username and description.
 * Username and description are saved to database, but picture is not due to time restraints.
 * @author Oskar Wiiala
 * @param profileViewModel
 * @param setShowDialog callback to close dialog
 */
@Composable
fun SetProfileDialog(
    profileViewModel: ProfileViewModel,
    setShowDialog: (Boolean) -> Unit
) {
    val profile by profileViewModel.profile.collectAsState()
    val profilePicture by profileViewModel.profilePicture.collectAsState()

    val scrollState = rememberScrollState()

    var usernameTextFieldState by remember {
        mutableStateOf(profile.firstOrNull()?.username)
    }

    var descriptionTextFieldState by remember {
        mutableStateOf(profile.firstOrNull()?.description)
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            profileViewModel.setProfilePicture(uri.toString())
        }
    )

    // Main content
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = BorderStroke(2.dp, MaterialTheme.colors.primary)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Edit profile info")
                Spacer(modifier = Modifier.height(16.dp))

                IconButton(modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(50)
                    )
                    .clip(CircleShape),
                    onClick = {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    AsyncImage(
                        model = Uri.parse(profilePicture),
                        contentDescription = "profile picture",
                        contentScale = ContentScale.Crop
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.6f)
                        .verticalScroll(scrollState)
                ) {
                    TextField(
                        value = usernameTextFieldState ?: "null",
                        onValueChange = { usernameTextFieldState = it },
                        label = { Text(text = "Enter a username here") },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = descriptionTextFieldState ?: "null",
                        onValueChange = { descriptionTextFieldState = it },
                        label = { Text(text = "Enter a description here") },
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Handles OK click
                    Button(onClick = {
                        val newProfile = Profile(
                            userID = 1,
                            username = usernameTextFieldState ?: "null",
                            description = descriptionTextFieldState ?: "null"
                        )

                        profileViewModel.setProfile(newProfile)
                        setShowDialog(false)
                    }) {
                        Text(text = "OK")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { setShowDialog(false) }) {
                        Text(text = "CANCEL")
                    }
                }
            }
        }
    }
}