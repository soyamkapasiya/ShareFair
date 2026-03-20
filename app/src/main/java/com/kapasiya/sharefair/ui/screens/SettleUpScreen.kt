package com.kapasiya.sharefair.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kapasiya.sharefair.ui.viewmodel.BillViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettleUpScreen(
    groupId: String,
    friendId: String,
    amount: Double,
    friendName: String,
    onBack: () -> Unit,
    viewModel: BillViewModel = viewModel()
) {
    var isSettled by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isSettled) {
                Text(
                    "Settlement Dashboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Clutter-free breakdown of your debt",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CurrencyRupee, 
                                contentDescription = null, 
                                tint = MaterialTheme.colorScheme.onPrimary, 
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text("Amount Owed", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "₹$amount", 
                            fontSize = 42.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "to $friendName", 
                            color = MaterialTheme.colorScheme.primary, 
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(64.dp))
                
                Button(
                    onClick = {
                        // UPI Integration Mock
                        val upiUri = Uri.parse("upi://pay?pa=friend@upi&pn=$friendName&am=$amount&cu=INR")
                        val intent = Intent(Intent.ACTION_VIEW, upiUri)
                        try {
                            context.startActivity(intent)
                            viewModel.settleUp(groupId, friendId, amount) {
                                isSettled = true
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "No UPI app found. Marking as paid manually.", Toast.LENGTH_SHORT).show()
                            viewModel.settleUp(groupId, friendId, amount) {
                                isSettled = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Settle via UPI",
                        color = MaterialTheme.colorScheme.onPrimary, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 18.sp
                    )
                }
                
                TextButton(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                }
            } else {
                CelebrationView(onBack)
            }
        }
    }
}

@Composable
fun CelebrationView(onDone: () -> Unit) {
    var startAnim by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnim) 1.2f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = ""
    )
    
    LaunchedEffect(Unit) {
        startAnim = true
        delay(3000)
        onDone()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.onPrimary, 
                modifier = Modifier.size(80.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            "Payment Settled!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.scale(scale)
        )
        Text(
            "Way to go! Your debt is cleared.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 8.dp).scale(scale)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Icon(
            Icons.Default.Celebration, 
            contentDescription = null, 
            tint = MaterialTheme.colorScheme.secondary, 
            modifier = Modifier.size(60.dp).scale(scale)
        )
    }
}
