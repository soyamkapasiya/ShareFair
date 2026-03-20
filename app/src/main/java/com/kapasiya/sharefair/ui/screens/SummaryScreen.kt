package com.kapasiya.sharefair.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kapasiya.sharefair.ui.viewmodel.MonthlySpend

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    onBack: () -> Unit,
    viewModel: com.kapasiya.sharefair.ui.viewmodel.SummaryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Spending Summary", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Info */ }) {
                        Icon(Icons.Default.Info, contentDescription = "Info")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is com.kapasiya.sharefair.ui.viewmodel.SummaryUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is com.kapasiya.sharefair.ui.viewmodel.SummaryUiState.Error -> {
                    Text(state.message, modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                }
                is com.kapasiya.sharefair.ui.viewmodel.SummaryUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            TotalExpenseCard(state.totalSpent)
                        }

                        item {
                            SpendingChartSection(state.monthlySpends)
                        }

                        item {
                            CategoryBreakdownSection(state.categorySpends)
                        }

                        item {
                            RecentInsightsSection()
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TotalExpenseCard(totalSpent: Double) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1A237E), Color(0xFF4A148C))
                    )
                )
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Group Spending", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "₹${String.format("%.0f", totalSpent)}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Trending 8% down this month", 
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.White, 
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SpendingChartSection(monthlySpends: List<MonthlySpend>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Monthly Trend", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            TextButton(onClick = { }) {
                Text("Last 6 Months", fontWeight = FontWeight.Bold)
                Icon(Icons.Default.ExpandMore, contentDescription = null)
            }
        }
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    BarChart(monthlySpends)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    monthlySpends.forEach { 
                        Text(it.month.take(3), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BarChart(spends: List<MonthlySpend>) {
    val maxVal = spends.maxOfOrNull { it.amount } ?: 1.0
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val barWidth = 36.dp.toPx()
        val spacing = size.width / spends.size
        
        spends.forEachIndexed { index, spend ->
            val barHeight = (spend.amount / maxVal * size.height).toFloat().coerceAtLeast(10f)
            val x = index * spacing + spacing / 2 - barWidth / 2
            
            drawRect(
                brush = Brush.verticalGradient(listOf(primaryColor, secondaryColor)),
                topLeft = Offset(x, size.height - barHeight),
                size = Size(barWidth, barHeight),
                alpha = 0.8f
            )
            
            // Subtle shadow highlight at the top of the bar
            drawLine(
                color = Color.White.copy(alpha = 0.3f),
                start = Offset(x, size.height - barHeight),
                end = Offset(x + barWidth, size.height - barHeight),
                strokeWidth = 2f
            )
        }
    }
}

@Composable
fun CategoryBreakdownSection(categorySpends: List<com.kapasiya.sharefair.ui.viewmodel.CategorySpend>) {
    val total = categorySpends.sumOf { it.amount }.coerceAtLeast(1.0)
    Column {
        Text("Top Categories", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(16.dp))
        
        val colors = listOf(
            Color(0xFF5C6BC0), // Indigo
            Color(0xFF26A69A), // Teal
            Color(0xFFFF7043), // Deep Orange
            Color(0xFFAB47BC), // Purple
            Color(0xFF8D6E63)  // Brown
        )
        
        categorySpends.forEachIndexed { index, category ->
            val percentage = (category.amount / total * 100).toInt()
            val color = colors[index % colors.size]
            
            Column(modifier = Modifier.padding(vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(modifier = Modifier.size(8.dp), shape = CircleShape, color = color) {}
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(category.category, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("₹${category.amount.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("($percentage%)", color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = color,
                    trackColor = color.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
fun RecentInsightsSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)) {
                Icon(Icons.Default.PieChart, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(10.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("AI Smart Insight", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
                Text(
                    "You saved ₹1,240 by splitting 'Dining' proportionally this month. That's a 15% improvement!",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

data class CategoryItem(val name: String, val percentage: Int, val color: Color)
