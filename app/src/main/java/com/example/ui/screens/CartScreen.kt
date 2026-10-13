package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Discount
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.api.WooCommerceConfig
import com.example.data.local.CartEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.QuantityStepper
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GrayBorder
import com.example.ui.theme.LiteAshBg
import com.example.ui.theme.LiteAshWhite
import com.example.ui.theme.LuxuryRed
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SubtleDivider
import com.example.ui.theme.WestsideTerracotta
import com.example.ui.viewmodel.CartViewModel

private fun formatInr(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        "₹%,d".format(amount.toLong())
    } else {
        "₹%,.2f".format(amount)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    onExploreWatches: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val subtotal = viewModel.calculateSubtotal(cartItems)
    val discount = viewModel.calculateDiscount(subtotal, uiState.appliedCoupon)
    val shipping = viewModel.calculateShipping(subtotal)
    val total = viewModel.calculateTotal(subtotal, discount, shipping)

    val freeShippingNeeded = (WooCommerceConfig.FREE_SHIPPING_THRESHOLD - subtotal).coerceAtLeast(0.0)
    val freeShippingProgress = (subtotal / WooCommerceConfig.FREE_SHIPPING_THRESHOLD).toFloat().coerceIn(0f, 1f)

    Scaffold(
        containerColor = LiteAshWhite,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Text(
                            text = "SHOPPING BAG (${cartItems.sumOf { it.quantity }})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = ObsidianBlack
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("cart_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = ObsidianBlack
                            )
                        }
                    },
                    actions = {
                        if (cartItems.isNotEmpty()) {
                            TextButton(
                                onClick = { viewModel.clearCart() },
                                modifier = Modifier.testTag("clear_cart_button")
                            ) {
                                Text(
                                    "CLEAR ALL",
                                    color = ObsidianBlack,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.8.sp
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
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(thickness = 0.8.dp, color = SubtleDivider)
                    Surface(
                        color = PureWhite,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "TOTAL AMOUNT",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                    Text(
                                        text = formatInr(total),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ObsidianBlack
                                    )
                                }

                                Button(
                                    onClick = onNavigateToCheckout,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ObsidianBlack,
                                        contentColor = PureWhite
                                    ),
                                    shape = RoundedCornerShape(2.dp),
                                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                                    modifier = Modifier.testTag("proceed_checkout_button")
                                ) {
                                    Text(
                                        "PROCEED TO CHECKOUT",
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier.testTag("cart_screen")
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            EmptyStateView(
                icon = Icons.Outlined.ShoppingBag,
                title = "YOUR BAG IS EMPTY",
                message = "Explore our latest collection of premium Titan, Fastrack, Sonata timepieces and wall decor.",
                actionButtonText = "START SHOPPING",
                onActionClick = onExploreWatches,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Kukatpally Express Delivery Badge
                item {
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = PureWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FlashOn,
                                contentDescription = null,
                                tint = ObsidianBlack,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "2-HOUR EXPRESS DELIVERY",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = ObsidianBlack
                                )
                                Text(
                                    text = "Available for Kukatpally, Miyapur, Hitec City zones",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Free Shipping Progress Meter
                item {
                    Card(
                        shape = RoundedCornerShape(2.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.LocalShipping,
                                    contentDescription = null,
                                    tint = ObsidianBlack,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (freeShippingNeeded <= 0.0) {
                                        "FREE PAN-INDIA DELIVERY UNLOCKED"
                                    } else {
                                        "ADD ${formatInr(freeShippingNeeded)} MORE FOR FREE DELIVERY"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.6.sp,
                                    color = ObsidianBlack
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { freeShippingProgress },
                                color = ObsidianBlack,
                                trackColor = LiteAshBg,
                                modifier = Modifier.fillMaxWidth().height(4.dp)
                            )
                        }
                    }
                }

                // Cart Items List
                items(
                    items = cartItems,
                    key = { it.id },
                    contentType = { "cart_item" }
                ) { item ->
                    CartItemCard(
                        item = item,
                        onQuantityChange = { newQty -> viewModel.updateQuantity(item, newQty) },
                        onRemove = { viewModel.removeItem(item) },
                        onSaveForLater = { viewModel.saveForLater(item) }
                    )
                }

                // Promo / Coupon Code Section
                item {
                    Card(
                        shape = RoundedCornerShape(2.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Discount,
                                        contentDescription = null,
                                        tint = ObsidianBlack,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "COUPONS & OFFERS",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                }

                                if (uiState.appliedCoupon != null) {
                                    Surface(
                                        color = LiteAshBg,
                                        shape = RoundedCornerShape(2.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${uiState.appliedCoupon?.code} APPLIED",
                                                color = ObsidianBlack,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Outlined.Close,
                                                contentDescription = "Remove coupon",
                                                tint = ObsidianBlack,
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clickable { viewModel.removeCoupon() }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (uiState.appliedCoupon == null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = uiState.couponCodeInput,
                                        onValueChange = { viewModel.onCouponCodeChanged(it) },
                                        placeholder = { Text("Try USTORE10 or KUKATPALLY50", fontSize = 12.sp) },
                                        singleLine = true,
                                        shape = RoundedCornerShape(2.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = LiteAshBg,
                                            unfocusedContainerColor = LiteAshBg,
                                            focusedBorderColor = ObsidianBlack,
                                            unfocusedBorderColor = GrayBorder
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("coupon_input_field")
                                    )

                                    Button(
                                        onClick = { viewModel.applyCoupon() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ObsidianBlack,
                                            contentColor = PureWhite
                                        ),
                                        shape = RoundedCornerShape(2.dp),
                                        modifier = Modifier.testTag("apply_coupon_button")
                                    ) {
                                        if (uiState.isApplyingCoupon) {
                                            CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(16.dp))
                                        } else {
                                            Text("APPLY", fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                                        }
                                    }
                                }

                                if (uiState.couponError != null) {
                                    Text(
                                        text = uiState.couponError!!,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Order Summary Card
                item {
                    Card(
                        shape = RoundedCornerShape(2.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                "ORDER SUMMARY",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )

                            SummaryLine("Subtotal", formatInr(subtotal))

                            if (discount > 0.0) {
                                SummaryLine("Discount (${uiState.appliedCoupon?.code})", "-${formatInr(discount)}", isHighlight = true)
                            }

                            SummaryLine(
                                "Shipping",
                                if (shipping == 0.0) "FREE" else formatInr(shipping)
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = SubtleDivider)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "TOTAL PAYABLE",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    formatInr(total),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ObsidianBlack
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
private fun CartItemCard(
    item: CartEntity,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    onSaveForLater: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().testTag("cart_item_${item.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Watch Image
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(LiteAshBg)
            )

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = ObsidianBlack,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (item.selectedVariation.isNotBlank()) {
                    Text(
                        text = item.selectedVariation.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatInr(item.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ObsidianBlack
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuantityStepper(
                        quantity = item.quantity,
                        onQuantityChange = onQuantityChange,
                        min = 1,
                        max = 10
                    )

                    Row {
                        IconButton(
                            onClick = onSaveForLater,
                            modifier = Modifier.size(36.dp).testTag("save_later_${item.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = "Save for later",
                                tint = ObsidianBlack,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onRemove,
                            modifier = Modifier.size(36.dp).testTag("remove_item_${item.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = "Remove item",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryLine(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
            color = if (isHighlight) WestsideTerracotta else ObsidianBlack
        )
    }
}
