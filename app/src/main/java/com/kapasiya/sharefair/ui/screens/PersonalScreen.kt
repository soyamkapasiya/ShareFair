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
import com.kapasiya.sharefair.model.Bill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalScreen(
    onAddSoloExpense: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Individual Manager", fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddSoloExpense,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Solo Expense")
            }
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
                PersonalWalletCard()
            }
            
            item {
                Text(
                    "CATEGORIES", 
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.Black, 
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            item {
                CategoryGrid()
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "PRIVATE LEDGER", 
                        fontSize = 11.sp, 
                        fontWeight = FontWeight.Black, 
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Text(
                        "Export", 
                        fontSize = 11.sp, 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            items(getMockSoloBills()) { bill ->
                SoloBillItem(bill)
            }
        }
    }
}

@Composable
fun PersonalWalletCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text("Your Solo Spend", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Text("₹8,240", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingDown, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("12% less than last month", color = Color.LightGray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun CategoryGrid() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CategoryItem("Food", Icons.Default.Restaurant, Color(0xFFFF7043), Modifier.weight(1f))
        CategoryItem("Travel", Icons.Default.DirectionsCar, Color(0xFF42A5F5), Modifier.weight(1f))
        CategoryItem("Health", Icons.Default.MedicalServices, Color(0xFF66BB6A), Modifier.weight(1f))
        CategoryItem("Misc", Icons.Default.Category, Color(0xFFAB47BC), Modifier.weight(1f))
    }
}

@Composable
fun CategoryItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(24.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = color)
        }
    }
}

@Composable
fun SoloBillItem(bill: Bill) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Gray.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(bill.title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Text(
                    java.text.SimpleDateFormat("dd MMM, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(bill.timestamp)), 
                    fontSize = 12.sp, 
                    color = Color.Gray
                )
            }
            Text("₹${bill.amount.toInt()}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFFD32F2F))
        }
    }
}

fun getMockSoloBills() = listOf(
    Bill(id="s1", title="Protein Shake", amount=450.0, timestamp=System.currentTimeMillis(), splitType="SOLO"),
    Bill(id="s2", title="Uber to Office", amount=120.0, timestamp=System.currentTimeMillis() - 86400000, splitType="SOLO"),
    Bill(id="s3", title="Netflix Subscription", amount=499.0, timestamp=System.currentTimeMillis() - 172800000, splitType="SOLO")
)
