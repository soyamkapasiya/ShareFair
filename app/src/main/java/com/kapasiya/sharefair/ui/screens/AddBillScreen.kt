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
    
    val group by groupDetailsViewModel.groupFlow.collectAsState()
    val members by groupDetailsViewModel.members.collectAsState()
    val uiState by billViewModel.uiState.collectAsState()
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
                    TextButton(
                        onClick = {
                            val total = amount.toDoubleOrNull() ?: 0.0
                            if (total > 0 && title.isNotEmpty() && selectedParticipants.isNotEmpty()) {
                                handleSplitAndSave(
                                    total, title, splitType, selectedParticipants, 
                                    exactAmounts, billViewModel, groupId, context, onBack
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

            // Main Input Area (Splitwise Style)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Icon
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Restaurant, 
                            contentDescription = null, 
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Enter a description", fontSize = 18.sp) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("₹", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                        }
                        TextField(
                            value = amount,
                            onValueChange = { amount = it },
                            placeholder = { Text("0.00", fontSize = 24.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Paid By / Split Info Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Paid by ", color = MaterialTheme.colorScheme.onBackground)
                Surface(
                    modifier = Modifier.clickable { /* Choose Payer */ },
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text("you", modifier = Modifier.padding(horizontal = 4.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Text(" and split ", color = MaterialTheme.colorScheme.onBackground)
                Surface(
                    modifier = Modifier.clickable { splitType = if (splitType == "EQUAL") "EXACT" else "EQUAL" },
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = if (splitType == "EQUAL") "equally" else "exactly", 
                        modifier = Modifier.padding(horizontal = 4.dp), 
                        color = MaterialTheme.colorScheme.primary, 
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(".", color = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Multi-user Selection (Visible when choosing split details)
            Text(
                "Choosing participants", 
                modifier = Modifier.padding(horizontal = 16.dp), 
                fontSize = 12.sp, 
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            
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
        
        if (uiState is BillUiState.Loading) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
    onBack: () -> Unit
) {
    val participantsMap = if (splitType == "EQUAL") {
        val share = total / selectedParticipants.size
        selectedParticipants.associateWith { share }
    } else {
        selectedParticipants.associateWith { exactAmounts[it]?.toDoubleOrNull() ?: 0.0 }
    }
    
    // Check sum if Exact
    if (splitType == "EXACT") {
        val sum = participantsMap.values.sum()
        if (kotlin.math.abs(sum - total) > 0.01) {
            Toast.makeText(context, "Amounts must equal total ₹$total", Toast.LENGTH_SHORT).show()
            return
        }
    }

    billViewModel.addBill(groupId, title, total, splitType, participantsMap) {
        Toast.makeText(context, "Bill Added!", Toast.LENGTH_SHORT).show()
        onBack()
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
