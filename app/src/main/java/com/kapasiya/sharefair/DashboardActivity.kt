package com.kapasiya.sharefair

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.kapasiya.sharefair.ui.theme.ShareFairTheme
import com.kapasiya.sharefair.ui.screens.*
import com.kapasiya.sharefair.model.Notification
import com.kapasiya.sharefair.ui.viewmodel.DashboardViewModel

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShareFairTheme {
                MainDashboardScreen(onSignOut = {
                    FirebaseAuth.getInstance().signOut()
                    // Restart logic or navigate to Login
                    val intent = Intent(this@DashboardActivity, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                })
            }
        }
    }
}

@Composable
fun MainDashboardScreen(
    onSignOut: () -> Unit,
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val navController = rememberNavController()
    val stats by dashboardViewModel.stats.collectAsState()
    
    val tabs = listOf(
        TabItem("Home", Icons.Default.Home, "home"),
        TabItem("Bills", Icons.Default.List, "bills"),
        TabItem("Groups", Icons.Default.Groups, "groups"),
        TabItem("Friends", Icons.Default.Person, "friends")
    )

    var currentRoute by remember { mutableStateOf("home") }
    
    // Notifications could also be moved to a ViewModel later
    val notifications = remember {
        listOf(
            Notification("Dinner Split", "Vineet added a new bill for Dinner", "2m ago"),
            Notification("Payment Received", "Sahil paid you ₹500", "1h ago"),
            Notification("Group Created", "You were added to 'Goa Trip'", "3h ago"),
            Notification("Reminder", "Rent payment is due tomorrow", "1d ago")
        )
    }

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf("home", "bills", "groups", "friends")) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                if (currentRoute != tab.route) {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.title) },
                            label = { Text(tab.title) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { 
                    HomeScreen(
                        viewModel = dashboardViewModel,
                        onNotificationClick = { navController.navigate("notifications") },
                        onProfileClick = { navController.navigate("profile") }
                    ) 
                }
                composable("profile") {
                    ProfileScreen(
                        userName = stats.userName,
                        userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "user@sharefair.com",
                        onBack = { navController.popBackStack() },
                        onSignOut = onSignOut
                    )
                }
                composable("notifications") {
                    NotificationScreen(
                        notifications = notifications,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("bills") { BillsScreen() }
                composable("groups") {
                    GroupsScreen(
                        onAddGroupClick = { navController.navigate("create_group") },
                        onGroupClick = { group -> navController.navigate("group_details/${group.id}") }
                    )
                }
                composable("friends") {
                    FriendsScreen(
                        onAddFriendClick = { navController.navigate("add_friend") }
                    )
                }
                composable("add_friend") {
                    AddFriendScreen(onBack = { navController.popBackStack() })
                }
                composable("create_group") {
                    CreateGroupScreen(onBack = { navController.popBackStack() })
                }
                composable(
                    route = "group_details/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                    GroupDetailsScreen(
                        groupId = groupId,
                        onBack = { navController.popBackStack() },
                        onAddExpenseClick = { id -> navController.navigate("add_bill/$id") }
                    )
                }
                composable(
                    route = "add_bill/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                    AddBillScreen(
                        groupId = groupId,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "settle_up/{friendId}/{friendName}/{amount}",
                    arguments = listOf(
                        navArgument("friendId") { type = NavType.StringType },
                        navArgument("friendName") { type = NavType.StringType },
                        navArgument("amount") { type = NavType.FloatType }
                    )
                ) { backStackEntry ->
                    val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
                    val friendName = backStackEntry.arguments?.getString("friendName") ?: "Friend"
                    val amount = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0
                    SettleUpScreen(
                        friendId = friendId,
                        amount = amount,
                        friendName = friendName,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
    
    // Update current route
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            currentRoute = destination.route ?: "home"
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }
}

data class TabItem(val title: String, val icon: ImageVector, val route: String)
