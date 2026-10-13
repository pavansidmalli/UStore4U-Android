package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Api
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.api.WooCommerceConfig
import com.example.data.model.Address
import com.example.data.model.Customer
import com.example.data.model.Order
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GrayBorder
import com.example.ui.theme.LiteAshBg
import com.example.ui.theme.LiteAshWhite
import com.example.ui.theme.LuxuryRed
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SubtleDivider
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WestsideTerracotta
import com.example.ui.viewmodel.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: AccountViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToTracking: (Long?) -> Unit = {},
    onExploreWatches: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentCustomer by viewModel.currentCustomer.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val isLiveApi by viewModel.isLiveApiEnabled.collectAsState()
    val context = LocalContext.current

    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = LiteAshWhite,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                if (currentCustomer?.isOwner == true) "OWNER PORTAL" else "MY ACCOUNT",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                letterSpacing = 1.4.sp,
                                color = ObsidianBlack
                            )
                            Text(
                                if (currentCustomer?.isOwner == true) "Store Director & Management" else if (currentCustomer != null) "VIP Client Concierge" else "Client & Owner Authentication",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("account_back_button")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = ObsidianBlack
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToCart, modifier = Modifier.testTag("account_cart_button")) {
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
        modifier = modifier.testTag("account_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // -------------------------------------------------------------
            // SECTION 1: PROFILE HEADER OR LOGIN / REGISTRATION STUB
            // -------------------------------------------------------------
            if (currentCustomer == null) {
                item {
                    AuthStubCard(
                        uiState = uiState,
                        onOwnerTabSelect = { isOwner -> viewModel.setOwnerTabSelected(isOwner) },
                        onOwnerPinChange = { viewModel.onOwnerPinChanged(it) },
                        onOwnerNameChange = { viewModel.onOwnerNameChanged(it) },
                        onChannelSelect = { viewModel.setAuthChannel(it) },
                        onMobileChange = { viewModel.onMobileChanged(it) },
                        onEmailChange = { viewModel.onEmailChanged(it) },
                        onPasswordChange = { viewModel.onPasswordChanged(it) },
                        onFirstNameChange = { viewModel.onFirstNameChanged(it) },
                        onLastNameChange = { viewModel.onLastNameChanged(it) },
                        onOtpCodeChange = { viewModel.onOtpCodeChanged(it) },
                        onSendOtp = { viewModel.sendOtp() },
                        onResendOtp = { viewModel.resendOtp() },
                        onBackToIdentifier = { viewModel.backToIdentifierInput() },
                        onVerifyOtp = { viewModel.verifyOtpAndAuthenticate() },
                        onSelectMode = { isLogin -> viewModel.setAuthMode(isLogin) },
                        onQuickFill = { isVIP -> viewModel.quickFillDemo(isVIP) },
                        onSubmit = { viewModel.submitAuth() }
                    )
                }
            } else {
                item {
                    CustomerProfileCard(
                        customer = currentCustomer!!,
                        onLogoutClick = { showLogoutConfirmDialog = true },
                        onEditProfile = { viewModel.startEditingAddress("shipping") }
                    )
                }
            }

            // -------------------------------------------------------------
            // SECTION 2: ORDER HISTORY & SHIPMENT TRACKING STUB
            // -------------------------------------------------------------
            item {
                OrderHistoryStubSection(
                    orders = orders,
                    onViewAllOrders = onNavigateToOrders,
                    onExploreWatches = onExploreWatches
                )
            }

            // -------------------------------------------------------------
            // SECTION 3: SAVED ADDRESSES STUB
            // -------------------------------------------------------------
            item {
                SavedAddressesStubSection(
                    customer = currentCustomer,
                    onEditShipping = { viewModel.startEditingAddress("shipping") },
                    onEditBilling = { viewModel.startEditingAddress("billing") },
                    onAddNewAddress = { viewModel.startEditingAddress("shipping") }
                )
            }

            // -------------------------------------------------------------
            // SECTION 4: HOROLOGY SERVICES & QUICK NAVIGATION
            // -------------------------------------------------------------
            item {
                Card(
                    shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, GrayBorder),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        AccountSectionHeader(title = "CLIENT SERVICES & PREFERENCES")
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp), color = SubtleDivider)

                        AccountMenuRow(
                            icon = Icons.Filled.LocalShipping,
                            title = "Live Watch Tracking & AWB",
                            subtitle = "Real-time WooCommerce status, milestones & carrier tracking",
                            onClick = { onNavigateToTracking(null) },
                            testTag = "menu_tracking_row"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp), color = SubtleDivider)

                        AccountMenuRow(
                            icon = Icons.Outlined.BookmarkBorder,
                            title = "My Saved Watches",
                            subtitle = "Manage your horology wishlist & private alerts",
                            onClick = onNavigateToWishlist,
                            testTag = "menu_wishlist_row"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp), color = SubtleDivider)

                        AccountMenuRow(
                            icon = Icons.Outlined.ReceiptLong,
                            title = "All Invoices & Tracking Receipts",
                            subtitle = "Full order ledger, warranties & certificate vault",
                            onClick = onNavigateToOrders,
                            testTag = "menu_orders_row"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp), color = SubtleDivider)

                        AccountMenuRow(
                            icon = Icons.Outlined.Settings,
                            title = "Preferences & App Settings",
                            subtitle = "Notifications, currency units, cache & privacy",
                            onClick = onNavigateToSettings,
                            testTag = "menu_settings_row"
                        )
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 5: CONCIERGE & BOUTIQUE CONTACT
            // -------------------------------------------------------------
            item {
                Card(
                    shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, GrayBorder),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        AccountSectionHeader(title = "U STORE CONCIERGE & BOUTIQUE")
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp), color = SubtleDivider)

                        AccountMenuRow(
                            icon = Icons.Outlined.Phone,
                            title = "Direct Boutique Concierge",
                            subtitle = WooCommerceConfig.CONTACT_PHONE,
                            onClick = {
                                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${WooCommerceConfig.CONTACT_PHONE_RAW}"))
                                context.startActivity(dialIntent)
                            },
                            testTag = "contact_phone_row"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp), color = SubtleDivider)

                        AccountMenuRow(
                            icon = Icons.Outlined.Email,
                            title = "VIP Horology Support",
                            subtitle = WooCommerceConfig.SUPPORT_EMAIL,
                            onClick = {
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:${WooCommerceConfig.SUPPORT_EMAIL}")
                                    putExtra(Intent.EXTRA_SUBJECT, "UStore4U VIP Inquiry")
                                }
                                context.startActivity(emailIntent)
                            },
                            testTag = "contact_email_row"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp), color = SubtleDivider)

                        AccountMenuRow(
                            icon = Icons.Outlined.LocationOn,
                            title = "Flagship Boutique Hub",
                            subtitle = WooCommerceConfig.STORE_LOCATION,
                            onClick = {
                                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=Kukatpally+Hyderabad+India"))
                                context.startActivity(mapIntent)
                            },
                            testTag = "contact_location_row"
                        )
                    }
                }
            }
        }

        // -----------------------------------------------------------------
        // ADDRESS EDITING DIALOG / SHEET STUB
        // -----------------------------------------------------------------
        if (uiState.isAddressEditing) {
            AddressEditDialog(
                uiState = uiState,
                onDismiss = { viewModel.dismissAddressEditing() },
                onFirstNameChange = { viewModel.updateAddressFormField(firstName = it) },
                onLastNameChange = { viewModel.updateAddressFormField(lastName = it) },
                onCompanyChange = { viewModel.updateAddressFormField(company = it) },
                onLine1Change = { viewModel.updateAddressFormField(line1 = it) },
                onLine2Change = { viewModel.updateAddressFormField(line2 = it) },
                onCityChange = { viewModel.updateAddressFormField(city = it) },
                onStateChange = { viewModel.updateAddressFormField(state = it) },
                onPostcodeChange = { viewModel.updateAddressFormField(postcode = it) },
                onCountryChange = { viewModel.updateAddressFormField(country = it) },
                onPhoneChange = { viewModel.updateAddressFormField(phone = it) },
                onSave = { viewModel.saveAddressForm() }
            )
        }

        // -----------------------------------------------------------------
        // LOGOUT CONFIRMATION DIALOG
        // -----------------------------------------------------------------
        if (showLogoutConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirmDialog = false },
                containerColor = PureWhite,
                shape = RoundedCornerShape(2.dp),
                title = {
                    Text("Sign Out Confirmation", fontWeight = FontWeight.Bold, color = ObsidianBlack)
                },
                text = {
                    Text(
                        "Are you sure you want to sign out of your U STORE VIP account? Your local cart items will be preserved.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.logout()
                            showLogoutConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LuxuryRed, contentColor = PureWhite),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.testTag("confirm_logout_button")
                    ) {
                        Text("SIGN OUT", fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutConfirmDialog = false }) {
                        Text("CANCEL", color = ObsidianBlack, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

// =========================================================================
// COMPONENT: AUTH CARD WITH MOBILE / EMAIL OTP & REGISTRATION WORKFLOW
// =========================================================================
@Composable
private fun AuthStubCard(
    uiState: com.example.ui.viewmodel.AccountUiState,
    onOwnerTabSelect: (Boolean) -> Unit,
    onOwnerPinChange: (String) -> Unit,
    onOwnerNameChange: (String) -> Unit,
    onChannelSelect: (String) -> Unit,
    onMobileChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onOtpCodeChange: (String) -> Unit,
    onSendOtp: () -> Unit,
    onResendOtp: () -> Unit,
    onBackToIdentifier: () -> Unit,
    onVerifyOtp: () -> Unit,
    onSelectMode: (Boolean) -> Unit,
    onQuickFill: (Boolean) -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, GrayBorder),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().testTag("auth_card")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Top Level Role Switcher: Customer vs Store Owner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LiteAshBg, RoundedCornerShape(2.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Customer Login Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (!uiState.isOwnerTabSelected) ObsidianBlack else Color.Transparent)
                        .clickable { onOwnerTabSelect(false) }
                        .padding(vertical = 10.dp)
                        .testTag("account_customer_tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = null,
                            tint = if (!uiState.isOwnerTabSelected) PureWhite else ObsidianBlack,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CUSTOMER LOGIN",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (!uiState.isOwnerTabSelected) PureWhite else ObsidianBlack,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                // Store Owner Login Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (uiState.isOwnerTabSelected) ObsidianBlack else Color.Transparent)
                        .clickable { onOwnerTabSelect(true) }
                        .padding(vertical = 10.dp)
                        .testTag("account_owner_tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.AdminPanelSettings,
                            contentDescription = null,
                            tint = if (uiState.isOwnerTabSelected) PureWhite else ObsidianBlack,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OWNER PORTAL",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.isOwnerTabSelected) PureWhite else ObsidianBlack,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isOwnerTabSelected) {
                // =========================================================
                // STORE OWNER LOGIN SECTION
                // =========================================================
                Text(
                    text = "STORE OWNER & DIRECTOR LOGIN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ObsidianBlack
                )
                Text(
                    text = "Authenticate with your Director Security PIN to access boutique catalog administration and executive privileges (Default PIN: 7492).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = uiState.ownerNameInput,
                    onValueChange = onOwnerNameChange,
                    label = { Text("Owner / Director Name") },
                    placeholder = { Text("e.g. Director UStore") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Outlined.Person, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(18.dp))
                    },
                    shape = RoundedCornerShape(2.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = LiteAshBg,
                        unfocusedContainerColor = LiteAshBg,
                        focusedBorderColor = ObsidianBlack,
                        unfocusedBorderColor = GrayBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("owner_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = uiState.ownerPinInput,
                    onValueChange = onOwnerPinChange,
                    label = { Text("Owner Security PIN *") },
                    placeholder = { Text("Enter 4-8 digit Director PIN (7492)") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Outlined.Lock, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(18.dp))
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    shape = RoundedCornerShape(2.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = LiteAshBg,
                        unfocusedContainerColor = LiteAshBg,
                        focusedBorderColor = ObsidianBlack,
                        unfocusedBorderColor = GrayBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("owner_pin_input")
                )

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage,
                        color = LuxuryRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onSubmit,
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = ObsidianBlack, contentColor = PureWhite),
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("owner_login_submit_button")
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = "AUTHENTICATE AS OWNER",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            } else {
                // =========================================================
                // CUSTOMER SIGN IN & REGISTRATION
                // =========================================================
                // Mode Segmented Switcher (Sign In vs Register)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LiteAshBg, RoundedCornerShape(2.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Sign In Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (uiState.isLoginMode) ObsidianBlack else Color.Transparent)
                            .clickable { onSelectMode(true) }
                            .padding(vertical = 10.dp)
                            .testTag("account_login_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SIGN IN",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.isLoginMode) PureWhite else ObsidianBlack,
                            letterSpacing = 1.sp
                        )
                    }

                    // Register Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (!uiState.isLoginMode) ObsidianBlack else Color.Transparent)
                            .clickable { onSelectMode(false) }
                            .padding(vertical = 10.dp)
                            .testTag("account_register_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "REGISTER",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (!uiState.isLoginMode) PureWhite else ObsidianBlack,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (uiState.isLoginMode) "SIGN IN TO U STORE" else "CREATE CLIENT PROFILE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ObsidianBlack
                )
                Text(
                    text = if (uiState.isLoginMode)
                        "Access your timepieces, order shipments, and bespoke concierge privileges via OTP or password."
                    else
                        "Register as a collector to unlock priority allocations, extended warranties, and private invitations.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Channel Selector Tabs (Mobile OTP vs Email OTP vs Password)
                if (!uiState.isOtpSent) {
                    Text(
                        text = "SELECT VERIFICATION METHOD",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = ObsidianBlack
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Mobile OTP Channel
                        OutlinedButton(
                            onClick = { onChannelSelect("mobile") },
                            shape = RoundedCornerShape(2.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (uiState.authChannel == "mobile") ObsidianBlack else PureWhite,
                                contentColor = if (uiState.authChannel == "mobile") PureWhite else ObsidianBlack
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (uiState.authChannel == "mobile") ObsidianBlack else GrayBorder
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("auth_channel_mobile")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Icon(
                                    Icons.Outlined.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (uiState.authChannel == "mobile") PureWhite else ObsidianBlack
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Mobile OTP",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Email OTP Channel
                        OutlinedButton(
                            onClick = { onChannelSelect("email") },
                            shape = RoundedCornerShape(2.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (uiState.authChannel == "email") ObsidianBlack else PureWhite,
                                contentColor = if (uiState.authChannel == "email") PureWhite else ObsidianBlack
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (uiState.authChannel == "email") ObsidianBlack else GrayBorder
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("auth_channel_email")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Icon(
                                    Icons.Outlined.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (uiState.authChannel == "email") PureWhite else ObsidianBlack
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Email OTP",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Password Channel
                        OutlinedButton(
                            onClick = { onChannelSelect("password") },
                            shape = RoundedCornerShape(2.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (uiState.authChannel == "password") ObsidianBlack else PureWhite,
                                contentColor = if (uiState.authChannel == "password") PureWhite else ObsidianBlack
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (uiState.authChannel == "password") ObsidianBlack else GrayBorder
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("auth_channel_password")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Icon(
                                    Icons.Outlined.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (uiState.authChannel == "password") PureWhite else ObsidianBlack
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Password",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // -------------------------------------------------------------
                // STEP 1: ENTER CREDENTIALS / REQUEST OTP
                // -------------------------------------------------------------
                if (!uiState.isOtpSent) {
                    // Registration specific fields
                    if (!uiState.isLoginMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = uiState.firstNameInput,
                                onValueChange = onFirstNameChange,
                                label = { Text("First Name *") },
                                singleLine = true,
                                shape = RoundedCornerShape(2.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = LiteAshBg,
                                    unfocusedContainerColor = LiteAshBg,
                                    focusedBorderColor = ObsidianBlack,
                                    unfocusedBorderColor = GrayBorder
                                ),
                                modifier = Modifier.weight(1f).testTag("auth_first_name")
                            )
                            OutlinedTextField(
                                value = uiState.lastNameInput,
                                onValueChange = onLastNameChange,
                                label = { Text("Last Name") },
                                singleLine = true,
                                shape = RoundedCornerShape(2.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = LiteAshBg,
                                    unfocusedContainerColor = LiteAshBg,
                                    focusedBorderColor = ObsidianBlack,
                                    unfocusedBorderColor = GrayBorder
                                ),
                                modifier = Modifier.weight(1f).testTag("auth_last_name")
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Mobile Number Field
                    if (uiState.authChannel == "mobile") {
                        OutlinedTextField(
                            value = uiState.mobileInput,
                            onValueChange = onMobileChange,
                            label = { Text("Mobile Number (with Country Code) *") },
                            placeholder = { Text("+1 (555) 392-1084 or +91 98765 43210") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Outlined.Phone, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(18.dp))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(2.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = LiteAshBg,
                                unfocusedContainerColor = LiteAshBg,
                                focusedBorderColor = ObsidianBlack,
                                unfocusedBorderColor = GrayBorder
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("auth_mobile_input")
                        )
                    }

                    // Email Field
                    if (uiState.authChannel == "email" || uiState.authChannel == "password") {
                        OutlinedTextField(
                            value = uiState.emailInput,
                            onValueChange = onEmailChange,
                            label = { Text("Email Address *") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Outlined.Email, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(18.dp))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(2.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = LiteAshBg,
                                unfocusedContainerColor = LiteAshBg,
                                focusedBorderColor = ObsidianBlack,
                                unfocusedBorderColor = GrayBorder
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("auth_email")
                        )
                    }

                    // Password Field (Only for password channel)
                    if (uiState.authChannel == "password") {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = uiState.passwordInput,
                            onValueChange = onPasswordChange,
                            label = { Text("Password *") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Outlined.Lock, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(18.dp))
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape = RoundedCornerShape(2.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = LiteAshBg,
                                unfocusedContainerColor = LiteAshBg,
                                focusedBorderColor = ObsidianBlack,
                                unfocusedBorderColor = GrayBorder
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("auth_password")
                        )
                    }

                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage,
                            color = LuxuryRed,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Submit / Get OTP Button
                    Button(
                        onClick = {
                            if (uiState.authChannel == "password") {
                                onSubmit()
                            } else {
                                onSendOtp()
                            }
                        },
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = ObsidianBlack, contentColor = PureWhite),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_submit_button")
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            val label = when {
                                uiState.authChannel == "mobile" -> if (uiState.isLoginMode) "GET MOBILE OTP" else "REQUEST OTP TO REGISTER"
                                uiState.authChannel == "email" -> if (uiState.isLoginMode) "GET EMAIL OTP" else "REQUEST OTP TO REGISTER"
                                else -> if (uiState.isLoginMode) "SIGN IN TO ACCOUNT" else "CREATE ACCOUNT"
                            }
                            Text(
                                text = label,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                } else {
                    // -------------------------------------------------------------
                    // STEP 2: OTP VERIFICATION VIEW
                    // -------------------------------------------------------------
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LiteAshBg, RoundedCornerShape(2.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (uiState.authChannel == "mobile") Icons.Outlined.Phone else Icons.Outlined.Email,
                                    contentDescription = null,
                                    tint = ObsidianBlack,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "OTP Verification Code Sent",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ObsidianBlack
                                )
                            }

                            TextButton(
                                onClick = onBackToIdentifier,
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "CHANGE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LuxuryRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = "We sent a 6-digit one-time passcode to ${if (uiState.authChannel == "mobile") uiState.mobileInput else uiState.emailInput}.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Demo / Instant OTP Helper Badge
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PureWhite, RoundedCornerShape(2.dp))
                                .border(BorderStroke(1.dp, GrayBorder), RoundedCornerShape(2.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "SIMULATED SMS / EMAIL OTP:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = uiState.generatedOtp,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 3.sp,
                                    color = ForestGreen
                                )
                            }

                            OutlinedButton(
                                onClick = { onOtpCodeChange(uiState.generatedOtp) },
                                shape = RoundedCornerShape(2.dp),
                                border = BorderStroke(0.8.dp, ForestGreen),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp).testTag("auto_fill_otp_button")
                            ) {
                                Text("AUTO-FILL", fontSize = 10.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 6-Digit OTP Code Input
                    OutlinedTextField(
                        value = uiState.otpCode,
                        onValueChange = onOtpCodeChange,
                        label = { Text("Enter 6-Digit OTP Code") },
                        placeholder = { Text("••••••") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(18.dp))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(2.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LiteAshBg,
                            unfocusedContainerColor = LiteAshBg,
                            focusedBorderColor = ObsidianBlack,
                            unfocusedBorderColor = GrayBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("auth_otp_input")
                    )

                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage,
                            color = LuxuryRed,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Verify Button
                    Button(
                        onClick = onVerifyOtp,
                        enabled = !uiState.isLoading && uiState.otpCode.length >= 4,
                        colors = ButtonDefaults.buttonColors(containerColor = ObsidianBlack, contentColor = PureWhite),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("verify_otp_submit_button")
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = if (uiState.isLoginMode) "VERIFY OTP & SIGN IN" else "VERIFY OTP & CREATE ACCOUNT",
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Resend OTP Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Didn't receive the code? ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(
                            onClick = onResendOtp,
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                            modifier = Modifier.testTag("resend_otp_button")
                        ) {
                            Text(
                                "Resend OTP",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = ObsidianBlack
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Fill Demo Account Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Demo Account:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { onQuickFill(true) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(2.dp),
                        border = BorderStroke(0.8.dp, GrayBorder),
                        modifier = Modifier.height(30.dp).testTag("quick_fill_vip_button")
                    ) {
                        Text("VIP Member", fontSize = 10.sp, color = ObsidianBlack, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = { onQuickFill(false) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(2.dp),
                        border = BorderStroke(0.8.dp, GrayBorder),
                        modifier = Modifier.height(30.dp).testTag("quick_fill_client_button")
                    ) {
                        Text("New Client", fontSize = 10.sp, color = ObsidianBlack, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = SubtleDivider)
            Spacer(modifier = Modifier.height(10.dp))

            // Security note
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Security,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "256-bit Encrypted SSL • SMS / Email OTP 2-Factor Protected",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        }
    }
}

// =========================================================================
// COMPONENT: LOGGED IN VIP CUSTOMER CARD
// =========================================================================
@Composable
private fun CustomerProfileCard(
    customer: Customer,
    onLogoutClick: () -> Unit,
    onEditProfile: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, GrayBorder),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().testTag("customer_profile_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(ObsidianBlack),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = customer.fullName.take(1).uppercase(),
                            color = PureWhite,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                customer.fullName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ObsidianBlack
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(ObsidianBlack, RoundedCornerShape(2.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "VIP",
                                    color = PureWhite,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                        Text(
                            customer.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Member ID: #UST-${customer.id.toString().padStart(5, '0')}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }

                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.testTag("account_logout_button")
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Sign Out",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = SubtleDivider)
            Spacer(modifier = Modifier.height(10.dp))

            // Member Tier Perks Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LiteAshBg, RoundedCornerShape(2.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Lifetime Complimentary Watch Servicing & Appraisal",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = ObsidianBlack,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// =========================================================================
// COMPONENT: ORDER HISTORY STUB & RECENT ORDERS PREVIEW
// =========================================================================
@Composable
private fun OrderHistoryStubSection(
    orders: List<Order>,
    onViewAllOrders: () -> Unit,
    onExploreWatches: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, GrayBorder),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().testTag("order_history_section")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.ReceiptLong,
                        contentDescription = null,
                        tint = ObsidianBlack,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "ORDER HISTORY & SHIPMENTS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = ObsidianBlack
                    )
                }

                TextButton(
                    onClick = onViewAllOrders,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    modifier = Modifier.testTag("view_all_orders_button")
                ) {
                    Text(
                        "View All (${orders.size})",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ObsidianBlack
                    )
                }
            }

            HorizontalDivider(color = SubtleDivider, modifier = Modifier.padding(vertical = 10.dp))

            if (orders.isEmpty()) {
                // Empty Orders State
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.ShoppingBag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No Previous Orders Found",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = ObsidianBlack
                    )
                    Text(
                        "Your placed orders and tracking history will appear here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onExploreWatches,
                        shape = RoundedCornerShape(2.dp),
                        border = BorderStroke(1.dp, ObsidianBlack),
                        modifier = Modifier.testTag("explore_collection_empty_button")
                    ) {
                        Text("EXPLORE COLLECTION", color = ObsidianBlack, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            } else {
                // List the 2 most recent orders with full details
                orders.take(2).forEachIndexed { index, order ->
                    RecentOrderItemStub(order = order, onClick = onViewAllOrders)
                    if (index < orders.take(2).lastIndex) {
                        HorizontalDivider(color = SubtleDivider, modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentOrderItemStub(
    order: Order,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Order #${order.id}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = ObsidianBlack
                )
                Spacer(modifier = Modifier.width(8.dp))
                OrderStatusChip(status = order.status)
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${order.lineItems.size} Item(s) • Total: $${order.total}",
                style = MaterialTheme.typography.bodySmall,
                color = ObsidianBlack,
                fontWeight = FontWeight.SemiBold
            )

            if (!order.trackingNumber.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                    Icon(
                        Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tracking: ${order.trackingNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ForestGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun OrderStatusChip(status: String) {
    val (bgColor, textColor, label) = when (status.lowercase()) {
        "completed", "delivered" -> Triple(ForestGreen.copy(alpha = 0.12f), ForestGreen, "DELIVERED")
        "processing" -> Triple(ObsidianBlack.copy(alpha = 0.08f), ObsidianBlack, "PROCESSING")
        "shipped", "in transit" -> Triple(Color(0xFF1E88E5).copy(alpha = 0.12f), Color(0xFF1565C0), "IN TRANSIT")
        else -> Triple(LiteAshBg, ObsidianBlack, status.uppercase())
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(2.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

// =========================================================================
// COMPONENT: SAVED ADDRESSES STUB
// =========================================================================
@Composable
private fun SavedAddressesStubSection(
    customer: Customer?,
    onEditShipping: () -> Unit,
    onEditBilling: () -> Unit,
    onAddNewAddress: () -> Unit
) {
    val shipping = customer?.shipping ?: Address(
        firstName = "Alexander",
        lastName = "Wright",
        address1 = "742 Evergreen Terrace",
        city = "Beverly Hills",
        state = "CA",
        postcode = "90210",
        country = "United States",
        phone = "+1 (555) 392-1084"
    )

    val billing = customer?.billing ?: Address(
        firstName = "Alexander",
        lastName = "Wright",
        address1 = "742 Evergreen Terrace",
        city = "Beverly Hills",
        state = "CA",
        postcode = "90210",
        country = "United States",
        email = "alexander.w@example.com",
        phone = "+1 (555) 392-1084"
    )

    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, GrayBorder),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().testTag("saved_addresses_section")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = ObsidianBlack,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "SAVED ADDRESSES",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = ObsidianBlack
                    )
                }

                TextButton(
                    onClick = onAddNewAddress,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    modifier = Modifier.testTag("add_address_button")
                ) {
                    Text(
                        "+ Add New",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ObsidianBlack
                    )
                }
            }

            HorizontalDivider(color = SubtleDivider, modifier = Modifier.padding(vertical = 10.dp))

            // Primary Shipping Address
            AddressCardItem(
                tag = "DEFAULT SHIPPING",
                address = shipping,
                onEdit = onEditShipping,
                testTag = "shipping_address_card"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Primary Billing Address
            AddressCardItem(
                tag = "DEFAULT BILLING",
                address = billing,
                onEdit = onEditBilling,
                testTag = "billing_address_card"
            )
        }
    }
}

@Composable
private fun AddressCardItem(
    tag: String,
    address: Address,
    onEdit: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(LiteAshBg, RoundedCornerShape(2.dp))
            .border(0.8.dp, GrayBorder, RoundedCornerShape(2.dp))
            .padding(12.dp)
            .testTag(testTag)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(ObsidianBlack, RoundedCornerShape(2.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = tag,
                        color = PureWhite,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(24.dp).testTag("edit_address_${tag.lowercase().replace(" ", "_")}")
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Address",
                        tint = ObsidianBlack,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            val recipientName = listOf(address.firstName, address.lastName).filter { it.isNotBlank() }.joinToString(" ").ifBlank { "Alexander Wright" }
            Text(
                text = recipientName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = ObsidianBlack
            )

            val street = address.address1.ifBlank { "742 Evergreen Terrace" }
            val cityStateZip = listOf(
                address.city.ifBlank { "Beverly Hills" },
                address.state.ifBlank { "CA" },
                address.postcode.ifBlank { "90210" }
            ).filter { it.isNotBlank() }.joinToString(", ")

            Text(street, style = MaterialTheme.typography.bodySmall, color = ObsidianBlack)
            Text(cityStateZip, style = MaterialTheme.typography.bodySmall, color = ObsidianBlack)
            Text(address.country.ifBlank { "United States" }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (address.phone.isNotBlank()) {
                Text("Tel: ${address.phone}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}

// =========================================================================
// COMPONENT: ADDRESS EDIT DIALOG
// =========================================================================
@Composable
private fun AddressEditDialog(
    uiState: com.example.ui.viewmodel.AccountUiState,
    onDismiss: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onCompanyChange: (String) -> Unit,
    onLine1Change: (String) -> Unit,
    onLine2Change: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onStateChange: (String) -> Unit,
    onPostcodeChange: (String) -> Unit,
    onCountryChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(2.dp),
        title = {
            Text(
                "Edit ${uiState.editingAddressType.replaceFirstChar { it.uppercase() }} Address",
                fontWeight = FontWeight.Bold,
                color = ObsidianBlack,
                letterSpacing = 0.5.sp
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = uiState.addressFirstName,
                            onValueChange = onFirstNameChange,
                            label = { Text("First Name") },
                            singleLine = true,
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.weight(1f).testTag("address_dialog_first_name")
                        )
                        OutlinedTextField(
                            value = uiState.addressLastName,
                            onValueChange = onLastNameChange,
                            label = { Text("Last Name") },
                            singleLine = true,
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.weight(1f).testTag("address_dialog_last_name")
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = uiState.addressLine1,
                        onValueChange = onLine1Change,
                        label = { Text("Street Address") },
                        singleLine = true,
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.fillMaxWidth().testTag("address_dialog_line1")
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = uiState.addressCity,
                            onValueChange = onCityChange,
                            label = { Text("City") },
                            singleLine = true,
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.weight(1f).testTag("address_dialog_city")
                        )
                        OutlinedTextField(
                            value = uiState.addressState,
                            onValueChange = onStateChange,
                            label = { Text("State") },
                            singleLine = true,
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.weight(1f).testTag("address_dialog_state")
                        )
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = uiState.addressPostcode,
                            onValueChange = onPostcodeChange,
                            label = { Text("Postal Code / ZIP") },
                            singleLine = true,
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.weight(1f).testTag("address_dialog_postcode")
                        )
                        OutlinedTextField(
                            value = uiState.addressCountry,
                            onValueChange = onCountryChange,
                            label = { Text("Country") },
                            singleLine = true,
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.weight(1f).testTag("address_dialog_country")
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = uiState.addressPhone,
                        onValueChange = onPhoneChange,
                        label = { Text("Phone Number") },
                        singleLine = true,
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.fillMaxWidth().testTag("address_dialog_phone")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = ObsidianBlack, contentColor = PureWhite),
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier.testTag("save_address_dialog_button")
            ) {
                Text("SAVE ADDRESS", fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = ObsidianBlack, fontWeight = FontWeight.Bold)
            }
        }
    )
}

// =========================================================================
// HELPER COMPONENTS
// =========================================================================
@Composable
private fun AccountSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
    )
}

@Composable
private fun AccountMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = ObsidianBlack, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = ObsidianBlack
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}
