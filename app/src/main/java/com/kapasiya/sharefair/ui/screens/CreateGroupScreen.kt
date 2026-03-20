package com.kapasiya.sharefair.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kapasiya.sharefair.model.User
import com.kapasiya.sharefair.ui.viewmodel.FriendsUiState
import com.kapasiya.sharefair.ui.viewmodel.FriendsViewModel
import com.kapasiya.sharefair.ui.viewmodel.GroupsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    onBack: () -> Unit,
    groupsViewModel: GroupsViewModel = viewModel(),
    friendsViewModel: FriendsViewModel = viewModel()
) {
    var groupName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("General") }
    var groupType by remember { mutableStateOf("GROUP") } // GROUP or PERSONAL
    var simplifyBalances by remember { mutableStateOf(true) }
    val selectedMembers = remember { mutableStateListOf<String>() }
    val context = LocalContext.current
    val friendsState by friendsViewModel.uiState.collectAsState()

    val categories = listOf("Travel", "Home", "Dining", "Groceries", "Entertainment", "General")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Group", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            Text(
                "Group Details", 
                fontWeight = FontWeight.SemiBold, 
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Group Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Group Type:", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(16.dp))
                FilterChip(
                    selected = groupType == "GROUP",
                    onClick = { groupType = "GROUP" },
                    label = { Text("Shared") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = groupType == "PERSONAL",
                    onClick = { groupType = "PERSONAL" },
                    label = { Text("Personal") }
                )
            }

            if (groupType == "GROUP") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = simplifyBalances,
                        onCheckedChange = { simplifyBalances = it }
                    )
                    Text("Simplify Balances", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Category", fontWeight = FontWeight.Medium)
            
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            if (groupType == "GROUP") {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Select Members", 
                    fontWeight = FontWeight.SemiBold, 
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                when (val state = friendsState) {
                    is FriendsUiState.Loading -> CircularProgressIndicator()
                    is FriendsUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                    is FriendsUiState.Success -> {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(state.friends) { friend ->
                                MemberSelectionItem(
                                    friend = friend,
                                    isSelected = selectedMembers.contains(friend.id),
                                    onToggle = {
                                        if (selectedMembers.contains(friend.id)) {
                                            selectedMembers.remove(friend.id)
                                        } else {
                                            selectedMembers.add(friend.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Button(
                onClick = {
                    if (groupName.isNotEmpty()) {
                        groupsViewModel.createGroup(
                            name = groupName, 
                            members = selectedMembers.toList(),
                            type = groupType,
                            simplifyBalances = simplifyBalances
                        ) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (success) onBack()
                        }
                    } else {
                        Toast.makeText(context, "Please enter group name", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create Group", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun MemberSelectionItem(friend: User, isSelected: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(friend.name.take(1).uppercase())
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            friend.name, 
            modifier = Modifier.weight(1f)
        )
        Checkbox(
            checked = isSelected, 
            onCheckedChange = { onToggle() }
        )
    }
}
