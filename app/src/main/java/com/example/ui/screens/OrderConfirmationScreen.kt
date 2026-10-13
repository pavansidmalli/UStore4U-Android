package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GrayBorder
import com.example.ui.theme.LiteAshBg
import com.example.ui.theme.LiteAshWhite
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SubtleDivider
import com.example.ui.theme.WestsideTerracotta

private fun formatInr(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        "₹%,d".format(amount.toLong())
    } else {
        "₹%,.2f".format(amount)
    }
}

@Composable
fun OrderConfirmationScreen(
    order: Order?,
    onNavigateHome: () -> Unit,
    onNavigateOrders: () -> Unit,
    onNavigateToTracking: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = LiteAshWhite,
        bottomBar = {
            Surface(
                color = PureWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (order != null) {
                                onNavigateToTracking(order.id)
                            } else {
                                onNavigateOrders()
                            }
                        },
                        shape = RoundedCornerShape(2.dp),
                        border = BorderStroke(1.dp, ObsidianBlack),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("confirmation_track_order_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp), tint = ObsidianBlack)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "TRACK SHIPMENT",
                                fontWeight = FontWeight.Bold,
                                color = ObsidianBlack,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    Button(
                        onClick = onNavigateHome,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ObsidianBlack,
                            contentColor = PureWhite
                        ),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("confirmation_continue_shopping_button")
                    ) {
                        Text(
                            "SHOPPING",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        },
        modifier = modifier.testTag("order_confirmation_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Success Monogram Animation Card
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(ObsidianBlack),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = PureWhite,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "THANK YOU FOR YOUR ORDER",
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center,
                        color = ObsidianBlack
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Order #${order?.id ?: "94821"} has been received and is being prepared with precision.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Tracking and Delivery Info Card
            item {
                Card(
                    shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, GrayBorder),
                    elevation = CardDefaults.cardElevation(0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocalShipping, contentDescription = null, tint = ObsidianBlack)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "DELIVERY & TRACKING DETAILS", 
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                letterSpacing = 0.8.sp,
                                color = ObsidianBlack
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = SubtleDivider)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tracking Number", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(order?.trackingNumber ?: "U4U-EXP-849201", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Estimated Delivery", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(order?.estimatedDelivery ?: "2-4 Business Days", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = ObsidianBlack)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Shipping Address", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(order?.shipping?.formattedOneLine ?: "Hyderabad, Telangana 500072", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, textAlign = TextAlign.End, modifier = Modifier.width(180.dp), color = ObsidianBlack)
                        }
                    }
                }
            }

            // Order Summary Details
            if (order != null && order.lineItems.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(2.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = BorderStroke(1.dp, GrayBorder),
                        elevation = CardDefaults.cardElevation(0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "PURCHASED TIMEPIECES", 
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                letterSpacing = 0.8.sp,
                                color = ObsidianBlack
                            )

                            order.lineItems.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${item.quantity}x ${item.name}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), color = ObsidianBlack)
                                    val itemTotal = item.total.toDoubleOrNull()?.let { formatInr(it) } ?: "₹${item.total}"
                                    Text(itemTotal, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = SubtleDivider)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Paid", fontWeight = FontWeight.Bold, color = ObsidianBlack)
                                val orderTotal = order.total.toDoubleOrNull()?.let { formatInr(it) } ?: "₹${order.total}"
                                Text(orderTotal, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                            }
                        }
                    }
                }
            }
        }
    }
}
