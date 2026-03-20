package com.kapasiya.sharefair

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.kapasiya.sharefair.model.Group
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
    var showOnboarding by remember { mutableStateOf(true) } // Simulation: true on first launch

    androidx.compose.animation.AnimatedVisibility(
        visible = showOnboarding,
        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically()
    ) {
        OnboardingScreen(onDismiss = { showOnboarding = false })
    }

    if (!showOnboarding) {
        Scaffold(
            bottomBar = {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                val showBottomBar = currentRoute in listOf("home", "friends", "groups", "personal")
                if (showBottomBar) {
                    BottomNavigationBar(navController = navController)
                }
            },
            floatingActionButton = {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                val showFAB = currentRoute in listOf("home", "friends", "groups", "personal")
                if (showFAB) {
                    FloatingActionButton(
                        onClick = { navController.navigate("groups") },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(20.dp),
                        elevation = FloatingActionButtonDefaults.elevation(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Expense")
                    }
                }
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
                        onProfileClick = { navController.navigate("profile") },
                        onAddExpenseClick = { navController.navigate("groups") },
                        onSettleUpClick = { navController.navigate("friends") },
                        onNavigateToPersonal = { navController.navigate("personal") }
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
                composable("personal") {
                    PersonalScreen(
                        onAddSoloExpense = { navController.navigate("add_bill/solo") },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("notifications") {
                    NotificationScreen(
                        notifications = emptyList(), // Provide mock or actual data
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("profile") {
                    ProfileScreen(
                        userName = "User",
                        userEmail = "user@example.com",
                        onBack = { navController.popBackStack() },
                        onSignOut = { /* Handle sign out */ },
                        onNavigateAnalytics = { navController.navigate("summary") }
                    )
                }
                composable("summary") {
                    SummaryScreen(onBack = { navController.popBackStack() })
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
                            navController.navigate("chat/$groupId")
                        }
                    )
                }
                composable(
                    route = "clear_balances/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                ) { entry ->
                    ClearBalancesScreen(
                        groupId = entry.arguments?.getString("groupId") ?: "",
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "chat/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                ) { entry ->
                    ChatScreen(
                        groupId = entry.arguments?.getString("groupId") ?: "",
                        onBack = { navController.popBackStack() }
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
}