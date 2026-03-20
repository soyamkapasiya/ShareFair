package com.kapasiya.sharefair

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.kapasiya.sharefair.ui.viewmodel.GroupsUiState
import com.kapasiya.sharefair.ui.viewmodel.GroupsViewModel

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShareFairTheme {
                MainDashboardScreen(onSignOut = {
                    FirebaseAuth.getInstance().signOut()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    onSignOut: () -> Unit,
    dashboardViewModel: DashboardViewModel = viewModel(),
    groupsViewModel: GroupsViewModel = viewModel()
) {
    val navController = rememberNavController()
    val stats by dashboardViewModel.stats.collectAsState()
    val groupsState by groupsViewModel.uiState.collectAsState()
    
    val tabs = listOf(
        TabItem("Home", Icons.Default.Home, "home"),
        TabItem("Bills", Icons.Default.List, "bills"),
        TabItem("Groups", Icons.Default.Groups, "groups"),
        TabItem("Friends", Icons.Default.Person, "friends")
    )

    var currentRoute by remember { mutableStateOf("home") }
    var showGroupPicker by remember { mutableStateOf(false) }

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
                NavigationBar {
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
        },
        floatingActionButton = {
            if (currentRoute in listOf("home", "bills", "groups", "friends")) {
                ExtendedFloatingActionButton(
                    onClick = { showGroupPicker = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = { Icon(Icons.Default.Add, "Add Expense") },
                    text = { Text("Add Expense", fontWeight = FontWeight.Bold) }
                )
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
                        profileImageUrl = stats.profileImageUrl,
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
                composable("add_friend") { AddFriendScreen(onBack = { navController.popBackStack() }) }
                composable("create_group") { CreateGroupScreen(onBack = { navController.popBackStack() }) }
                composable(
                    "group_details/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                ) { entry ->
                    GroupDetailsScreen(
                        groupId = entry.arguments?.getString("groupId") ?: "",
                        onBack = { navController.popBackStack() },
                        onAddExpenseClick = { id -> navController.navigate("add_bill/$id") }
                    )
                }
                composable(
                    "add_bill/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.StringType })
                ) { entry ->
                    AddBillScreen(
                        groupId = entry.arguments?.getString("groupId") ?: "",
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

    if (showGroupPicker) {
        AlertDialog(
            onDismissRequest = { showGroupPicker = false },
            title = { Text("Select a Group to split") },
            text = {
                when (val state = groupsState) {
                    is GroupsUiState.Success -> {
                        if (state.groups.isEmpty()) {
                            Text("You aren't in any groups yet. Create one first!")
                        } else {
                            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                                items(state.groups) { group ->
                                    ListItem(
                                        headlineContent = { Text(group.name) },
                                        modifier = Modifier.clickable {
                                            showGroupPicker = false
                                            navController.navigate("add_bill/${group.id}")
                                        },
                                        leadingContent = { 
                                            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(40.dp)) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(Icons.Default.Groups, null, tint = MaterialTheme.colorScheme.primary)
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    is GroupsUiState.Loading -> { 
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is GroupsUiState.Error -> { Text("Error loading groups: ${state.message}") }
                    else -> {}
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showGroupPicker = false }) { Text("Cancel") } }
        )
    }

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, dest, _ ->
            currentRoute = dest.route ?: "home"
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }
}

data class TabItem(val title: String, val icon: ImageVector, val route: String)
