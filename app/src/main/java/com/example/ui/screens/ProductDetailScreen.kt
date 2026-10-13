package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Product
import com.example.data.model.ProductReview
import com.example.ui.components.ErrorRetryView
import com.example.ui.components.QuantityStepper
import com.example.ui.components.WatchDetailSkeleton
import com.example.ui.components.WatchLoupeInspectionDialog
import com.example.ui.components.WatchProductCard
import com.example.ui.components.WatchRatingBar
import com.example.ui.components.ZoomableWatchImage
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LuxuryRed
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.ProductDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToProduct: (Long) -> Unit,
    onBuyNowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val isInWishlist by viewModel.isWishlist.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showFullscreenLoupe by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isAddedToCartSuccess) {
        if (uiState.isAddedToCartSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar("Added to your shopping bag")
                viewModel.resetCartSuccess()
            }
        }
    }

    Scaffold(
        containerColor = com.example.ui.theme.LiteAshWhite,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("product_detail_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = ObsidianBlack
                            )
                        }
                    },
                    actions = {
                        // Share Watch
                        IconButton(
                            onClick = {
                                val p = uiState.product ?: return@IconButton
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, p.name)
                                    putExtra(Intent.EXTRA_TEXT, "Check out the ${p.name} on U Store: ${p.permalink.ifBlank { "https://ustore4u.com/" }}")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Timepiece"))
                            },
                            modifier = Modifier.testTag("share_product_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = ObsidianBlack)
                        }

                        // Cart
                        IconButton(
                            onClick = onNavigateToCart,
                            modifier = Modifier.testTag("product_detail_cart_button")
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
                                Icon(Icons.Outlined.ShoppingBag, contentDescription = "Cart", tint = ObsidianBlack)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PureWhite,
                        scrolledContainerColor = PureWhite,
                        navigationIconContentColor = ObsidianBlack,
                        actionIconContentColor = ObsidianBlack
                    )
                )
                HorizontalDivider(thickness = 0.8.dp, color = com.example.ui.theme.SubtleDivider)
            }
        },
        bottomBar = {
            if (uiState.product != null) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(thickness = 0.8.dp, color = com.example.ui.theme.SubtleDivider)
                    Surface(
                        color = PureWhite,
                        tonalElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Wishlist Icon Button
                            OutlinedButton(
                                onClick = { viewModel.toggleWishlist() },
                                shape = RoundedCornerShape(2.dp),
                                border = BorderStroke(1.dp, com.example.ui.theme.GrayBorder),
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("detail_wishlist_button"),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Wishlist",
                                    tint = if (isInWishlist) LuxuryRed else ObsidianBlack
                                )
                            }

                            // Add to Bag Button
                            OutlinedButton(
                                onClick = { viewModel.addToCart() },
                                shape = RoundedCornerShape(2.dp),
                                border = BorderStroke(1.dp, ObsidianBlack),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("add_to_cart_button")
                            ) {
                                Text(
                                    text = "ADD TO BAG",
                                    color = ObsidianBlack,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    fontSize = 12.sp
                                )
                            }

                            // Buy Now Button
                            Button(
                                onClick = {
                                    viewModel.addToCart(onSuccess = onBuyNowClick)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ObsidianBlack,
                                    contentColor = PureWhite
                                ),
                                shape = RoundedCornerShape(2.dp),
                                modifier = Modifier
                                    .weight(1.1f)
                                    .height(48.dp)
                                    .testTag("buy_now_button")
                            ) {
                                Text(
                                    text = "BUY NOW",
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier.testTag("product_detail_screen")
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                WatchDetailSkeleton(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            uiState.errorMessage != null && uiState.product == null -> {
                ErrorRetryView(
                    message = uiState.errorMessage ?: "Product not available.",
                    onRetry = { viewModel.loadProduct() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            uiState.product != null -> {
                val product = uiState.product!!
                val currentImage = product.images.getOrNull(uiState.selectedImageIndex)?.src ?: product.primaryImageUrl

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // 1. High-Resolution Gesture-Based Zoom Gallery
                    item {
                        Column(modifier = Modifier.fillMaxWidth().background(PureWhite)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .background(com.example.ui.theme.LiteAshBg)
                            ) {
                                ZoomableWatchImage(
                                    imageUrl = currentImage,
                                    contentDescription = product.name,
                                    onOpenFullscreenLoupe = { showFullscreenLoupe = true },
                                    modifier = Modifier.fillMaxSize()
                                )

                                if (product.onSale && product.discountPercentage > 0) {
                                    Surface(
                                        color = ObsidianBlack,
                                        shape = RoundedCornerShape(bottomEnd = 2.dp),
                                        modifier = Modifier.align(Alignment.TopStart)
                                    ) {
                                        Text(
                                            text = "SAVE ${product.discountPercentage}%",
                                            color = PureWhite,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.6.sp,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            // Thumbnails Row
                            if (product.images.size > 1) {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    itemsIndexed(
                                        items = product.images,
                                        key = { index, img -> "${img.id}_$index" }
                                    ) { index, img ->
                                        val isSelected = index == uiState.selectedImageIndex
                                        Box(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) ObsidianBlack else com.example.ui.theme.GrayBorder,
                                                    shape = RoundedCornerShape(2.dp)
                                                )
                                                .clickable { viewModel.selectImage(index) }
                                        ) {
                                            AsyncImage(
                                                model = img.src,
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 2. Title, Brand & Price
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PureWhite)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = product.brand.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = ObsidianBlack,
                                letterSpacing = 2.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = ObsidianBlack
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Rating & SKU Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val r = product.averageRating.toFloatOrNull() ?: 5.0f
                                WatchRatingBar(rating = r, ratingCount = product.ratingCount, starSize = 16.dp)

                                Text(
                                    text = "SKU: ${product.sku.ifBlank { "U4U-HOR-${product.id}" }}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Price Section
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = product.formattedPrice,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ObsidianBlack
                                )

                                if (product.onSale && product.regularPrice.isNotBlank()) {
                                    Text(
                                        text = product.formattedRegularPrice,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                }

                                Surface(
                                    color = if (product.inStock) ForestGreen.copy(alpha = 0.12f) else LuxuryRed.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(2.dp)
                                ) {
                                    Text(
                                        text = if (product.inStock) "In Stock" else "Out of Stock",
                                        color = if (product.inStock) ForestGreen else LuxuryRed,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Local Kukatpally Express Delivery & Policy Notice
                            Card(
                                shape = RoundedCornerShape(2.dp),
                                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.LiteAshBg),
                                border = BorderStroke(1.dp, com.example.ui.theme.GrayBorder)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.FlashOn,
                                            contentDescription = null,
                                            tint = ForestGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "2-Hour Express Delivery in Kukatpally (10km)",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = ForestGreen
                                        )
                                    }
                                    Text(
                                        text = "• 100% Genuine Brand Warranty (Titan, Fastrack, Sonata, Ajanta)\n• No Return • Only Exchange policy within 7 days",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ObsidianBlack
                                    )
                                }
                            }
                        }
                    }

                    // 3. Product Attributes / Variations (e.g. Straps, Dial Colors)
                    if (product.attributes.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(PureWhite)
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp), color = com.example.ui.theme.SubtleDivider)

                                product.attributes.forEach { attr ->
                                    val selectedOption = uiState.selectedVariations[attr.name] ?: attr.options.firstOrNull() ?: ""
                                    Text(
                                        text = "${attr.name}: $selectedOption",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = ObsidianBlack
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                                    ) {
                                        attr.options.forEach { option ->
                                            val isSelected = selectedOption == option
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = { viewModel.selectVariation(attr.name, option) },
                                                label = { Text(option) },
                                                shape = RoundedCornerShape(2.dp),
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = ObsidianBlack,
                                                    selectedLabelColor = PureWhite,
                                                    containerColor = com.example.ui.theme.LiteAshBg,
                                                    labelColor = ObsidianBlack
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 4. Quantity Stepper
                    item {
                        Surface(
                            color = PureWhite,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "QUANTITY",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = ObsidianBlack
                                )

                                QuantityStepper(
                                    quantity = uiState.quantity,
                                    onQuantityChange = { viewModel.setQuantity(it) }
                                )
                            }
                        }
                    }

                    // 5. Technical Horological Specs Table
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "TECHNICAL SPECIFICATIONS",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = ObsidianBlack
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Card(
                                shape = RoundedCornerShape(2.dp),
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = BorderStroke(1.dp, com.example.ui.theme.GrayBorder)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    SpecRow("Movement Calibre", product.movement)
                                    SpecRow("Case Diameter", product.caseDiameter)
                                    SpecRow("Water Resistance", product.waterResistance)
                                    SpecRow("Glass Crystal", product.glassType)
                                    SpecRow("Strap / Bracelet", product.strapMaterial)
                                    SpecRow("Warranty", product.warranty)
                                }
                            }
                        }
                    }

                    // 6. Detailed Description
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "DESCRIPTION & HERITAGE",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = ObsidianBlack
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            val rawDesc = if (product.description.isNotBlank()) product.description else product.shortDescription
                            val cleanedText = remember(rawDesc) {
                                HtmlCompat.fromHtml(rawDesc, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().trim()
                            }

                            Text(
                                text = cleanedText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ObsidianBlack,
                                lineHeight = 24.sp
                            )
                        }
                    }

                    // 7. Customer Reviews Section
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "CUSTOMER REVIEWS (${uiState.reviews.size})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = ObsidianBlack
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (uiState.reviews.isEmpty()) {
                                Text(
                                    text = "No reviews yet. Be the first to review this timepiece!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                uiState.reviews.forEach { rev ->
                                    Card(
                                        shape = RoundedCornerShape(2.dp),
                                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                                        border = BorderStroke(1.dp, com.example.ui.theme.GrayBorder),
                                        elevation = CardDefaults.cardElevation(0.dp),
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(rev.reviewer, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                                                    if (rev.verified) {
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Icon(
                                                            imageVector = Icons.Outlined.Verified,
                                                            contentDescription = "Verified Buyer",
                                                            tint = ObsidianBlack,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                                WatchRatingBar(rating = rev.rating.toFloat(), starSize = 12.dp)
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = rev.review,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = ObsidianBlack
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 8. Related Timepieces Carousel
                    if (uiState.relatedProducts.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                                Text(
                                    text = "YOU MAY ALSO LIKE",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = ObsidianBlack,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(uiState.relatedProducts, key = { it.id }) { related ->
                                        WatchProductCard(
                                            product = related,
                                            isInWishlist = false,
                                            onProductClick = { onNavigateToProduct(related.id) },
                                            onWishlistToggle = { },
                                            onQuickAddToCart = { },
                                            modifier = Modifier.width(180.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Fullscreen Horology Loupe Inspection Dialog
        if (showFullscreenLoupe && uiState.product != null) {
            val product = uiState.product!!
            WatchLoupeInspectionDialog(
                productName = product.name,
                brand = product.brand,
                images = product.images,
                selectedIndex = uiState.selectedImageIndex,
                onSelectImage = { index -> viewModel.selectImage(index) },
                onDismiss = { showFullscreenLoupe = false }
            )
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
