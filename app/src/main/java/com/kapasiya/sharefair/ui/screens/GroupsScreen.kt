package com.kapasiya.sharefair.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kapasiya.sharefair.model.Group
import com.kapasiya.sharefair.ui.theme.*
import com.kapasiya.sharefair.ui.viewmodel.GroupsUiState
import com.kapasiya.sharefair.ui.viewmodel.GroupsViewModel

@Composable
fun GroupsScreen(
    onAddGroupClick: () -> Unit,
    onGroupClick: (Group) -> Unit,
    viewModel: GroupsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Your Groups",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                IconButton(
                    onClick = onAddGroupClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Group")
                }
            }

            // Splitwise Import Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clickable { /* Simulate Import */ },
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1CC29F).copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1CC29F).copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(12.dp), color = Color(0xFF1CC29F).copy(alpha = 0.2f)) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = Color(0xFF1CC29F), modifier = Modifier.padding(10.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Import from Splitwise", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF0C5F4D))
                        Text("Transfer your bills to ShareFair instantly", fontSize = 11.sp, color = Color(0xFF0C5F4D).copy(alpha = 0.7f))
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF1CC29F), modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Collections Section (Trips, Events)
            Text(
                "COLLECTIONS", 
                fontWeight = FontWeight.Black, 
                fontSize = 11.sp, 
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
            
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                items(3) { index ->
                    val color = if (index == 0) Color(0xFF5C6BC0) else if (index == 1) Color(0xFF66BB6A) else Color(0xFFFF7043)
                    val title = if (index == 0) "Goa Trip 🏖️" else if (index == 1) "Flat Rent 🏠" else "Office Party 🍕"
                    
                    Surface(
                        modifier = Modifier.width(180.dp).height(130.dp),
                        shape = RoundedCornerShape(32.dp),
                        color = color.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(2.dp, color.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.Center) {
                            Surface(
                                modifier = Modifier.size(36.dp), 
                                shape = RoundedCornerShape(12.dp), 
                                color = color.copy(alpha = 0.15f)
                            ) {
                                Icon(
                                    if (index == 0) Icons.Default.CardTravel else if (index == 1) Icons.Default.Home else Icons.Default.Restaurant, 
                                    contentDescription = null, 
                                    tint = color, 
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(title, fontWeight = FontWeight.Black, fontSize = 15.sp, color = color)
                            Text("₹${(index + 1) * 5200}", fontSize = 13.sp, color = color.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                        }
                    }
                }
                item {
                    Surface(
                        modifier = Modifier
                            .width(80.dp)
                            .height(130.dp)
                            .clickable { onAddGroupClick() },
                        shape = RoundedCornerShape(32.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                            Text("ADD", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("All Groups", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    GroupsUiState.Idle -> { }
                    is GroupsUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is GroupsUiState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is GroupsUiState.Success -> {
                        if (state.groups.isEmpty()) {
                            EmptyGroupsView()
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.groups) { group ->
                                    GroupItem(group, onGroupClick)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupList(groups: List<Group>, onGroupClick: (Group) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(groups) { group ->
            GroupItem(group, onGroupClick)
        }
    }
}

@Composable
fun GroupItem(group: Group, onGroupClick: (Group) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onGroupClick(group) },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Group, 
                    contentDescription = null, 
                    tint = Color.White, 
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${group.members.size} Members • ${group.type.uppercase()}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                // Activity Tracker / Strength Bar
                LinearProgressIndicator(
                    progress = 0.65f, // Placeholder for group health/activity
                    modifier = Modifier.fillMaxWidth(0.5f).height(4.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f)
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${group.groupBalance.values.sum().toInt()}", 
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black, 
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Total Pot", 
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun EmptyGroupsView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        ) {
            Icon(
                Icons.Default.GroupWork, 
                contentDescription = null, 
                modifier = Modifier.padding(24.dp).size(48.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Ready to Split?", 
            fontWeight = FontWeight.Black, 
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Create a group for your next trip or bill.", 
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 40.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
