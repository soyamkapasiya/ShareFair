package com.kapasiya.sharefair.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
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

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Elements...
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .offset(x = (-50).dp, y = 50.dp)
                    .blur(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            painterResource(android.R.drawable.ic_menu_revert), 
                            contentDescription = "back", 
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        "Add Bill", 
                        fontSize = 24.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Amount Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            placeholder = { Text("0.00", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), fontSize = 48.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )
                        Text(
                            "TOTAL AMOUNT", 
                            color = MaterialTheme.colorScheme.onSurfaceVariant, 
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("What for?", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Split Selector
                TabRow(
                    selectedTabIndex = if (splitType == "EQUAL") 0 else 1,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[if (splitType == "EQUAL") 0 else 1]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    divider = {},
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                ) {
                    Tab(
                        selected = splitType == "EQUAL",
                        onClick = { splitType = "EQUAL" },
                        text = { Text("Equal") }
                    )
                    Tab(
                        selected = splitType == "EXACT",
                        onClick = { splitType = "EXACT" },
                        text = { Text("Exact") }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Split with", 
                    color = MaterialTheme.colorScheme.onBackground, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 18.sp
                )
                
                // Participants Multi-select List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (members.isEmpty()) {
                        item {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)))
                        }
                    } else {
                        items(members) { user ->
                            ParticipantSelectionItem(
                                user = user,
                                isSelected = selectedParticipants.contains(user.id),
                                splitType = splitType,
                                exactAmount = exactAmounts[user.id] ?: "",
                                onToggle = {
                                    if (selectedParticipants.contains(user.id)) {
                                        selectedParticipants.remove(user.id)
                                    } else {
                                        selectedParticipants.add(user.id)
                                    }
                                },
                                onAmountChange = { exactAmounts[user.id] = it }
                            )
                        }
                    }
                }

                // Split Button
                Button(
                    onClick = {
                        val total = amount.toDoubleOrNull() ?: 0.0
                        if (total > 0 && title.isNotEmpty() && selectedParticipants.isNotEmpty()) {
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
                                    return@Button
                                }
                            }

                            billViewModel.addBill(groupId, title, total, splitType, participantsMap) {
                                Toast.makeText(context, "Bill Added!", Toast.LENGTH_SHORT).show()
                                onBack()
                            }
                        } else {
                            Toast.makeText(context, "Check inputs... total, title & users", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    enabled = uiState !is BillUiState.Loading
                ) {
                    if (uiState is BillUiState.Loading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save Bill", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ParticipantSelectionItem(
    user: User,
    isSelected: Boolean,
    splitType: String,
    exactAmount: String,
    onToggle: () -> Unit,
    onAmountChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) 
                           else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) else null
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    user.name.take(1).uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    user.name,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    user.email,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (splitType == "EXACT" && isSelected) {
                OutlinedTextField(
                    value = exactAmount,
                    onValueChange = onAmountChange,
                    modifier = Modifier.width(100.dp),
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
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
}
