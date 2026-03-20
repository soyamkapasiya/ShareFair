package com.kapasiya.sharefair.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kapasiya.sharefair.model.User
import com.kapasiya.sharefair.ui.theme.*
import com.kapasiya.sharefair.ui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBillScreen(
    groupId: String,
    onBack: () -> Unit,
    billViewModel: BillViewModel = viewModel(),
    groupDetailsViewModel: GroupDetailsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return GroupDetailsViewModel(groupId) as T
            }
        }
    )
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var splitType by remember { mutableStateOf("EQUAL") }
    val selectedParticipants = remember { mutableStateListOf<String>() }
    val exactAmounts = remember { mutableStateMapOf<String, String>() }
    val billItems = remember { mutableStateListOf<com.kapasiya.sharefair.model.BillItem>() }
    
    val isSolo = groupId == "solo"
    val group by groupDetailsViewModel.groupFlow.collectAsState()
    val members by groupDetailsViewModel.members.collectAsState()
    val uiState by billViewModel.uiState.collectAsState()
    var isFetching by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(group) {
        group?.members?.let { membersIds ->
            if (selectedParticipants.isEmpty()) {
                selectedParticipants.addAll(membersIds)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add an expense", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isFetching = true
                        // Simulate Fetching
                        val mockItems = listOf(
                            com.kapasiya.sharefair.model.BillItem(name = "Zinger Burger", price = 180.0),
                            com.kapasiya.sharefair.model.BillItem(name = "Pepsi 500ml", price = 60.0),
                            com.kapasiya.sharefair.model.BillItem(name = "French Fries", price = 120.0)
                        )
                        // Wait 2 seconds
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            billItems.clear()
                            billItems.addAll(mockItems)
                            title = "Lunch (Zomato)"
                            amount = "360.0"
                            splitType = "ITEM_WISE"
                            isFetching = false
                            Toast.makeText(context, "Fetched last order from Zomato!", Toast.LENGTH_SHORT).show()
                        }, 2000)
                    }) {
                        Icon(Icons.Default.CloudDownload, contentDescription = "Fetch Online", tint = MaterialTheme.colorScheme.primary)
                    }
                    
                    TextButton(
                        onClick = {
                            val total = amount.toDoubleOrNull() ?: 0.0
                            if (total > 0 && title.isNotEmpty() && selectedParticipants.isNotEmpty()) {
                                handleSplitAndSave(
                                    total, title, splitType, selectedParticipants, 
                                    exactAmounts, billViewModel, groupId, context, onBack,
                                    billItems
                                )
                            } else {
                                Toast.makeText(context, "Check inputs... total, title & users", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = uiState !is BillUiState.Loading
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Participant Info Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("With ", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                Text(
                    text = if (selectedParticipants.size == members.size) "you and the whole group" else "you and ${selectedParticipants.size - 1} others",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // Split Type Selector (Premium Tab Row)
            if (!isSolo) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("EQUAL", "EXACT", "ITEM_WISE").forEach { type ->
                        val isSelected = splitType == type
                        Surface(
                            modifier = Modifier.weight(1f).height(44.dp).clickable { splitType = type },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = when (type) {
                                        "EQUAL" -> "Equally"
                                        "EXACT" -> "Exact"
                                        "ITEM_WISE" -> "Item-wise"
                                        else -> type
                                    },
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VpnLock, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Personal Expense Mode", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }

            // Main Input Area (Splitwise Style)
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Icon
                    Surface(
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = when (splitType) {
                                    "ITEM_WISE" -> Icons.Default.CloudDownload
                                    "EXACT" -> Icons.Default.Calculate
                                    else -> Icons.Default.Restaurant
                                },
                                contentDescription = null, 
                                modifier = Modifier.size(30.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        TextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("What was this for?", fontSize = 16.sp) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("₹", fontSize = 28.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            TextField(
                                value = amount,
                                onValueChange = { amount = it },
                                placeholder = { Text("0.00", fontSize = 28.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Black),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.height(16.dp))

            if (!isSolo) {
                // Multi-user Selection (Visible when choosing split details)
                Text(
                    "Choosing participants", 
                    modifier = Modifier.padding(horizontal = 16.dp), 
                    fontSize = 12.sp, 
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
                
                if (splitType == "ITEM_WISE") {
                    ItemWiseInput(
                        items = billItems,
                        members = members,
                        onItemsUpdate = { updatedItems ->
                            billItems.clear()
                            billItems.addAll(updatedItems)
                            // Auto-calculate the total amount based on items
                            amount = billItems.sumOf { it.price }.toString()
                        }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(members) { user ->
                            ParticipantRow(
                                user = user,
                                isSelected = selectedParticipants.contains(user.id),
                                splitType = splitType,
                                exactAmount = exactAmounts[user.id] ?: "",
                                onToggle = {
                                    if (selectedParticipants.contains(user.id)) {
                                        if (selectedParticipants.size > 1) selectedParticipants.remove(user.id)
                                    } else {
                                        selectedParticipants.add(user.id)
                                    }
                                },
                                onAmountChange = { exactAmounts[user.id] = it }
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "This expense will be private to you.", 
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
        
        if (uiState is BillUiState.Loading || isFetching) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    if (isFetching) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Fetching your online bills...", color = Color.White)
                    }
                }
            }
        }
    }
}

private fun handleSplitAndSave(
    total: Double,
    title: String,
    splitType: String,
    selectedParticipants: List<String>,
    exactAmounts: Map<String, String>,
    billViewModel: BillViewModel,
    groupId: String,
    context: android.content.Context,
    onBack: () -> Unit,
    billItems: List<com.kapasiya.sharefair.model.BillItem> = emptyList()
) {
    var finalParticipants = selectedParticipants.toList()
    
    val participantsMap = if (groupId == "solo") {
        mapOf("me" to total) // Simplified for solo
    } else {
        when (splitType) {
            "EQUAL" -> {
                if (selectedParticipants.isEmpty()) emptyMap()
                else {
                    val share = total / selectedParticipants.size
                    selectedParticipants.associateWith { share }
                }
            }
            "EXACT" -> {
                selectedParticipants.associateWith { exactAmounts[it]?.toDoubleOrNull() ?: 0.0 }
            }
            "ITEM_WISE" -> {
                val map = mutableMapOf<String, Double>()
                billItems.forEach { item ->
                    if (item.consumedBy.isNotEmpty()) {
                        val share = item.price / item.consumedBy.size
                        item.consumedBy.forEach { userId ->
                            map[userId] = (map[userId] ?: 0.0) + share
                        }
                    }
                }
                finalParticipants = map.keys.toList()
                map
            }
            else -> emptyMap()
        }
    }
    
    if (groupId != "solo" && finalParticipants.isEmpty() && splitType != "ITEM_WISE") {
        Toast.makeText(context, "No participants selected", Toast.LENGTH_SHORT).show()
        return
    }

    // Check sum if Exact or ItemWise
    if (splitType == "EXACT" && groupId != "solo") {
        val sum = participantsMap.values.sum()
        if (kotlin.math.abs(sum - total) > 0.01) {
            Toast.makeText(context, "Amounts must equal total ₹$total", Toast.LENGTH_SHORT).show()
            return
        }
    }

    billViewModel.addBill(groupId, title, total, if (groupId == "solo") "SOLO" else splitType, participantsMap, billItems) {
        Toast.makeText(context, "Bill Added!", Toast.LENGTH_SHORT).show()
        onBack()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemWiseInput(
    items: List<com.kapasiya.sharefair.model.BillItem>,
    members: List<User>,
    onItemsUpdate: (List<com.kapasiya.sharefair.model.BillItem>) -> Unit
) {
    var selectedMemberId by remember { mutableStateOf(members.firstOrNull()?.id ?: "") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Assign Items", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Select a person, then tap items they consumed", fontSize = 12.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Horizontal User Tabs for assignment
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(members) { user ->
                val isSelected = selectedMemberId == user.id
                Surface(
                    onClick = { selectedMemberId = user.id },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.height(40.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            user.name, 
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
            items(items.size) { index ->
                val billItem = items[index]
                val isConsumedBySelected = billItem.consumedBy.contains(selectedMemberId)
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            val newList = items.toMutableList()
                            val newConsumedBy = billItem.consumedBy.toMutableList()
                            if (isConsumedBySelected) {
                                newConsumedBy.remove(selectedMemberId)
                            } else {
                                if (selectedMemberId.isNotEmpty()) newConsumedBy.add(selectedMemberId)
                            }
                            newList[index] = billItem.copy(consumedBy = newConsumedBy)
                            onItemsUpdate(newList)
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isConsumedBySelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = if (isConsumedBySelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(billItem.name, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = if (billItem.consumedBy.isEmpty()) "Unassigned" else "${billItem.consumedBy.size} people sharing",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                        Text("₹${billItem.price}", fontWeight = FontWeight.Bold)
                        
                        if (isConsumedBySelected) {
                            Icon(
                                Icons.Default.CheckCircle, 
                                contentDescription = null, 
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 8.dp).size(20.dp)
                            )
                        }
                    }
                }
            }
        }
        
        FilledTonalButton(
            onClick = {
                val newList = items.toMutableList()
                newList.add(com.kapasiya.sharefair.model.BillItem(name = "New Item", price = 0.0))
                onItemsUpdate(newList)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text("Add Item Manually")
        }
    }
}

@Composable
fun ParticipantRow(
    user: User,
    isSelected: Boolean,
    splitType: String,
    exactAmount: String,
    onToggle: () -> Unit,
    onAmountChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(user.name.take(1).uppercase(), color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            user.name, 
            modifier = Modifier.weight(1f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        if (splitType == "EXACT" && isSelected) {
            TextField(
                value = exactAmount,
                onValueChange = onAmountChange,
                modifier = Modifier.width(100.dp),
                placeholder = { Text("0.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
        } else {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
        }
    }
}
