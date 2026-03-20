package com.kapasiya.sharefair.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kapasiya.sharefair.model.Bill
import com.kapasiya.sharefair.model.User
import com.kapasiya.sharefair.ui.theme.*
import com.kapasiya.sharefair.ui.viewmodel.DashboardViewModel

@Composable
fun HomeScreen(
    viewModel: DashboardViewModel = viewModel(),
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAddExpenseClick: () -> Unit = {},
    onSettleUpClick: () -> Unit = {},
    onNavigateToPersonal: () -> Unit = {}
) {
    val stats by viewModel.stats.collectAsState()
    var showChecklist by remember { mutableStateOf(true) }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                HeaderSection(stats.userName, stats.profileImageUrl, onNotificationClick, onProfileClick)
            }
            
            // New User Checklist Feature
            if (showChecklist) {
                item {
                    OnboardingChecklist(onDismiss = { showChecklist = false })
                }
            }

            item {
                BalanceCard(stats.totalBalance, stats.owesYou, stats.youOwe)
            }

            // Real-time Personal Tracking Card (Glassmorphism Effect)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(100.dp).clickable { onNavigateToPersonal() },
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(52.dp), 
                            shape = CircleShape, 
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(14.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Personal Tracking", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Text("Track private spendings", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Text("₹${stats.youOwe * 0.4}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, fontSize = 18.sp)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onAddExpenseClick,
                        modifier = Modifier.weight(1f).height(60.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Expense", fontWeight = FontWeight.Bold)
                    }
                    
                    Button(
                        onClick = onSettleUpClick,
                        modifier = Modifier.weight(1f).height(60.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)), // Darker Forest Green
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Settle Up", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Quick Khata (Lent/Got)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f).height(80.dp).clickable { /* Quick Lent */ },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text("I Gave", fontSize = 12.sp, color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                            Text("Lent (Lene) 🔴", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f).height(80.dp).clickable { /* Quick Got */ },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9).copy(alpha = 0.5f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text("I Got", fontSize = 12.sp, color = Color(0xFF2E7D32).copy(alpha = 0.7f))
                            Text("Got (Dene) 🟢", color = Color(0xFF2E7D32), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                    }
                }
            }

            item {
                QuickActionBubbles(stats.friends)
            }
            item {
                SpendingGraphSection(stats.spendingHistory)
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Recent Activity",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        "See all", 
                        color = MaterialTheme.colorScheme.primary, 
                        fontSize = 12.sp, 
                        modifier = Modifier.clickable { }
                    )
                }
            }
            
            if (stats.recentTransactions.isEmpty()) {
                item {
                    Text(
                        "No recent activity to show", 
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(stats.recentTransactions) { bill ->
                    TransactionItem(bill)
                }
            }
        }
    }
}

@Composable
fun OnboardingChecklist(onDismiss: () -> Unit) {
    val tasks = remember {
        mutableStateListOf(
            "Create your first group" to false,
            "Add a friend" to true,
            "Enable SMS sync" to false,
            "Try item-wise split" to false
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Getting Started", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Complete tasks to earn ShareFair Coins!", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(18.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            tasks.forEach { (task, completed) ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (completed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (completed) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        task, 
                        fontSize = 14.sp,
                        textDecoration = if (completed) TextDecoration.LineThrough else null,
                        color = if (completed) Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderSection(userName: String, profileImageUrl: String, onNotificationClick: () -> Unit, onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.clickable { onProfileClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                if (profileImageUrl.isNotEmpty()) {
                    coil.compose.AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(userName.take(1).uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    "Hello, $userName!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Welcome back to ShareFair",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
        
        Surface(
            modifier = Modifier
                .size(48.dp)
                .clickable { onNotificationClick() },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
        ) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun BalanceCard(total: Double, owesYou: Double, youOwe: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF1A237E), Color(0xFF311B92), Color(0xFF4A148C))
                    )
                )
                .padding(28.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Total Net Balance", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                }
                Text(
                    "₹$total",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha = 0.1f)).padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BalanceInfoItem("Owes you", "₹$owesYou", Color(0xFF81C784))
                    VerticalDivider(modifier = Modifier.height(30.dp), thickness = 1.dp, color = Color.White.copy(alpha = 0.2f))
                    BalanceInfoItem("You owe", "₹$youOwe", Color(0xFFFF8A80))
                }
            }
        }
    }
}

@Composable
fun BalanceInfoItem(label: String, amount: String, color: Color) {
    Column {
        Text(label, fontSize = 12.sp, color = color.copy(alpha = 0.7f))
        Text(amount, fontSize = 16.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun QuickActionBubbles(friends: List<User>) {
    if (friends.isEmpty()) return
    
    Column {
        Text("Frequent Splits", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(friends) { friend ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(64.dp)) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
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
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        friend.name.split(" ").firstOrNull() ?: "", 
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), 
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun SpendingGraphSection(history: List<Pair<String, Double>>) {
    if (history.isEmpty()) return
    
    val lineColor = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Weekly Insights", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (history.isEmpty()) return@Canvas
                    
                    val maxAmount = history.maxOf { it.second }.coerceAtLeast(1.0)
                    val stepWidth = size.width / (history.size - 1)
                    val path = Path()
                    
                    history.forEachIndexed { index, pair ->
                        val x = index * stepWidth
                        val y = size.height - (pair.second / maxAmount * size.height).toFloat()
                        
                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                        
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.1f),
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 1f
                        )
                    }
                    
                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 4f)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(bill: Bill) {
    Card(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) {
                Icon(Icons.Default.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(12.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(bill.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Group shared", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
            }
            Text("₹${bill.amount}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}
