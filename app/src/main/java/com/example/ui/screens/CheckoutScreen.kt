package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.api.WooCommerceConfig
import com.example.data.model.Order
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GrayBorder
import com.example.ui.theme.LiteAshBg
import com.example.ui.theme.LiteAshWhite
import com.example.ui.theme.LuxuryRed
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SubtleDivider
import com.example.ui.theme.WestsideTerracotta
import com.example.ui.viewmodel.CheckoutViewModel

private fun formatInr(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        "₹%,d".format(amount.toLong())
    } else {
        "₹%,.2f".format(amount)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel,
    onNavigateBack: () -> Unit,
    onOrderSuccess: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val shippingCost = if (uiState.shippingMethod == "express") {
        WooCommerceConfig.EXPRESS_SHIPPING_COST
    } else {
        if (subtotal >= WooCommerceConfig.FREE_SHIPPING_THRESHOLD) 0.0 else WooCommerceConfig.STANDARD_SHIPPING_COST
    }
    val total = (subtotal + shippingCost).coerceAtLeast(0.0)

    Scaffold(
        containerColor = LiteAshWhite,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Text(
                            "SECURE CHECKOUT",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            letterSpacing = 1.2.sp,
                            color = ObsidianBlack
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("checkout_back_button")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = ObsidianBlack
                            )
                        }
                    },
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("256-bit SSL", style = MaterialTheme.typography.labelSmall, color = ObsidianBlack, fontWeight = FontWeight.Bold)
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
            Surface(
                color = PureWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.placeOrder(onSuccess = { order ->
                                onOrderSuccess(order.id)
                            })
                        },
                        enabled = !uiState.isSubmitting && cartItems.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ObsidianBlack,
                            contentColor = PureWhite
                        ),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("place_order_button")
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(20.dp))
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "PLACE ORDER • ${formatInr(total)}",
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier.testTag("checkout_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Contact & Customer Information
            item {
                SectionCard(title = "1. CUSTOMER & CONTACT DETAILS") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.firstName,
                            onValueChange = { viewModel.onFirstNameChanged(it) },
                            label = { Text("First Name *") },
                            singleLine = true,
                            shape = RoundedCornerShape(2.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = LiteAshBg,
                                unfocusedContainerColor = LiteAshBg,
                                focusedBorderColor = ObsidianBlack,
                                unfocusedBorderColor = GrayBorder
                            ),
                            modifier = Modifier.weight(1f).testTag("checkout_first_name")
                        )
                        OutlinedTextField(
                            value = uiState.lastName,
                            onValueChange = { viewModel.onLastNameChanged(it) },
                            label = { Text("Last Name *") },
                            singleLine = true,
                            shape = RoundedCornerShape(2.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = LiteAshBg,
                                unfocusedContainerColor = LiteAshBg,
                                focusedBorderColor = ObsidianBlack,
                                unfocusedBorderColor = GrayBorder
                            ),
                            modifier = Modifier.weight(1f).testTag("checkout_last_name")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.onEmailChanged(it) },
                        label = { Text("Email Address (for order receipts) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(2.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LiteAshBg,
                            unfocusedContainerColor = LiteAshBg,
                            focusedBorderColor = ObsidianBlack,
                            unfocusedBorderColor = GrayBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("checkout_email")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.phone,
                        onValueChange = { viewModel.onPhoneChanged(it) },
                        label = { Text("Phone / WhatsApp Number (for delivery updates) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(2.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LiteAshBg,
                            unfocusedContainerColor = LiteAshBg,
                            focusedBorderColor = ObsidianBlack,
                            unfocusedBorderColor = GrayBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("checkout_phone")
                    )
                }
            }

            // 2. Shipping Address
            item {
                SectionCard(title = "2. SHIPPING ADDRESS") {
                    OutlinedTextField(
                        value = uiState.address1,
                        onValueChange = { viewModel.onAddress1Changed(it) },
                        label = { Text("Street Address, Flat / House No *") },
                        singleLine = true,
                        shape = RoundedCornerShape(2.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LiteAshBg,
                            unfocusedContainerColor = LiteAshBg,
                            focusedBorderColor = ObsidianBlack,
                            unfocusedBorderColor = GrayBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("checkout_address1")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.city,
                            onValueChange = { viewModel.onCityChanged(it) },
                            label = { Text("City (e.g. Hyderabad) *") },
                            singleLine = true,
                            shape = RoundedCornerShape(2.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = LiteAshBg,
                                unfocusedContainerColor = LiteAshBg,
                                focusedBorderColor = ObsidianBlack,
                                unfocusedBorderColor = GrayBorder
                            ),
                            modifier = Modifier.weight(1f).testTag("checkout_city")
                        )
                        OutlinedTextField(
                            value = uiState.state,
                            onValueChange = { viewModel.onStateChanged(it) },
                            label = { Text("State (e.g. Telangana)") },
                            singleLine = true,
                            shape = RoundedCornerShape(2.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = LiteAshBg,
                                unfocusedContainerColor = LiteAshBg,
                                focusedBorderColor = ObsidianBlack,
                                unfocusedBorderColor = GrayBorder
                            ),
                            modifier = Modifier.weight(1f).testTag("checkout_state")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.postcode,
                            onValueChange = { viewModel.onPostcodeChanged(it) },
                            label = { Text("PIN / Postal Code *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(2.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = LiteAshBg,
                                unfocusedContainerColor = LiteAshBg,
                                focusedBorderColor = ObsidianBlack,
                                unfocusedBorderColor = GrayBorder
                            ),
                            modifier = Modifier.weight(1f).testTag("checkout_postcode")
                        )
                        OutlinedTextField(
                            value = uiState.country,
                            onValueChange = { viewModel.onCountryChanged(it) },
                            label = { Text("Country") },
                            singleLine = true,
                            shape = RoundedCornerShape(2.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = LiteAshBg,
                                unfocusedContainerColor = LiteAshBg,
                                focusedBorderColor = ObsidianBlack,
                                unfocusedBorderColor = GrayBorder
                            ),
                            modifier = Modifier.weight(1f).testTag("checkout_country")
                        )
                    }
                }
            }

            // 3. Delivery Method Selection
            item {
                SectionCard(title = "3. DELIVERY OPTION") {
                    DeliveryOptionRow(
                        title = "2-Hour Express Delivery (Kukatpally 10 km Zone)",
                        price = formatInr(WooCommerceConfig.EXPRESS_SHIPPING_COST),
                        isSelected = uiState.shippingMethod == "express",
                        onClick = { viewModel.onShippingMethodChanged("express") }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    DeliveryOptionRow(
                        title = "Standard Pan-India Insured Delivery (2-4 Days)",
                        price = if (subtotal >= WooCommerceConfig.FREE_SHIPPING_THRESHOLD) "FREE" else formatInr(WooCommerceConfig.STANDARD_SHIPPING_COST),
                        isSelected = uiState.shippingMethod == "standard",
                        onClick = { viewModel.onShippingMethodChanged("standard") }
                    )
                }
            }

            // 4. Payment Method
            item {
                SectionCard(title = "4. PAYMENT METHOD") {
                    PaymentOptionRow(
                        icon = Icons.Outlined.QrCode2,
                        title = "UPI / Google Pay / PhonePe / Paytm / Cards",
                        subtitle = "Instant, secure payment via ustore4u.com gateway",
                        isSelected = uiState.paymentMethod == "web_checkout",
                        onClick = { viewModel.onPaymentMethodChanged("web_checkout") }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PaymentOptionRow(
                        icon = Icons.Outlined.LocalShipping,
                        title = "Cash on Delivery (COD)",
                        subtitle = "Pay upon delivery at your doorstep",
                        isSelected = uiState.paymentMethod == "cod",
                        onClick = { viewModel.onPaymentMethodChanged("cod") }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PaymentOptionRow(
                        icon = Icons.Outlined.AccountBalance,
                        title = "Direct Bank Transfer (NEFT / IMPS)",
                        subtitle = "Direct account transfer to UStore4U official account",
                        isSelected = uiState.paymentMethod == "bacs",
                        onClick = { viewModel.onPaymentMethodChanged("bacs") }
                    )
                }
            }

            // 5. Order Notes (Optional)
            item {
                SectionCard(title = "5. ORDER NOTES (OPTIONAL)") {
                    OutlinedTextField(
                        value = uiState.orderNotes,
                        onValueChange = { viewModel.onOrderNotesChanged(it) },
                        placeholder = { Text("Special delivery instructions, gift wrapping note, landmark...") },
                        shape = RoundedCornerShape(2.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LiteAshBg,
                            unfocusedContainerColor = LiteAshBg,
                            focusedBorderColor = ObsidianBlack,
                            unfocusedBorderColor = GrayBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 6. Review Order Items
            item {
                SectionCard(title = "ORDER REVIEW (${cartItems.size} items)") {
                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = ObsidianBlack)
                                Text("Qty: ${item.quantity}  •  ${item.selectedVariation}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(formatInr(item.price * item.quantity), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = SubtleDivider)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", style = MaterialTheme.typography.bodyMedium, color = ObsidianBlack)
                        Text(formatInr(subtotal), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = ObsidianBlack)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Shipping", style = MaterialTheme.typography.bodyMedium, color = ObsidianBlack)
                        Text(if (shippingCost == 0.0) "FREE" else formatInr(shippingCost), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = ObsidianBlack)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = SubtleDivider)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Amount Due", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                        Text(formatInr(total), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title, 
                style = MaterialTheme.typography.titleSmall, 
                fontWeight = FontWeight.Bold, 
                letterSpacing = 0.8.sp,
                color = ObsidianBlack
            )
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun DeliveryOptionRow(
    title: String,
    price: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(2.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) ObsidianBlack else GrayBorder
        ),
        color = if (isSelected) LiteAshBg else PureWhite,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(selectedColor = ObsidianBlack)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = ObsidianBlack
                )
            }
            Text(
                text = price,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (price == "FREE") ForestGreen else ObsidianBlack
            )
        }
    }
}

@Composable
private fun PaymentOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(2.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) ObsidianBlack else GrayBorder
        ),
        color = if (isSelected) LiteAshBg else PureWhite,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = ObsidianBlack)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ObsidianBlack,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = ObsidianBlack
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
