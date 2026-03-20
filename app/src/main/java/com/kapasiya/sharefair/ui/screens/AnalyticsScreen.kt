package com.kapasiya.sharefair.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.kapasiya.sharefair.data.RepositoryModule
import com.kapasiya.sharefair.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnalyticsViewModel : ViewModel() {
    private val userRepository = RepositoryModule.userRepository
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        if (currentUserId.isEmpty()) return
        viewModelScope.launch {
            userRepository.getUserFlow(currentUserId).collect {
                _currentUser.value = it
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onBack: () -> Unit,
    onUpgrade: () -> Unit,
    viewModel: AnalyticsViewModel = viewModel()
) {
    val user by viewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visual Spending Analytics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (user?.isPremium == false) {
            PremiumLockedState(padding, onUpgrade)
        } else {
            AnalyticsContent(padding)
        }
    }
}

@Composable
fun PremiumLockedState(padding: PaddingValues, onUpgrade: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Lock, 
            contentDescription = null, 
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Analytics is a Premium Feature", 
            fontSize = 22.sp, 
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Get deep insights into your spending habits with interactive pie charts and visual graphs.",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onUpgrade,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Upgrade to Premium", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AnalyticsContent(padding: PaddingValues) {
    val categories = listOf(
        SpendingCategory("Food & Dining", 4500.0, Color(0xFFFF5722)),
        SpendingCategory("Travel", 12000.0, Color(0xFF2196F3)),
        SpendingCategory("Rent", 25000.0, Color(0xFF4CAF50)),
        SpendingCategory("Groceries", 3200.0, Color(0xFFFFC107)),
        SpendingCategory("Entertainment", 1500.0, Color(0xFF9C27B0))
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text("Monthly Breakdown", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            SimplePieChart(categories)
        }

        item {
            Text("Spending by Category", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(categories) { category ->
            CategorySpendingItem(category, 25000.0)
        }
    }
}

data class SpendingCategory(val name: String, val amount: Double, val color: Color)

@Composable
fun SimplePieChart(categories: List<SpendingCategory>) {
    val total = categories.sumOf { it.amount }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(180.dp)) {
            var startAngle = 0f
            categories.forEach { category ->
                val sweepAngle = (category.amount / total * 360f).toFloat()
                drawArc(
                    color = category.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 40.dp.toPx())
                )
                startAngle += sweepAngle
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total Spent", fontSize = 12.sp, color = Color.Gray)
            Text("₹${total.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CategorySpendingItem(category: SpendingCategory, max: Double) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp).background(category.color, RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(8.dp))
                Text(category.name, fontWeight = FontWeight.Medium)
            }
            Text("₹${category.amount}", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (category.amount / max).toFloat() },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            color = category.color
        )
    }
}
