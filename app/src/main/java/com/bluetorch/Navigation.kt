package com.bluetorch

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScreen(navController: NavHostController = rememberNavController()){
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = "BlueTorch")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("ble")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ){ contentPadding ->
        NavHost(
            navController = navController,
            startDestination = "StartView",
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            composable(route = "StartView") {
                MainScreen(navController)
            }
            composable(route = "ble"){
                    BLEScreen()
            }
        }
    }
}

@Preview
@Composable
fun Preview2() {
    NavigationScreen(rememberNavController())
}

