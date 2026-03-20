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
            CenterAlignedTopAppBar(
                title = { Text("Recent Activity", fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Text(
                    "TODAY",
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            
            items(getMockActivities()) { activity ->
                ActivityItem(activity)
            }
            
            item {
                Text(
                    "YESTERDAY",
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
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
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(activity.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    activity.icon,
                    contentDescription = null,
                    tint = activity.color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    activity.title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    activity.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    lineHeight = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                activity.time,
                fontSize = 11.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
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
