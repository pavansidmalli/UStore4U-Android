package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.Order
import com.example.data.model.OrderLineItem
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GrayBorder
import com.example.ui.theme.LiteAshBg
import com.example.ui.theme.LiteAshWhite
import com.example.ui.theme.LuxuryRed
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SubtleDivider
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.OrdersViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatInr(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        "₹%,d".format(amount.toLong())
    } else {
        "₹%,.2f".format(amount)
    }
}

private fun formatOrderDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val date = inputFormat.parse(dateString) ?: return dateString.take(10)
        val outputFormat = SimpleDateFormat("MMMM dd, yyyy • hh:mm a", Locale.US)
        outputFormat.format(date)
    } catch (e: Exception) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = inputFormat.parse(dateString.take(10)) ?: return dateString.take(10)
            val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
            outputFormat.format(date)
        } catch (e2: Exception) {
            dateString.take(10)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToTracking: (Long) -> Unit,
    onNavigateToProduct: (Long) -> Unit = {},
    onExploreWatches: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()
    val filteredOrders by viewModel.filteredOrders.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStatusFilter by viewModel.selectedStatusFilter.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val selectedOrderForInvoice by viewModel.selectedOrderForInvoice.collectAsState()
    val reorderSuccessMessage by viewModel.reorderSuccessMessage.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var isSearchExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(reorderSuccessMessage) {
        reorderSuccessMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearReorderMessage()
        }
    }

    // Counts for filter chips
    val allCount = orders.size
    val processingCount = orders.count { it.status.equals("processing", ignoreCase = true) || it.status.equals("on-hold", ignoreCase = true) }
    val shippedCount = orders.count { it.status.equals("shipped", ignoreCase = true) || it.status.equals("in_transit", ignoreCase = true) }
    val completedCount = orders.count { it.status.equals("completed", ignoreCase = true) || it.status.equals("delivered", ignoreCase = true) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = LiteAshWhite,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "MY ORDERS",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                letterSpacing = 1.4.sp,
                                color = ObsidianBlack
                            )
                            Text(
                                "${orders.size} Timepiece Transactions",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("orders_back_button")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = ObsidianBlack
                            )
                        }
                    },
                    actions = {
                        // Search Toggle
                        IconButton(
                            onClick = { isSearchExpanded = !isSearchExpanded },
                            modifier = Modifier.testTag("orders_search_toggle_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search Orders",
                                tint = if (isSearchExpanded || searchQuery.isNotBlank()) ChampagneGold else ObsidianBlack
                            )
                        }

                        // Refresh / Sync
                        IconButton(
                            onClick = { viewModel.refreshOrders() },
                            modifier = Modifier.testTag("orders_refresh_button")
                        ) {
                            if (isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = ObsidianBlack
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Refresh Orders",
                                    tint = ObsidianBlack
                                )
                            }
                        }

                        // Live Tracking Shortcut
                        IconButton(
                            onClick = { onNavigateToTracking(0L) },
                            modifier = Modifier.testTag("orders_track_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocalShipping,
                                contentDescription = "Track Shipment",
                                tint = ObsidianBlack
                            )
                        }

                        // Cart Button
                        IconButton(onClick = onNavigateToCart, modifier = Modifier.testTag("orders_cart_button")) {
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

                // Search Bar Input Field (Collapsible)
                AnimatedVisibility(
                    visible = isSearchExpanded || searchQuery.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        color = PureWhite,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = {
                                    Text("Search by Order #, Watch model, SKU, AWB...", fontSize = 12.sp)
                                },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Search,
                                        contentDescription = null,
                                        tint = ObsidianBlack,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotBlank()) {
                                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                            Icon(
                                                imageVector = Icons.Filled.Clear,
                                                contentDescription = "Clear search",
                                                tint = ObsidianBlack,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(2.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = LiteAshBg,
                                    unfocusedContainerColor = LiteAshBg,
                                    focusedBorderColor = ObsidianBlack,
                                    unfocusedBorderColor = GrayBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("orders_search_input")
                            )

                            if (searchQuery.isNotBlank()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(onClick = {
                                    viewModel.setSearchQuery("")
                                    isSearchExpanded = false
                                }) {
                                    Text("Cancel", color = ObsidianBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Filter Tabs Carousel
                Surface(
                    color = PureWhite,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OrderStatusFilterChip(
                            label = "ALL ($allCount)",
                            isSelected = selectedStatusFilter == "ALL",
                            onClick = { viewModel.setStatusFilter("ALL") }
                        )

                        OrderStatusFilterChip(
                            label = "PROCESSING ($processingCount)",
                            isSelected = selectedStatusFilter == "PROCESSING",
                            onClick = { viewModel.setStatusFilter("PROCESSING") }
                        )

                        OrderStatusFilterChip(
                            label = "IN TRANSIT ($shippedCount)",
                            isSelected = selectedStatusFilter == "SHIPPED",
                            onClick = { viewModel.setStatusFilter("SHIPPED") }
                        )

                        OrderStatusFilterChip(
                            label = "DELIVERED ($completedCount)",
                            isSelected = selectedStatusFilter == "COMPLETED",
                            onClick = { viewModel.setStatusFilter("COMPLETED") }
                        )
                    }
                }

                HorizontalDivider(thickness = 0.8.dp, color = SubtleDivider)
            }
        },
        modifier = modifier.testTag("orders_screen")
    ) { innerPadding ->
        if (orders.isEmpty()) {
            EmptyStateView(
                icon = Icons.Outlined.ReceiptLong,
                title = "NO ORDERS PLACED YET",
                message = "You haven't placed any watch orders with UStore4U yet. When you purchase timepieces, they will appear here with complete transaction history and live courier tracking.",
                actionButtonText = "START SHOPPING",
                onActionClick = onExploreWatches,
                modifier = Modifier.padding(innerPadding)
            )
        } else if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = ObsidianBlack.copy(alpha = 0.4f),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "NO MATCHING ORDERS FOUND",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = ObsidianBlack,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "No orders match your filter criteria \"$searchQuery\".",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            viewModel.setSearchQuery("")
                            viewModel.setStatusFilter("ALL")
                        },
                        shape = RoundedCornerShape(2.dp),
                        border = BorderStroke(1.dp, ObsidianBlack)
                    ) {
                        Text("RESET ALL FILTERS", fontWeight = FontWeight.Bold, color = ObsidianBlack, fontSize = 11.sp)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tracking Quick Action Banner
                item {
                    Surface(
                        color = ObsidianBlack,
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToTracking(0L) }
                            .testTag("track_by_awb_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(PureWhite.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocalShipping,
                                        contentDescription = null,
                                        tint = ChampagneGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "LIVE WATCH CONSIGNMENT TRACKING",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall,
                                        letterSpacing = 0.8.sp,
                                        color = PureWhite
                                    )
                                    Text(
                                        "Real-time FedEx / BlueDart dispatch & armed vault milestones",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PureWhite.copy(alpha = 0.7f),
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = ChampagneGold,
                                shape = RoundedCornerShape(2.dp)
                            ) {
                                Text(
                                    "TRACK",
                                    fontWeight = FontWeight.Bold,
                                    color = ObsidianBlack,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.8.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Order Cards List
                items(
                    items = filteredOrders,
                    key = { it.id },
                    contentType = { "order_card" }
                ) { order ->
                    OrderHistoryCard(
                        order = order,
                        onTrackOrder = { onNavigateToTracking(order.id) },
                        onViewInvoice = { viewModel.selectOrderForInvoice(order) },
                        onReorderItem = { item -> viewModel.reorderItem(item) },
                        onReorderAll = { viewModel.reorderAllItems(order) },
                        onNavigateToProduct = onNavigateToProduct,
                        context = context
                    )
                }
            }
        }
    }

    // Comprehensive Luxury Receipt & Invoice Inspection Modal
    selectedOrderForInvoice?.let { order ->
        WatchOrderInvoiceDialog(
            order = order,
            onDismiss = { viewModel.selectOrderForInvoice(null) },
            onTrackOrder = {
                viewModel.selectOrderForInvoice(null)
                onNavigateToTracking(order.id)
            },
            context = context
        )
    }
}

@Composable
private fun OrderStatusFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) ObsidianBlack else LiteAshBg,
        shape = RoundedCornerShape(2.dp),
        border = BorderStroke(0.8.dp, if (isSelected) ObsidianBlack else GrayBorder),
        modifier = Modifier
            .clickable { onClick() }
            .testTag("filter_chip_$label")
    ) {
        Text(
            text = label,
            color = if (isSelected) PureWhite else ObsidianBlack,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 0.6.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun OrderHistoryCard(
    order: Order,
    onTrackOrder: () -> Unit,
    onViewInvoice: () -> Unit,
    onReorderItem: (OrderLineItem) -> Unit,
    onReorderAll: () -> Unit,
    onNavigateToProduct: (Long) -> Unit,
    context: Context
) {
    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, GrayBorder),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_card_${order.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Order ID + Date + Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ORDER #${order.id}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = ObsidianBlack
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy Order ID",
                            tint = ObsidianBlack.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(14.dp)
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Order ID", order.id.toString())
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Order #${order.id} copied to clipboard", Toast.LENGTH_SHORT).show()
                                }
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatOrderDate(order.dateCreated),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                // Dynamic Status Pill
                val statusText = when (order.status.lowercase()) {
                    "completed", "delivered" -> "DELIVERED"
                    "shipped", "in_transit" -> "IN TRANSIT"
                    "processing" -> "PROCESSING"
                    "cancelled" -> "CANCELLED"
                    "on-hold" -> "ON HOLD"
                    else -> order.status.uppercase()
                }

                val statusBg = when (order.status.lowercase()) {
                    "completed", "delivered" -> ForestGreen.copy(alpha = 0.12f)
                    "shipped", "in_transit" -> ChampagneGold.copy(alpha = 0.25f)
                    "processing" -> WarmGold.copy(alpha = 0.2f)
                    "cancelled" -> LuxuryRed.copy(alpha = 0.12f)
                    else -> LiteAshBg
                }

                val statusColor = when (order.status.lowercase()) {
                    "completed", "delivered" -> ForestGreen
                    "shipped", "in_transit" -> ObsidianBlack
                    "processing" -> ObsidianBlack
                    "cancelled" -> LuxuryRed
                    else -> MaterialTheme.colorScheme.onSurface
                }

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = statusText,
                            color = statusColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SubtleDivider)

            // Tracking info banner (if assigned)
            if (order.trackingNumber != null) {
                Surface(
                    color = LiteAshBg,
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable { onTrackOrder() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.LocalShipping,
                                contentDescription = null,
                                tint = ObsidianBlack,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "AWB: ${order.trackingNumber}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ObsidianBlack,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = order.estimatedDelivery ?: "Complimentary Insured Courier",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Track",
                            tint = ObsidianBlack,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Purchased Watch Items Breakdown
            order.lineItems.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (item.productId > 0) {
                                    onNavigateToProduct(item.productId)
                                }
                            }
                    ) {
                        if (item.imageUrl.isNotBlank()) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .border(0.8.dp, GrayBorder, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(LiteAshBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Watch,
                                    contentDescription = null,
                                    tint = ObsidianBlack.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = ObsidianBlack,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Qty: ${item.quantity}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                                if (item.sku.isNotBlank()) {
                                    Text(
                                        text = "Ref: ${item.sku}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(horizontalAlignment = Alignment.End) {
                        val itemTotalFormatted = item.total.toDoubleOrNull()?.let {
                            formatInr(it)
                        } ?: "₹${item.total}"
                        Text(
                            text = itemTotalFormatted,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = ObsidianBlack
                        )

                        // Buy Again Quick Action
                        Text(
                            text = "Buy Again",
                            color = ObsidianBlack,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { onReorderItem(item) }
                                .padding(top = 2.dp)
                                .testTag("buy_again_item_${item.id}")
                        )
                    }
                }

                if (index < order.lineItems.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = SubtleDivider)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SubtleDivider)

            // Payment method & Authenticity info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CreditCard,
                        contentDescription = null,
                        tint = ObsidianBlack.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = order.paymentMethodTitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Verified,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "100% Authentic Guaranteed",
                        style = MaterialTheme.typography.labelSmall,
                        color = ForestGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Row with Total Paid & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOTAL TRANSACTION",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp,
                        letterSpacing = 0.6.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val orderTotalFormatted = order.total.toDoubleOrNull()?.let {
                        formatInr(it)
                    } ?: "₹${order.total}"
                    Text(
                        text = orderTotalFormatted,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = ObsidianBlack
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Invoice / Receipt button
                    OutlinedButton(
                        onClick = onViewInvoice,
                        shape = RoundedCornerShape(2.dp),
                        border = BorderStroke(1.dp, ObsidianBlack),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("view_invoice_order_${order.id}_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ReceiptLong,
                            contentDescription = null,
                            tint = ObsidianBlack,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "RECEIPT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.6.sp,
                            color = ObsidianBlack
                        )
                    }

                    // Live Track button
                    Button(
                        onClick = onTrackOrder,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ObsidianBlack,
                            contentColor = PureWhite
                        ),
                        shape = RoundedCornerShape(2.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("track_order_${order.id}_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.LocalShipping,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "TRACK LIVE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.6.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Luxury Horology Purchase Receipt / Invoice Dialog.
 * Shows detailed itemization, customer information, payment details,
 * taxes, and authenticity certificates.
 */
@Composable
private fun WatchOrderInvoiceDialog(
    order: Order,
    onDismiss: () -> Unit,
    onTrackOrder: () -> Unit,
    context: Context
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = PureWhite,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("watch_invoice_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header: Logo & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "U STORE 4U",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.6.sp,
                            color = ObsidianBlack
                        )
                        Text(
                            text = "HAUTE HORLOGERIE INVOICE",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 9.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_invoice_button")
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = ObsidianBlack)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = ObsidianBlack, thickness = 1.5.dp)

                // Meta Information Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("INVOICE NO:", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text("INV-${order.id}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack)

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("DATE & TIME:", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text(formatOrderDate(order.dateCreated), fontSize = 11.sp, color = ObsidianBlack)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("PAYMENT METHOD:", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text(order.paymentMethodTitle, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ObsidianBlack)

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("STATUS:", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text(order.status.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Addresses Section
                Surface(
                    color = LiteAshBg,
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("CONSIGNED TO (SHIPPING):", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack, letterSpacing = 0.5.sp)
                            Text(order.shipping.fullName.ifBlank { "Valued Client" }, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                            Text(order.shipping.formattedOneLine.ifBlank { "Hyderabad, India" }, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (order.shipping.phone.isNotBlank()) {
                                Text("Tel: ${order.shipping.phone}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        if (order.trackingNumber != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.End) {
                                Text("COURIER CONSIGNMENT:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack, letterSpacing = 0.5.sp)
                                Text(order.trackingNumber, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                                Text("Insured Vault Shipping", fontSize = 10.sp, color = ForestGreen, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Line Items Table
                Text("PURCHASED TIMEPIECES", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp, color = ObsidianBlack)
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = SubtleDivider)

                order.lineItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ObsidianBlack)
                            Text("Qty: ${item.quantity}  •  SKU: ${item.sku.ifBlank { "U4U-LUX" }}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        val itemPriceFormatted = item.total.toDoubleOrNull()?.let { formatInr(it) } ?: "₹${item.total}"
                        Text(itemPriceFormatted, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = SubtleDivider)

                // Financial Breakdown
                val subtotalVal = order.lineItems.sumOf { it.total.toDoubleOrNull() ?: 0.0 }
                val discountVal = order.discountTotal.toDoubleOrNull() ?: 0.0
                val shippingVal = order.shippingTotal.toDoubleOrNull() ?: 0.0
                val totalVal = order.total.toDoubleOrNull() ?: subtotalVal

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatInr(subtotalVal), fontSize = 11.sp, color = ObsidianBlack)
                }

                if (discountVal > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Collector Privilege Discount", fontSize = 11.sp, color = ForestGreen)
                        Text("- ${formatInr(discountVal)}", fontSize = 11.sp, color = ForestGreen)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Insured Armed Courier Delivery", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(if (shippingVal == 0.0) "COMPLIMENTARY" else formatInr(shippingVal), fontSize = 11.sp, color = if (shippingVal == 0.0) ForestGreen else ObsidianBlack)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = ObsidianBlack)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("GRAND TOTAL PAID", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = ObsidianBlack)
                    Text(formatInr(totalVal), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = ObsidianBlack)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Authenticity & Certification Seal
                Surface(
                    color = LiteAshBg,
                    shape = RoundedCornerShape(2.dp),
                    border = BorderStroke(0.8.dp, ChampagneGold.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Backed by 2-Year International Warranty & Serial Number Certificate of Authenticity.",
                            fontSize = 9.sp,
                            color = ObsidianBlack,
                            lineHeight = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Share / Export Receipt & Track
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareText = """
                                UStore4U Official Horology Invoice #INV-${order.id}
                                Date: ${formatOrderDate(order.dateCreated)}
                                Total: ${formatInr(totalVal)}
                                Status: ${order.status.uppercase()}
                                Consignment AWB: ${order.trackingNumber ?: "Pending"}
                                Items:
                                ${order.lineItems.joinToString("\n") { "- ${it.name} (Qty: ${it.quantity}) - ₹${it.total}" }}
                            """.trimIndent()

                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Order Receipt"))
                        },
                        shape = RoundedCornerShape(2.dp),
                        border = BorderStroke(1.dp, ObsidianBlack),
                        modifier = Modifier.weight(1f).height(40.dp).testTag("share_invoice_button")
                    ) {
                        Icon(Icons.Outlined.Share, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SHARE RECEIPT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                    }

                    Button(
                        onClick = onTrackOrder,
                        colors = ButtonDefaults.buttonColors(containerColor = ObsidianBlack, contentColor = PureWhite),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.weight(1f).height(40.dp).testTag("track_invoice_order_button")
                    ) {
                        Icon(Icons.Outlined.LocalShipping, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("TRACK LIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
