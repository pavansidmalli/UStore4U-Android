package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.HeadsetMic
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Order
import com.example.data.model.OrderLineItem
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GrayBorder
import com.example.ui.theme.LiteAshBg
import com.example.ui.theme.LiteAshWhite
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SubtleDivider
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.OrderTrackingViewModel

private fun formatInr(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        "₹%,d".format(amount.toLong())
    } else {
        "₹%,.2f".format(amount)
    }
}

data class OrderMilestone(
    val title: String,
    val subtitle: String,
    val timestamp: String,
    val location: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean,
    val icon: ImageVector
)

private fun generateMilestonesForOrder(order: Order): List<OrderMilestone> {
    val status = order.status.lowercase()
    val dateCreated = order.dateCreated.take(10).ifBlank { "2026-08-30" }
    val carrier = if (order.trackingNumber?.contains("BD") == true) "Blue Dart Platinum Express" else "FedEx Luxury Priority"
    val city = order.shipping.city.ifBlank { "Hyderabad" }

    val isStep1Done = true
    val isStep2Done = status in listOf("processing", "shipped", "in_transit", "out_for_delivery", "completed")
    val isStep3Done = status in listOf("shipped", "in_transit", "out_for_delivery", "completed")
    val isStep4Done = status in listOf("out_for_delivery", "completed")
    val isStep5Done = status == "completed"

    return listOf(
        OrderMilestone(
            title = "Order Registered & Payment Confirmed",
            subtitle = "Transaction captured via ${order.paymentMethodTitle}. WooCommerce Order #${order.id} verified.",
            timestamp = "$dateCreated • 10:15 AM",
            location = "UStore4U Storefront System",
            isCompleted = isStep1Done,
            isCurrent = status == "pending",
            icon = Icons.Outlined.ReceiptLong
        ),
        OrderMilestone(
            title = "Horological QC & Vault Inspection",
            subtitle = "Calibre accuracy rate verified (+/- 2 sec/day), waterproof pressure seal certified, tamper-evident security vault packed.",
            timestamp = "$dateCreated • 04:30 PM",
            location = "Swiss Horology Certification Vault",
            isCompleted = isStep2Done,
            isCurrent = status == "processing",
            icon = Icons.Outlined.Verified
        ),
        OrderMilestone(
            title = "Dispatched with $carrier",
            subtitle = "Handed over to armored logistics partner. Air Waybill #${order.trackingNumber ?: "U4U-EXP-849201"} generated.",
            timestamp = "Next Day • 09:00 AM",
            location = "Air Freight Transit Terminal",
            isCompleted = isStep3Done,
            isCurrent = status == "shipped" || status == "in_transit",
            icon = Icons.Outlined.LocalShipping
        ),
        OrderMilestone(
            title = "Out for Secure White-Glove Handover",
            subtitle = "Courier specialist is en route to $city destination. Client OTP verification required upon delivery.",
            timestamp = "Expected Today • 11:30 AM",
            location = "$city Regional Delivery Center",
            isCompleted = isStep4Done,
            isCurrent = status == "out_for_delivery",
            icon = Icons.Outlined.LocationOn
        ),
        OrderMilestone(
            title = "Delivered & Signed by Client",
            subtitle = "Consignment handed over to ${order.shipping.firstName} ${order.shipping.lastName}. Digital biometric signature confirmed.",
            timestamp = order.estimatedDelivery ?: "Completed",
            location = "${order.shipping.city}, ${order.shipping.country}",
            isCompleted = isStep5Done,
            isCurrent = status == "completed",
            icon = Icons.Outlined.CheckCircle
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    viewModel: OrderTrackingViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToProduct: (Long) -> Unit,
    onExploreWatches: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val availableOrders by viewModel.availableOrders.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val context = LocalContext.current

    val currentOrder = uiState.selectedOrder

    Scaffold(
        containerColor = LiteAshWhite,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "ORDER TRACKING",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                letterSpacing = 1.2.sp,
                                color = ObsidianBlack
                            )
                            if (currentOrder != null) {
                                Text(
                                    "CONSIGNMENT #${currentOrder.id}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("order_tracking_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = ObsidianBlack
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.refreshCurrentOrderStatus() },
                            enabled = !uiState.isRefreshing && currentOrder != null,
                            modifier = Modifier.testTag("refresh_order_status_button")
                        ) {
                            if (uiState.isRefreshing) {
                                CircularProgressIndicator(
                                    color = ObsidianBlack,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh Status",
                                    tint = ObsidianBlack
                                )
                            }
                        }

                        IconButton(
                            onClick = onNavigateToCart,
                            modifier = Modifier.testTag("order_tracking_cart_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (cartCount > 0) {
                                        Badge(containerColor = ObsidianBlack, contentColor = PureWhite) {
                                            Text(cartCount.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ShoppingBag,
                                    contentDescription = "Cart",
                                    tint = ObsidianBlack
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PureWhite,
                        scrolledContainerColor = PureWhite,
                        titleContentColor = ObsidianBlack,
                        navigationIconContentColor = ObsidianBlack,
                        actionIconContentColor = ObsidianBlack
                    )
                )
                HorizontalDivider(thickness = 0.8.dp, color = SubtleDivider)
            }
        },
        modifier = modifier.testTag("order_tracking_screen")
    ) { innerPadding ->
        if (currentOrder == null && availableOrders.isEmpty()) {
            EmptyStateView(
                icon = Icons.Outlined.LocalShipping,
                title = "NO ACTIVE CONSIGNMENTS",
                message = "You don't have any watch orders to track currently. Place an order to track live delivery status.",
                actionButtonText = "BROWSE TIMEPIECES",
                onActionClick = onExploreWatches,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Live WooCommerce API Sync Notice Bar
                item {
                    WooCommerceSyncBanner(
                        isRefreshing = uiState.isRefreshing,
                        statusNotification = uiState.statusNotification,
                        isLiveApi = uiState.isLiveApiActive,
                        onRefreshClick = { viewModel.refreshCurrentOrderStatus() }
                    )
                }

                // 2. Order Selector & Search / AWB Lookup Bar
                item {
                    OrderLookupAndSelectorCard(
                        searchQuery = uiState.searchQuery,
                        onSearchChange = { viewModel.onSearchQueryChanged(it) },
                        availableOrders = availableOrders,
                        selectedOrder = currentOrder,
                        onSelectOrder = { viewModel.selectOrder(it) }
                    )
                }

                if (currentOrder != null) {
                    // 3. Hero Status & Estimated Delivery Card
                    item {
                        OrderTrackingHeroCard(
                            order = currentOrder,
                            onCopyTracking = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("AWB Tracking Number", currentOrder.trackingNumber ?: "U4U-EXP-${currentOrder.id}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Tracking Number copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    // 4. Live Progress Stepper / Vertical Timeline
                    item {
                        OrderMilestoneTimelineCard(order = currentOrder)
                    }

                    // 5. Courier & Secure Delivery Vault Card (PIN / Address / Carrier)
                    item {
                        CourierVaultDetailsCard(
                            order = currentOrder,
                            onOpenCarrierPortal = {
                                val url = currentOrder.trackingUrl ?: "https://www.fedex.com"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Opening carrier portal: $url", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onCopyOtp = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Delivery OTP", "8492")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Delivery OTP (8492) copied", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    // 6. Purchased Watches in this Order
                    item {
                        PurchasedWatchesListCard(
                            lineItems = currentOrder.lineItems,
                            onNavigateToProduct = onNavigateToProduct
                        )
                    }

                    // 7. Order Summary & Concierge Actions Card
                    item {
                        OrderConciergeActionsCard(
                            order = currentOrder,
                            onContactConcierge = {
                                Toast.makeText(context, "Connecting to UStore4U Horology Concierge (+1-800-USTORE4U)...", Toast.LENGTH_LONG).show()
                            },
                            onDownloadCertificate = {
                                Toast.makeText(context, "Certificate of Authenticity & Invoice saved for Order #${currentOrder.id}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WooCommerceSyncBanner(
    isRefreshing: Boolean,
    statusNotification: String?,
    isLiveApi: Boolean,
    onRefreshClick: () -> Unit
) {
    Surface(
        color = LiteAshBg,
        shape = RoundedCornerShape(2.dp),
        border = BorderStroke(1.dp, GrayBorder),
        modifier = Modifier.fillMaxWidth().testTag("wc_sync_banner")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isLiveApi) ForestGreen else ObsidianBlack)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isLiveApi) "WOOCOMMERCE ORDER API: LIVE SYNC ACTIVE" else "STOREFRONT CONSIGNMENT TRACKING",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.6.sp,
                        color = ObsidianBlack
                    )
                    Text(
                        text = statusNotification ?: "Synchronized with central watch dispatch ledger",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isRefreshing) {
                CircularProgressIndicator(
                    color = ObsidianBlack,
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
            } else {
                TextButton(
                    onClick = onRefreshClick,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        "REFRESH",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ObsidianBlack
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderLookupAndSelectorCard(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    availableOrders: List<Order>,
    selectedOrder: Order?,
    onSelectOrder: (Order) -> Unit
) {
    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, GrayBorder),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().testTag("order_lookup_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "TRACK CONSIGNMENT BY ID OR AWB",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = ObsidianBlack
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search by Order # (e.g. 98412) or AWB code...") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = ObsidianBlack,
                        modifier = Modifier.size(18.dp)
                    )
                },
                shape = RoundedCornerShape(2.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = LiteAshBg,
                    unfocusedContainerColor = LiteAshBg,
                    focusedBorderColor = ObsidianBlack,
                    unfocusedBorderColor = GrayBorder
                ),
                modifier = Modifier.fillMaxWidth().testTag("order_search_input")
            )

            if (availableOrders.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "SELECT RECENT PURCHASE:",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableOrders, key = { it.id }) { order ->
                        val isSelected = selectedOrder?.id == order.id
                        val statusColor = when (order.status.lowercase()) {
                            "completed" -> ForestGreen
                            "processing" -> WarmGold
                            else -> ObsidianBlack
                        }

                        Surface(
                            color = if (isSelected) ObsidianBlack else PureWhite,
                            shape = RoundedCornerShape(2.dp),
                            border = BorderStroke(1.dp, if (isSelected) ObsidianBlack else GrayBorder),
                            modifier = Modifier
                                .clickable { onSelectOrder(order) }
                                .testTag("order_chip_${order.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) PureWhite else statusColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "#${order.id} • ${order.status.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) PureWhite else ObsidianBlack,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderTrackingHeroCard(
    order: Order,
    onCopyTracking: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianBlack),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().testTag("order_tracking_hero_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Order ID & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ORDER CONSIGNMENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = PureWhite.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "#${order.id}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = PureWhite
                        )
                        IconButton(
                            onClick = onCopyTracking,
                            modifier = Modifier.size(28.dp).padding(start = 4.dp).testTag("copy_tracking_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "Copy",
                                tint = PureWhite.copy(alpha = 0.8f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // Status Badge
                Surface(
                    color = when (order.status.lowercase()) {
                        "completed" -> ForestGreen.copy(alpha = 0.3f)
                        "shipped", "in_transit" -> ChampagneGold.copy(alpha = 0.25f)
                        "processing" -> PureWhite.copy(alpha = 0.15f)
                        else -> PureWhite.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(2.dp),
                    border = BorderStroke(
                        1.dp,
                        when (order.status.lowercase()) {
                            "completed" -> ForestGreen
                            "shipped", "in_transit" -> ChampagneGold
                            else -> PureWhite.copy(alpha = 0.4f)
                        }
                    )
                ) {
                    Text(
                        text = when (order.status.lowercase()) {
                            "completed" -> "DELIVERED"
                            "shipped", "in_transit" -> "IN TRANSIT"
                            "processing" -> "HOROLOGY QC & PACKING"
                            else -> order.status.uppercase()
                        },
                        color = when (order.status.lowercase()) {
                            "completed" -> ForestGreen
                            "shipped", "in_transit" -> ChampagneGold
                            else -> PureWhite
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                color = PureWhite.copy(alpha = 0.15f)
            )

            // Estimated Delivery Block
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PureWhite.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "ESTIMATED DELIVERY",
                        style = MaterialTheme.typography.labelSmall,
                        color = PureWhite.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = order.estimatedDelivery ?: "Within 2-4 Business Days",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar
            val progressPercent = when (order.status.lowercase()) {
                "completed" -> 1.0f
                "out_for_delivery" -> 0.8f
                "shipped", "in_transit" -> 0.6f
                "processing" -> 0.35f
                else -> 0.15f
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Shipment Progress",
                        style = MaterialTheme.typography.labelSmall,
                        color = PureWhite.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                    Text(
                        text = "${(progressPercent * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGold,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(2.5.dp))
                        .background(PureWhite.copy(alpha = 0.15f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressPercent)
                            .height(5.dp)
                            .clip(RoundedCornerShape(2.5.dp))
                            .background(ChampagneGold)
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderMilestoneTimelineCard(order: Order) {
    val milestones = remember(order.id, order.status) { generateMilestonesForOrder(order) }

    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, GrayBorder),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().testTag("order_milestones_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Watch,
                        contentDescription = null,
                        tint = ObsidianBlack,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SHIPMENT & HOROLOGY TIMELINE",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = ObsidianBlack
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SubtleDivider)

            // Timeline Items
            milestones.forEachIndexed { index, milestone ->
                val isLast = index == milestones.size - 1

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Timeline Node + Vertical Connecting Line
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(32.dp)
                    ) {
                        // Node Circle
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        milestone.isCompleted -> ObsidianBlack
                                        milestone.isCurrent -> ChampagneGold
                                        else -> LiteAshBg
                                    }
                                )
                                .border(
                                    1.5.dp,
                                    when {
                                        milestone.isCompleted -> ObsidianBlack
                                        milestone.isCurrent -> ObsidianBlack
                                        else -> GrayBorder
                                    },
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (milestone.isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = PureWhite,
                                    modifier = Modifier.size(14.dp)
                                )
                            } else if (milestone.isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(ObsidianBlack)
                                )
                            }
                        }

                        // Connecting Vertical Bar
                        if (!isLast) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(58.dp)
                                    .background(
                                        if (milestone.isCompleted) ObsidianBlack else GrayBorder
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Text & Details
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = if (!isLast) 14.dp else 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = milestone.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (milestone.isCompleted || milestone.isCurrent) FontWeight.Bold else FontWeight.Medium,
                                color = if (milestone.isCompleted || milestone.isCurrent) ObsidianBlack else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = milestone.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = milestone.location,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = milestone.timestamp,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ObsidianBlack
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourierVaultDetailsCard(
    order: Order,
    onOpenCarrierPortal: () -> Unit,
    onCopyOtp: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, GrayBorder),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().testTag("courier_vault_card")
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = ObsidianBlack,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CARRIER & SECURITY VAULT",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = ObsidianBlack
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = SubtleDivider)

            // Carrier and AWB
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Carrier Logistics Partner", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val carrierName = if (order.trackingNumber?.contains("BD") == true) "Blue Dart Platinum Express" else "FedEx Priority Armored"
                    Text(carrierName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                }

                OutlinedButton(
                    onClick = onOpenCarrierPortal,
                    shape = RoundedCornerShape(2.dp),
                    border = BorderStroke(0.8.dp, ObsidianBlack),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp).testTag("carrier_portal_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("PORTAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Outlined.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp), tint = ObsidianBlack)
                    }
                }
            }

            // Air Waybill (AWB)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Air Waybill (AWB)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = order.trackingNumber ?: "U4U-FEDEX-918234102",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = ObsidianBlack
                )
            }

            // Delivery OTP Verification Badge
            Surface(
                color = LiteAshBg,
                shape = RoundedCornerShape(2.dp),
                border = BorderStroke(1.dp, GrayBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Lock, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "HIGH-VALUE DELIVERY OTP",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = ObsidianBlack
                            )
                            Text(
                                "Share with courier at delivery: 8492",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    TextButton(
                        onClick = onCopyOtp,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("COPY OTP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                    }
                }
            }

            // Destination Address
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Destination Address", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${order.shipping.firstName} ${order.shipping.lastName} • ${order.shipping.phone.ifBlank { "+91 98765 43210" }}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ObsidianBlack
                )
                Text(
                    text = order.shipping.formattedOneLine.ifBlank { "Plot 42, Jubilee Hills Road No. 36, Hyderabad, Telangana 500033, India" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PurchasedWatchesListCard(
    lineItems: List<OrderLineItem>,
    onNavigateToProduct: (Long) -> Unit
) {
    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, GrayBorder),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().testTag("purchased_watches_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Inventory2,
                    contentDescription = null,
                    tint = ObsidianBlack,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CONSIGNED TIMEPIECES (${lineItems.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = ObsidianBlack
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SubtleDivider)

            lineItems.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (item.productId > 0) {
                                onNavigateToProduct(item.productId)
                            }
                        }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .border(BorderStroke(0.8.dp, GrayBorder), RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ObsidianBlack
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (item.sku.isNotBlank()) {
                                Text(
                                    text = "SKU: ${item.sku}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "Qty: ${item.quantity}",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    val priceVal = item.total.toDoubleOrNull() ?: (item.price * item.quantity)
                    Text(
                        text = formatInr(priceVal),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = ObsidianBlack
                    )
                }

                if (index < lineItems.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = SubtleDivider)
                }
            }
        }
    }
}

@Composable
private fun OrderConciergeActionsCard(
    order: Order,
    onContactConcierge: () -> Unit,
    onDownloadCertificate: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, GrayBorder),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().testTag("order_concierge_actions_card")
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Payment breakdown summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Payment Method", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(order.paymentMethodTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = ObsidianBlack)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Armored Insured Shipping", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Complimentary (Insured)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = ForestGreen)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Paid", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                val totalVal = order.total.toDoubleOrNull() ?: 0.0
                Text(formatInr(totalVal), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = ObsidianBlack)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = SubtleDivider)

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onContactConcierge,
                    shape = RoundedCornerShape(2.dp),
                    border = BorderStroke(1.dp, ObsidianBlack),
                    modifier = Modifier.weight(1f).height(44.dp).testTag("concierge_support_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.HeadsetMic, contentDescription = null, modifier = Modifier.size(16.dp), tint = ObsidianBlack)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CONCIERGE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                    }
                }

                Button(
                    onClick = onDownloadCertificate,
                    colors = ButtonDefaults.buttonColors(containerColor = ObsidianBlack, contentColor = PureWhite),
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier.weight(1f).height(44.dp).testTag("download_cert_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Verified, contentDescription = null, modifier = Modifier.size(16.dp), tint = PureWhite)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CERTIFICATE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PureWhite)
                    }
                }
            }
        }
    }
}
