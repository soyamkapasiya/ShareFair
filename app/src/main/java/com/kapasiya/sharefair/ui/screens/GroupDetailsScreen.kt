package com.kapasiya.sharefair.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kapasiya.sharefair.model.Bill
import com.kapasiya.sharefair.model.Group
import com.kapasiya.sharefair.ui.viewmodel.BillsUiState
import com.kapasiya.sharefair.ui.viewmodel.GroupDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsScreen(
    groupId: String,
    onBack: () -> Unit,
    onAddExpenseClick: (String) -> Unit,
    onChatClick: (String) -> Unit
) {
    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return GroupDetailsViewModel(groupId) as T
        }
    }
    val viewModel: GroupDetailsViewModel = viewModel(factory = factory)
    val group by viewModel.groupFlow.collectAsState()
    val billsUiState by viewModel.billsUiState.collectAsState()
    val simplifiedTransactions by viewModel.simplifiedTransactions.collectAsState()
    
    var showSimplified by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    
    val context = LocalContext.current

    Scaffold(
        topBar = {
            if (isSearchExpanded) {
                TopAppBar(
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search group expenses...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            isSearchExpanded = false
                            searchQuery = ""
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close Search")
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text(group?.name ?: "Group Details", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                painterResource(android.R.drawable.ic_menu_revert), 
                                contentDescription = "back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchExpanded = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = { onChatClick(groupId) }) {
                            Icon(Icons.AutoMirrored.Filled.Message, contentDescription = "Group Chat")
                        }
                        IconButton(onClick = { showSimplified = !showSimplified }) {
                            Icon(
                                imageVector = if (showSimplified) Icons.AutoMirrored.Filled.ListAlt else Icons.Default.Payments, 
                                contentDescription = "Toggle Simplify"
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddExpenseClick(groupId) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            GroupHeader(group)

            // Group-level Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Unsettled", "Item-wise").forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 12.sp) }
                    )
                }
            }

            if (showSimplified) {
                Text(
                    "Clear Balances", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold, 
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
                
                if (simplifiedTransactions.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No outstanding balances! 🙌", color = Color.Gray)
                    }
                } else {
                    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                        items(simplifiedTransactions) { tx ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${tx.fromUserId.take(8)} pays ${tx.toUserId.take(8)}", fontWeight = FontWeight.SemiBold)
                                        Text("₹${String.format("%.2f", tx.amount)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                                    }
                                    
                                    IconButton(onClick = {
                                        Toast.makeText(context, "Smart reminder sent to friend!", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(Icons.Default.NotificationsActive, contentDescription = "Remind", tint = MaterialTheme.colorScheme.primary)
                                    }

                                    Button(
                                        onClick = { viewModel.settleUp(tx.fromUserId, tx.toUserId, tx.amount) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Settle", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    "Recent Expenses", 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.Bold, 
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                when (val state = billsUiState) {
                    is BillsUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                        CircularProgressIndicator() 
                    }
                    is BillsUiState.Error -> Text(state.message, modifier = Modifier.padding(24.dp))
                    is BillsUiState.Success -> {
                        val filteredBills = state.bills.filter { 
                            it.title.contains(searchQuery, ignoreCase = true) 
                        }.filter {
                            when (selectedFilter) {
                                "Unsettled" -> it.splitType != "SETTLEMENT"
                                "Item-wise" -> it.splitType == "ITEM_WISE"
                                else -> true
                            }
                        }

                        if (filteredBills.isEmpty()) {
                            EmptyBillsView()
                        } else {
                            BillsList(filteredBills)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupHeader(group: Group?) {
    if (group == null) return
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                Brush.verticalGradient(
                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                group.name, 
                fontSize = 28.sp, 
                fontWeight = FontWeight.Bold, 
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                "${group.members.size} members • ${if (group.type == "PERSONAL") "Personal Tracking" else "Group Split"}", 
                fontSize = 14.sp, 
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun BillsList(bills: List<Bill>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(bills) { bill ->
            BillItem(bill)
        }
    }
}

@Composable
fun BillItem(bill: Bill) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ListAlt, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    bill.title, 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    if (bill.splitType == "SETTLEMENT") "Settled" else "Paid by You", 
                    fontSize = 12.sp, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                "₹${bill.amount}", 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold, 
                color = if (bill.splitType == "SETTLEMENT") Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun EmptyBillsView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ListAlt, 
            contentDescription = null, 
            modifier = Modifier.size(64.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("No matching expenses found", fontSize = 16.sp, color = Color.Gray)
    }
}
