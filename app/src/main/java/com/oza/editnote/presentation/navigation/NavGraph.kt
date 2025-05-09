package com.oza.editnote.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.oza.editnote.presentation.screen.list.PageListScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "page_list"
    ) {
        composable("page_list") {
            PageListScreen()
        }
    }
}