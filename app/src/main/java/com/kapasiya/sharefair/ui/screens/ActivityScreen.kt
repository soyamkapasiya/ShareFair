package com.kapasiya.sharefair.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen() {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Recent Activity", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Today",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(getMockActivities()) { activity ->
                ActivityItem(activity)
            }
            
            item {
                Text(
                    "Yesterday",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            
            items(getYesterdayMockActivities()) { activity ->
                ActivityItem(activity)
            }
        }
    }
}

@Composable
fun ActivityItem(activity: ActivityData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(activity.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    activity.icon,
                    contentDescription = null,
                    tint = activity.color
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    activity.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    activity.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                activity.time,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

data class ActivityData(
    val title: String,
    val description: String,
    val time: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

fun getMockActivities() = listOf(
    ActivityData("Dinner at Spice Hub", "You added an expense in 'Trip to Goa'", "2:30 PM", Icons.Default.Restaurant, Color(0xFFE91E63)),
    ActivityData("Payment Received", "John Doe settled up with you", "11:45 AM", Icons.Default.Notifications, Color(0xFF4CAF50))
)

fun getYesterdayMockActivities() = listOf(
    ActivityData("Grocery Shopping", "Rahul added an expense in 'Flatmates'", "Yesterday", Icons.Default.Restaurant, Color(0xFF2196F3)),
    ActivityData("New Group Created", "You created 'Weekend Getaway'", "Yesterday", Icons.Default.Notifications, Color(0xFFFF9800))
)
