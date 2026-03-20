package com.kapasiya.sharefair.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userName: String,
    userEmail: String,
    profileImageUrl: String = "",
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onNavigateAnalytics: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle Edit */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(
                        width = 4.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        ),
                        shape = CircleShape
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    if (profileImageUrl.isNotEmpty()) {
                        coil.compose.AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = { /* Handle Edit */ },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.width(140.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Settings Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    SettingItem("My Account", Icons.Default.AccountCircle)
                    SettingItem("Spending Analytics", Icons.Default.TrendingUp) {
                        onNavigateAnalytics()
                    }
                    SettingItem("Payment Methods", Icons.Default.Payments)
                    
                    var fingerprintEnabled by remember { mutableStateOf(true) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Icon(
                                Icons.Default.Fingerprint,
                                contentDescription = null,
                                modifier = Modifier.padding(12.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Biometric Security", fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                        Switch(
                            checked = fingerprintEnabled,
                            onCheckedChange = { fingerprintEnabled = it }
                        )
                    }
                    
                    SettingItem("Privacy & Security", Icons.Default.Lock)
                    SettingItem("App Settings", Icons.Default.Settings)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Linked Accounts Section
            Text(
                "CONNECTED SERVICES (AUTO-FETCH)", 
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                fontSize = 11.sp, 
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                val services = listOf("Zomato" to Color(0xFFCB202D), "Swiggy" to Color(0xFFFC8019), "Zepto" to Color(0xFF5E2B97))
                services.forEach { (name, color) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(18.dp),
                            color = color.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(2.dp, color.copy(alpha = 0.4f))
                        ) {
                            Icon(
                                imageVector = if (name == "Zepto") Icons.Default.Bolt else Icons.Default.Restaurant, 
                                contentDescription = null, 
                                tint = color, 
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(name, fontSize = 11.sp, color = color, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out Safely", fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun SettingItem(title: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.padding(10.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}
