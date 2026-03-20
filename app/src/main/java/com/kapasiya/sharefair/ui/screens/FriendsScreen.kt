package com.kapasiya.sharefair.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
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
import com.kapasiya.sharefair.model.User
import com.kapasiya.sharefair.ui.viewmodel.FriendsUiState
import com.kapasiya.sharefair.ui.viewmodel.FriendsViewModel
import com.kapasiya.sharefair.ui.theme.*

@Composable
fun FriendsScreen(
    onAddFriendClick: () -> Unit,
    viewModel: FriendsViewModel = viewModel()
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
                    "My Friends",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                IconButton(
                    onClick = onAddFriendClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Friend")
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is FriendsUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is FriendsUiState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is FriendsUiState.Success -> {
                        if (state.friends.isEmpty()) {
                            EmptyFriendsView()
                        } else {
                            FriendsList(state.friends)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FriendsList(friends: List<User>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(friends) { friend ->
            FriendItem(friend)
        }
    }
}

@Composable
fun FriendItem(friend: User) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
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
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (friend.profileImageUrl.isNotEmpty()) {
                    coil.compose.AsyncImage(
                        model = friend.profileImageUrl,
                        contentDescription = friend.name,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                val balance = friend.totalBalance
                Text(
                    text = if (balance >= 0) "Owes you ₹$balance" else "You owe ₹${-balance}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (balance >= 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Balance Indicator Bar
                val progress = (0.5f + (balance / 2000).coerceIn(-0.5, 0.5)).toFloat()
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (balance >= 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                    trackColor = Color.LightGray.copy(alpha = 0.2f)
                )
            }
            
            IconButton(onClick = { /* Settle with friend */ }) {
                Icon(
                    Icons.Default.ChevronRight, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun EmptyFriendsView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))
            Text("No friends yet. Add some to start splitting!", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
        }
    }
}
