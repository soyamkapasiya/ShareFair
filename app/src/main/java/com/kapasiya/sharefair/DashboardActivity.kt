package com.kapasiya.sharefair

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.kapasiya.sharefair.ui.screens.*
import com.kapasiya.sharefair.ui.theme.ShareFairTheme
import com.kapasiya.sharefair.ui.components.BottomNavigationBar

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShareFairTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    onNotificationClick = { navController.navigate("notifications") },
                    onProfileClick = { navController.navigate("profile") }
                )
            }
            composable("friends") {
                FriendsScreen(onAddFriendClick = { /* Handle add friend */ })
            }
            composable("groups") {
                GroupsScreen(
                    onGroupClick = { group ->
                        navController.navigate("group_details/${group.id}")
                    },
                    onAddGroupClick = { /* Handle create group */ }
                )
            }
            composable("activity") {
                ActivityScreen()
            }
            composable("notifications") {
                NotificationScreen(
                    notifications = emptyList(), // Provide mock or actual data
                    onBack = { navController.popBackStack() }
                )
            }
            composable("profile") {
                ProfileScreen(
                    userName = "User", // Provide actual user name
                    userEmail = "user@example.com", // Provide actual user email
                    onBack = { navController.popBackStack() },
                    onSignOut = { /* Handle sign out */ }
                )
            }
            composable(
                route = "group_details/{groupId}",
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { entry ->
                GroupDetailsScreen(
                    groupId = entry.arguments?.getString("groupId") ?: "",
                    onBack = { navController.popBackStack() },
                    onAddExpenseClick = { groupId ->
                        navController.navigate("add_bill/$groupId")
                    },
                    onChatClick = { groupId ->
                        // Navigate to chat
                    }
                )
            }
            composable(
                route = "add_bill/{groupId}",
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { entry ->
                AddBillScreen(
                    groupId = entry.arguments?.getString("groupId") ?: "",
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "settle_up/{groupId}/{friendId}/{friendName}/{amount}",
                arguments = listOf(
                    navArgument("groupId") { type = NavType.StringType },
                    navArgument("friendId") { type = NavType.StringType },
                    navArgument("friendName") { type = NavType.StringType },
                    navArgument("amount") { type = NavType.FloatType }
                )
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
                val friendName = backStackEntry.arguments?.getString("friendName") ?: "Friend"
                val amount = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0
                SettleUpScreen(
                    groupId = groupId,
                    friendId = friendId,
                    amount = amount,
                    friendName = friendName,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
