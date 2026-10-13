package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.api.WooCommerceConfig
import com.example.data.model.ProductCategory
import com.example.data.repository.SortOption
import com.example.ui.components.ErrorRetryView
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.UStore4UTopBar
import com.example.ui.components.WatchCarousel
import com.example.ui.components.WatchProductCard
import com.example.ui.components.WestsideHomeFeedSkeleton
import com.example.ui.theme.GrayBorder
import com.example.ui.theme.LiteAshBg
import com.example.ui.theme.LiteAshWhite
import com.example.ui.theme.LuxuryRed
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftGrayBg
import com.example.ui.theme.SubtleDivider
import com.example.ui.theme.WestsideOliveBanner
import com.example.ui.theme.WestsideSpecialPriceBg
import com.example.ui.theme.WestsideSpecialPriceMagenta
import com.example.ui.theme.WestsideTerracotta
import com.example.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToProduct: (Long) -> Unit,
    onNavigateToCategory: (Long, String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToAllProducts: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val wishlistIds by viewModel.wishlistProductIds.collectAsState()
    val recentlyViewed by viewModel.recentlyViewed.collectAsState()
    val context = LocalContext.current

    var selectedEditorialTab by remember { mutableStateOf("Festive") }

    if (uiState.isFilterSheetVisible) {
        FilterBottomSheet(
            onDismissRequest = { viewModel.setFilterSheetVisible(false) },
            categories = uiState.categories,
            minPrice = uiState.minPrice,
            maxPrice = uiState.maxPrice,
            currentSort = uiState.selectedSort,
            onApplyFilters = { _, _, min, max, _, _, sort ->
                viewModel.setPriceRange(min, max, sort)
            }
        )
    }

    Scaffold(
        topBar = {
            UStore4UTopBar(
                cartItemCount = cartCount,
                onCartClick = onNavigateToCart,
                onSearchClick = onNavigateToSearch,
                onWishlistClick = { onNavigateToAllProducts("wishlist") },
                onAccountClick = onNavigateToAccount
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("home_screen")
    ) { innerPadding ->
        val refreshState = rememberPullToRefreshState()

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.loadHomeData(isRefresh = true) },
            state = refreshState,
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = refreshState,
                    isRefreshing = uiState.isRefreshing,
                    containerColor = MaterialTheme.colorScheme.surface,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading && !uiState.isRefreshing -> {
                    WestsideHomeFeedSkeleton()
                }
                uiState.errorMessage != null && uiState.featuredProducts.isEmpty() -> {
                    ErrorRetryView(
                        message = uiState.errorMessage ?: "Failed to load watch store.",
                        onRetry = { viewModel.loadHomeData() }
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        contentPadding = PaddingValues(bottom = 48.dp)
                    ) {
                        // 1. Editorial Serif Category Tabs (Festive, Women, Men, Kids, Wall Clocks)
                        item {
                            WestsideEditorialCategoryTabs(
                                selectedTab = selectedEditorialTab,
                                onTabSelect = { tab ->
                                    selectedEditorialTab = tab
                                    when (tab) {
                                        "Women", "Woman" -> onNavigateToCategory(102L, "Women's Watches")
                                        "Men", "Man" -> onNavigateToCategory(101L, "Men's Watches")
                                        "Kids" -> onNavigateToCategory(108L, "Kids & Youth Watches")
                                        "Wall Clocks" -> onNavigateToCategory(103L, "Wall Clocks")
                                        else -> onNavigateToAllProducts("all")
                                    }
                                }
                            )
                        }

                        // 2. Westside Sort & Price Filter Bar
                        item {
                            WestsideFeedSortFilterBar(
                                selectedSort = uiState.selectedSort,
                                selectedPricePreset = uiState.selectedPricePreset,
                                minPrice = uiState.minPrice,
                                maxPrice = uiState.maxPrice,
                                isFilterActive = uiState.isFilterOrSortActive,
                                onOpenFilterSheet = { viewModel.setFilterSheetVisible(true) },
                                onSelectSort = { viewModel.setSortOption(it) },
                                onSelectPricePreset = { preset, min, max -> viewModel.setPricePreset(preset, min, max) },
                                onResetFilters = { viewModel.resetFilters() }
                            )
                        }

                        if (uiState.isFilterOrSortActive) {
                            // Active Filter Status Header
                            item {
                                WestsideActiveFilterBanner(
                                    itemCount = uiState.filteredProducts.size,
                                    sortOption = uiState.selectedSort,
                                    selectedPricePreset = uiState.selectedPricePreset,
                                    minPrice = uiState.minPrice,
                                    maxPrice = uiState.maxPrice,
                                    onReset = { viewModel.resetFilters() }
                                )
                            }

                            if (uiState.filteredProducts.isEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 48.dp, horizontal = 24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "NO WATCHES FOUND IN THIS PRICE RANGE",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            textAlign = TextAlign.Center,
                                            color = ObsidianBlack
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Try adjusting your price filters or sorting options.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))
                                        Button(
                                            onClick = { viewModel.resetFilters() },
                                            colors = ButtonDefaults.buttonColors(containerColor = ObsidianBlack, contentColor = PureWhite),
                                            shape = RoundedCornerShape(2.dp),
                                            modifier = Modifier.testTag("empty_state_reset_button")
                                        ) {
                                            Text("RESET ALL FILTERS", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.8.sp)
                                        }
                                    }
                                }
                            } else {
                                // 2-Column Product Grid in Feed
                                val rows = uiState.filteredProducts.chunked(2)
                                items(
                                    items = rows,
                                    key = { row -> row.joinToString("-") { it.id.toString() } },
                                    contentType = { "filtered_watch_row" }
                                ) { row ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        val first = row[0]
                                        Box(modifier = Modifier.weight(1f)) {
                                            WatchProductCard(
                                                product = first,
                                                isInWishlist = wishlistIds.contains(first.id),
                                                onProductClick = { onNavigateToProduct(first.id) },
                                                onWishlistToggle = { viewModel.toggleWishlist(first) },
                                                onQuickAddToCart = { viewModel.quickAddToCart(first) }
                                            )
                                        }
                                        if (row.size > 1) {
                                            val second = row[1]
                                            Box(modifier = Modifier.weight(1f)) {
                                                WatchProductCard(
                                                    product = second,
                                                    isInWishlist = wishlistIds.contains(second.id),
                                                    onProductClick = { onNavigateToProduct(second.id) },
                                                    onWishlistToggle = { viewModel.toggleWishlist(second) },
                                                    onQuickAddToCart = { viewModel.quickAddToCart(second) }
                                                )
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }

                            // 2-Hour Express Delivery Bar
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                WestsideDeliveryStrip(
                                    onCallSupport = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${WooCommerceConfig.CONTACT_PHONE_RAW}"))
                                        context.startActivity(intent)
                                    }
                                )
                            }

                            // WestStyleClub Banner
                            item {
                                WestsideStyleClubBanner(
                                    onJoinClick = onNavigateToAccount
                                )
                            }
                        } else {
                            // Standard Editorial Layout when no filters active
                            // 3. Tall Visual Lookbook Cards Row (WOMAN, MAN, SPECIAL PRICES, SHOP KIDS / CLOCKS)
                            item {
                                WestsideVisualLookbookRow(
                                    onWomanClick = { onNavigateToCategory(102L, "Women's Watches") },
                                    onManClick = { onNavigateToCategory(101L, "Men's Watches") },
                                    onSpecialPricesClick = { onNavigateToAllProducts("deals") },
                                    onClocksClick = { onNavigateToCategory(103L, "Wall Clocks") }
                                )
                            }

                            // 4. Westside Expandable Accordion Menu Sections
                            item {
                                WestsideAccordionMenu(
                                    onNavigateToCategory = onNavigateToCategory,
                                    onNavigateToAll = { onNavigateToAllProducts("all") },
                                    onNavigateToDeals = { onNavigateToAllProducts("deals") }
                                )
                            }

                            // 5. WestStyleClub / UStyleClub Editorial Olive Taupe Banner
                            item {
                                WestsideStyleClubBanner(
                                    onJoinClick = onNavigateToAccount
                                )
                            }

                            // 6. Westside Footer Links
                            item {
                                WestsideNavTextLinks(
                                    onAboutClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(WooCommerceConfig.DEFAULT_STORE_URL))
                                        context.startActivity(intent)
                                    },
                                    onStoreLocatorClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:17.4849,78.4138?q=Kukatpally,Hyderabad"))
                                        context.startActivity(intent)
                                    },
                                    onCustomerCareClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${WooCommerceConfig.CONTACT_PHONE_RAW}"))
                                        context.startActivity(intent)
                                    }
                                )
                            }

                            // 7. 2-Hour Express Delivery Bar
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                WestsideDeliveryStrip(
                                    onCallSupport = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${WooCommerceConfig.CONTACT_PHONE_RAW}"))
                                        context.startActivity(intent)
                                    }
                                )
                            }

                            // 8. Trending Now Carousel
                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                                WatchCarousel(
                                    title = "TRENDING NOW",
                                    subtitle = "Most coveted timepieces & bestselling clocks",
                                    products = uiState.bestSellers,
                                    wishlistProductIds = wishlistIds,
                                    onProductClick = onNavigateToProduct,
                                    onWishlistToggle = { viewModel.toggleWishlist(it) },
                                    onQuickAddToCart = { viewModel.quickAddToCart(it) },
                                    onViewAllClick = { onNavigateToAllProducts("bestsellers") }
                                )
                            }

                            // 9. New In Store Carousel
                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                                WatchCarousel(
                                    title = "NEW IN STORE",
                                    subtitle = "Fresh drops from Titan, Fastrack & Sonata",
                                    products = uiState.newArrivals,
                                    wishlistProductIds = wishlistIds,
                                    onProductClick = onNavigateToProduct,
                                    onWishlistToggle = { viewModel.toggleWishlist(it) },
                                    onQuickAddToCart = { viewModel.quickAddToCart(it) },
                                    onViewAllClick = { onNavigateToAllProducts("new") }
                                )
                            }

                            // 10. Curated For You
                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                                WatchCarousel(
                                    title = "CURATED FOR YOU",
                                    subtitle = "Handpicked formal, casual & home decor essentials",
                                    products = uiState.featuredProducts,
                                    wishlistProductIds = wishlistIds,
                                    onProductClick = onNavigateToProduct,
                                    onWishlistToggle = { viewModel.toggleWishlist(it) },
                                    onQuickAddToCart = { viewModel.quickAddToCart(it) },
                                    onViewAllClick = { onNavigateToAllProducts("featured") }
                                )
                            }

                            // 11. Recently Viewed
                            if (recentlyViewed.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "RECENTLY VIEWED",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.2.sp,
                                            color = ObsidianBlack,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        LazyRow(
                                            contentPadding = PaddingValues(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            items(
                                                items = recentlyViewed,
                                                key = { it.productId },
                                                contentType = { "recently_viewed" }
                                            ) { item ->
                                                Card(
                                                    shape = RoundedCornerShape(2.dp),
                                                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                                                    border = BorderStroke(1.dp, GrayBorder),
                                                    elevation = CardDefaults.cardElevation(0.dp),
                                                    modifier = Modifier
                                                        .width(135.dp)
                                                        .clickable { onNavigateToProduct(item.productId) }
                                                ) {
                                                    Column(modifier = Modifier.padding(8.dp)) {
                                                        AsyncImage(
                                                            model = item.imageUrl,
                                                            contentDescription = item.name,
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .aspectRatio(1f)
                                                                .clip(RoundedCornerShape(2.dp))
                                                        )
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                        Text(
                                                            text = item.name,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontWeight = FontWeight.Medium,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            color = ObsidianBlack
                                                        )
                                                        val priceFormatted = if (item.price % 1.0 == 0.0) {
                                                            "₹%,d".format(item.price.toLong())
                                                        } else {
                                                            "₹%,.2f".format(item.price)
                                                        }
                                                        Text(
                                                            text = priceFormatted,
                                                            style = MaterialTheme.typography.labelMedium,
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

                            // 12. Kukatpally Store Contact & Guarantees
                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                                WestsideTrustAndStoreFooter(
                                    onCallStore = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${WooCommerceConfig.CONTACT_PHONE_RAW}"))
                                        context.startActivity(intent)
                                    },
                                    onStoreDirections = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:17.4849,78.4138?q=Kukatpally,Hyderabad"))
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Westside Feed Sort & Filter Bar
 * Displays direct one-tap sort options (Price: Low to High, Price: High to Low)
 * and price range chips (Under ₹1,500, ₹1,500 - ₹3,000, etc.) + Filters sheet trigger
 */
@Composable
private fun WestsideFeedSortFilterBar(
    selectedSort: SortOption,
    selectedPricePreset: String?,
    minPrice: Double?,
    maxPrice: Double?,
    isFilterActive: Boolean,
    onOpenFilterSheet: () -> Unit,
    onSelectSort: (SortOption) -> Unit,
    onSelectPricePreset: (String?, Double?, Double?) -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 1. All Filters & Sort Sheet Button
            item(key = "filter_button") {
                Surface(
                    onClick = onOpenFilterSheet,
                    shape = RoundedCornerShape(2.dp),
                    color = if (isFilterActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (isFilterActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                    modifier = Modifier.testTag("feed_filter_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Tune,
                            contentDescription = "Filters & Sort",
                            tint = if (isFilterActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "FILTERS & SORT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = if (isFilterActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                        if (isFilterActive) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(LuxuryRed)
                            )
                        }
                    }
                }
            }

            // 2. Price: Low to High
            item(key = "sort_price_asc") {
                val isLowHigh = selectedSort == SortOption.PRICE_ASC
                FilterChip(
                    selected = isLowHigh,
                    onClick = {
                        onSelectSort(if (isLowHigh) SortOption.LATEST else SortOption.PRICE_ASC)
                    },
                    label = {
                        Text(
                            "PRICE: LOW → HIGH",
                            fontSize = 11.sp,
                            fontWeight = if (isLowHigh) FontWeight.Bold else FontWeight.Normal,
                            letterSpacing = 0.5.sp
                        )
                    },
                    shape = RoundedCornerShape(2.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isLowHigh,
                        borderColor = MaterialTheme.colorScheme.outline,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        borderWidth = 1.dp
                    ),
                    modifier = Modifier.testTag("sort_price_asc_chip")
                )
            }

            // 3. Price: High to Low
            item(key = "sort_price_desc") {
                val isHighLow = selectedSort == SortOption.PRICE_DESC
                FilterChip(
                    selected = isHighLow,
                    onClick = {
                        onSelectSort(if (isHighLow) SortOption.LATEST else SortOption.PRICE_DESC)
                    },
                    label = {
                        Text(
                            "PRICE: HIGH → LOW",
                            fontSize = 11.sp,
                            fontWeight = if (isHighLow) FontWeight.Bold else FontWeight.Normal,
                            letterSpacing = 0.5.sp
                        )
                    },
                    shape = RoundedCornerShape(2.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isHighLow,
                        borderColor = MaterialTheme.colorScheme.outline,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        borderWidth = 1.dp
                    ),
                    modifier = Modifier.testTag("sort_price_desc_chip")
                )
            }

            // 4. Under ₹1,500
            item(key = "preset_under_1500") {
                val isSelected = selectedPricePreset == "UNDER_1500" || (minPrice == 0.0 && maxPrice == 1500.0)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) {
                            onSelectPricePreset(null, null, null)
                        } else {
                            onSelectPricePreset("UNDER_1500", 0.0, 1500.0)
                        }
                    },
                    label = {
                        Text(
                            "UNDER ₹1,500",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            letterSpacing = 0.5.sp
                        )
                    },
                    shape = RoundedCornerShape(2.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outline,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        borderWidth = 1.dp
                    ),
                    modifier = Modifier.testTag("filter_under_1500_chip")
                )
            }

            // 5. ₹1,500 - ₹3,000
            item(key = "preset_1500_3000") {
                val isSelected = selectedPricePreset == "1500_3000" || (minPrice == 1500.0 && maxPrice == 3000.0)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) {
                            onSelectPricePreset(null, null, null)
                        } else {
                            onSelectPricePreset("1500_3000", 1500.0, 3000.0)
                        }
                    },
                    label = {
                        Text(
                            "₹1,500 - ₹3,000",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            letterSpacing = 0.5.sp
                        )
                    },
                    shape = RoundedCornerShape(2.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outline,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        borderWidth = 1.dp
                    ),
                    modifier = Modifier.testTag("filter_1500_3000_chip")
                )
            }

            // 6. ₹3,000 - ₹6,000
            item(key = "preset_3000_6000") {
                val isSelected = selectedPricePreset == "3000_6000" || (minPrice == 3000.0 && maxPrice == 6000.0)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) {
                            onSelectPricePreset(null, null, null)
                        } else {
                            onSelectPricePreset("3000_6000", 3000.0, 6000.0)
                        }
                    },
                    label = {
                        Text(
                            "₹3,000 - ₹6,000",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            letterSpacing = 0.5.sp
                        )
                    },
                    shape = RoundedCornerShape(2.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outline,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        borderWidth = 1.dp
                    ),
                    modifier = Modifier.testTag("filter_3000_6000_chip")
                )
            }

            // 7. Above ₹6,000
            item(key = "preset_above_6000") {
                val isSelected = selectedPricePreset == "ABOVE_6000" || (minPrice == 6000.0 && maxPrice == null)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) {
                            onSelectPricePreset(null, null, null)
                        } else {
                            onSelectPricePreset("ABOVE_6000", 6000.0, null)
                        }
                    },
                    label = {
                        Text(
                            "ABOVE ₹6,000",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            letterSpacing = 0.5.sp
                        )
                    },
                    shape = RoundedCornerShape(2.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outline,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        borderWidth = 1.dp
                    ),
                    modifier = Modifier.testTag("filter_above_6000_chip")
                )
            }
        }
    }
}

/**
 * Active Filter Summary Banner
 */
@Composable
private fun WestsideActiveFilterBanner(
    itemCount: Int,
    sortOption: SortOption,
    selectedPricePreset: String?,
    minPrice: Double?,
    maxPrice: Double?,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priceLabel = when {
        selectedPricePreset == "UNDER_1500" || (minPrice == 0.0 && maxPrice == 1500.0) -> "Under ₹1,500"
        selectedPricePreset == "1500_3000" || (minPrice == 1500.0 && maxPrice == 3000.0) -> "₹1,500 - ₹3,000"
        selectedPricePreset == "3000_6000" || (minPrice == 3000.0 && maxPrice == 6000.0) -> "₹3,000 - ₹6,000"
        selectedPricePreset == "ABOVE_6000" || (minPrice == 6000.0 && maxPrice == null) -> "Above ₹6,000"
        minPrice != null && maxPrice != null -> "₹${minPrice.toInt()} - ₹${maxPrice.toInt()}"
        minPrice != null -> "Above ₹${minPrice.toInt()}"
        maxPrice != null -> "Under ₹${maxPrice.toInt()}"
        else -> null
    }

    Surface(
        color = LiteAshWhite,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .border(1.dp, GrayBorder, RoundedCornerShape(2.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SHOWING $itemCount WATCHES",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = ObsidianBlack
                )
                val summaryParts = mutableListOf<String>()
                if (sortOption != SortOption.LATEST) {
                    summaryParts.add("Sort: ${sortOption.title}")
                }
                if (priceLabel != null) {
                    summaryParts.add("Price: $priceLabel")
                }
                if (summaryParts.isNotEmpty()) {
                    Text(
                        text = summaryParts.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            TextButton(
                onClick = onReset,
                modifier = Modifier.testTag("clear_active_filters_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Filters",
                        tint = LuxuryRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "CLEAR",
                        color = LuxuryRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

/**
 * 1. Editorial Serif Category Tabs (Festive, Women, Men, Kids, Wall Clocks)
 */
@Composable
private fun WestsideEditorialCategoryTabs(
    selectedTab: String,
    onTabSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = remember { listOf("Festive", "Women", "Men", "Kids", "Wall Clocks") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(PureWhite)
            .padding(vertical = 10.dp)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                items = tabs,
                key = { it }
            ) { tab ->
                val isSelected = tab.equals(selectedTab, ignoreCase = true)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelect(tab) }
                        .padding(horizontal = 2.dp, vertical = 4.dp)
                        .testTag("tab_${tab.lowercase().replace(" ", "_")}")
                ) {
                    Text(
                        text = tab,
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                        color = ObsidianBlack
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(ObsidianBlack)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        // Trailing arrow indicating horizontal scroll
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "More categories",
            tint = ObsidianBlack.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 6.dp)
                .size(20.dp)
        )
    }
}

/**
 * 2. Visual Lookbook Cards Row (WOMAN, MAN, SPECIAL PRICES, SHOP KIDS / CLOCKS)
 */
@Composable
private fun WestsideVisualLookbookRow(
    onWomanClick: () -> Unit,
    onManClick: () -> Unit,
    onSpecialPricesClick: () -> Unit,
    onClocksClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Card 1: WOMEN
        item(key = "lookbook_women") {
            Column(
                modifier = Modifier
                    .width(135.dp)
                    .clickable(onClick = onWomanClick)
                    .testTag("lookbook_women")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(0.dp))
                        .background(LiteAshBg)
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1508057198894-247b23fe5ade?w=400&auto=format&fit=crop&q=80",
                        contentDescription = "Women's Collection",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "WOMEN",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ObsidianBlack
                )
            }
        }

        // Card 2: MEN
        item(key = "lookbook_men") {
            Column(
                modifier = Modifier
                    .width(135.dp)
                    .clickable(onClick = onManClick)
                    .testTag("lookbook_men")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(0.dp))
                        .background(LiteAshBg)
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=400&auto=format&fit=crop&q=80",
                        contentDescription = "Men's Collection",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "MEN",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ObsidianBlack
                )
            }
        }

        // Card 3: SPECIAL PRICES (Exact Magenta Graphical Card from Westside screenshot)
        item(key = "lookbook_special_prices") {
            Column(
                modifier = Modifier
                    .width(135.dp)
                    .clickable(onClick = onSpecialPricesClick)
                    .testTag("lookbook_special_prices")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .border(1.dp, GrayBorder, RoundedCornerShape(0.dp))
                        .background(PureWhite)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "SPECIAL\nPRICES",
                            color = WestsideSpecialPriceMagenta,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            lineHeight = 20.sp,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Bottom organic wave/scrim in magenta
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(45.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        WestsideSpecialPriceMagenta.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "SHOP NOW",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ObsidianBlack
                )
            }
        }

        // Card 4: WALL CLOCKS / KIDS
        item(key = "lookbook_clocks") {
            Column(
                modifier = Modifier
                    .width(135.dp)
                    .clickable(onClick = onClocksClick)
                    .testTag("lookbook_clocks")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(0.dp))
                        .background(LiteAshBg)
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1563861826100-9cb868fdbe1c?w=400&auto=format&fit=crop&q=80",
                        contentDescription = "Wall Clocks",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "WALL CLOCKS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ObsidianBlack
                )
            }
        }
    }
}

/**
 * 3. Interactive Accordion Menu (FESTIVE EDIT, WOMEN, MEN, KIDS, WALL CLOCKS)
 */
@Composable
private fun WestsideAccordionMenu(
    onNavigateToCategory: (Long, String) -> Unit,
    onNavigateToAll: () -> Unit,
    onNavigateToDeals: () -> Unit,
    modifier: Modifier = Modifier
) {
    var festiveExpanded by remember { mutableStateOf(true) }
    var womenExpanded by remember { mutableStateOf(false) }
    var menExpanded by remember { mutableStateOf(false) }
    var kidsExpanded by remember { mutableStateOf(false) }
    var wallClocksExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // --- 1. FESTIVE EDIT (Expanded by default with "VIEW ALL") ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { festiveExpanded = !festiveExpanded }
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FESTIVE EDIT",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp,
                color = ObsidianBlack
            )
            Icon(
                imageVector = if (festiveExpanded) Icons.Default.Remove else Icons.Default.Add,
                contentDescription = null,
                tint = ObsidianBlack,
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(
            visible = festiveExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "VIEW ALL",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = ObsidianBlack,
                    modifier = Modifier
                        .clickable(onClick = onNavigateToAll)
                        .padding(8.dp)
                )
            }
        }

        // --- 2. WOMEN (+) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { womenExpanded = !womenExpanded }
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WOMEN",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp,
                color = ObsidianBlack
            )
            Icon(
                imageVector = if (womenExpanded) Icons.Default.Remove else Icons.Default.Add,
                contentDescription = null,
                tint = ObsidianBlack,
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(
            visible = womenExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Titan Raga Collection",
                    fontSize = 16.sp,
                    color = ObsidianBlack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCategory(102L, "Women's Watches") }
                        .padding(vertical = 6.dp)
                )
                Text(
                    text = "Sonata Gold & Silver Editions",
                    fontSize = 16.sp,
                    color = ObsidianBlack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCategory(102L, "Women's Watches") }
                        .padding(vertical = 6.dp)
                )
                Text(
                    text = "Smart & Minimalist Dials",
                    fontSize = 16.sp,
                    color = ObsidianBlack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCategory(102L, "Women's Watches") }
                        .padding(vertical = 6.dp)
                )
            }
        }

        // --- 3. MEN ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { menExpanded = !menExpanded }
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MEN",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp,
                color = ObsidianBlack
            )
            Icon(
                imageVector = if (menExpanded) Icons.Default.Remove else Icons.Default.Add,
                contentDescription = null,
                tint = ObsidianBlack,
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(
            visible = menExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Titan Workwear Chronographs",
                    fontSize = 16.sp,
                    color = ObsidianBlack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCategory(101L, "Men's Watches") }
                        .padding(vertical = 6.dp)
                )
                Text(
                    text = "Fastrack Sporty & Digital",
                    fontSize = 16.sp,
                    color = ObsidianBlack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCategory(101L, "Men's Watches") }
                        .padding(vertical = 6.dp)
                )
                Text(
                    text = "Automatic & Leather Classics",
                    fontSize = 16.sp,
                    color = ObsidianBlack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCategory(101L, "Men's Watches") }
                        .padding(vertical = 6.dp)
                )
            }
        }

        // --- 4. KIDS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { kidsExpanded = !kidsExpanded }
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "KIDS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp,
                color = ObsidianBlack
            )
            Icon(
                imageVector = if (kidsExpanded) Icons.Default.Remove else Icons.Default.Add,
                contentDescription = null,
                tint = ObsidianBlack,
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(
            visible = kidsExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Zoop by Titan Kids Collection",
                    fontSize = 16.sp,
                    color = ObsidianBlack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCategory(108L, "Kids & Youth Watches") }
                        .padding(vertical = 6.dp)
                )
                Text(
                    text = "Fastrack Digital & Sporty Kids",
                    fontSize = 16.sp,
                    color = ObsidianBlack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCategory(108L, "Kids & Youth Watches") }
                        .padding(vertical = 6.dp)
                )
                Text(
                    text = "Fun & Colorful Learn-To-Tell Dials",
                    fontSize = 16.sp,
                    color = ObsidianBlack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCategory(108L, "Kids & Youth Watches") }
                        .padding(vertical = 6.dp)
                )
            }
        }

        // --- 5. WALL CLOCKS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { wallClocksExpanded = !wallClocksExpanded }
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WALL CLOCKS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp,
                color = ObsidianBlack
            )
            Icon(
                imageVector = if (wallClocksExpanded) Icons.Default.Remove else Icons.Default.Add,
                contentDescription = null,
                tint = ObsidianBlack,
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(
            visible = wallClocksExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Ajanta Living Room Wall Clocks",
                    fontSize = 16.sp,
                    color = ObsidianBlack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCategory(103L, "Wall Clocks") }
                        .padding(vertical = 6.dp)
                )
                Text(
                    text = "Oreva Modern Silent Sweep",
                    fontSize = 16.sp,
                    color = ObsidianBlack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCategory(103L, "Wall Clocks") }
                        .padding(vertical = 6.dp)
                )
                Text(
                    text = "Contemporary & Wooden Dials",
                    fontSize = 16.sp,
                    color = ObsidianBlack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCategory(103L, "Wall Clocks") }
                        .padding(vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * 4. WestStyleClub / UStyleClub Editorial Olive Taupe Banner (As in Screenshot)
 */
@Composable
private fun WestsideStyleClubBanner(
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = WestsideOliveBanner),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable(onClick = onJoinClick)
            .testTag("style_club_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(185.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Half: Model photo with "ARE you IN - JOIN NOW"
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxSize()
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=600&auto=format&fit=crop&q=80",
                    contentDescription = "WestStyleClub Member",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark overlay gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    WestsideOliveBanner.copy(alpha = 0.85f),
                                    WestsideOliveBanner
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "ARE",
                        color = PureWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "you IN",
                        color = PureWhite,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "JOIN NOW",
                        color = PureWhite,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            // Right Half: Polaroid Styled Editorial Frame
            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxSize()
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = PureWhite,
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=300&auto=format&fit=crop&q=80",
                            contentDescription = "Club Member",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(95.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "For you and the ones you love, discover stylish treats with Weststyle Club.",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 7.5.sp,
                            lineHeight = 9.sp,
                            color = ObsidianBlack,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text("🎂 CELEBRATE", fontSize = 6.5.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                            Text("🎁 BIRTHDAYS", fontSize = 6.5.sp, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 5. Westside Footer Links (As in screenshot: ABOUT US, WESTSTYLECLUB, W-STYLE)
 */
@Composable
private fun WestsideNavTextLinks(
    onAboutClick: () -> Unit,
    onStoreLocatorClick: () -> Unit,
    onCustomerCareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "ABOUT US",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = ObsidianBlack,
            modifier = Modifier.clickable(onClick = onAboutClick)
        )
        Text(
            text = "WESTSTYLECLUB",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = ObsidianBlack,
            modifier = Modifier.clickable(onClick = onAboutClick)
        )
        Text(
            text = "W-STYLE",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = ObsidianBlack,
            modifier = Modifier.clickable(onClick = onAboutClick)
        )
        Text(
            text = "STORE LOCATOR (KUKATPALLY)",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = ObsidianBlack,
            modifier = Modifier.clickable(onClick = onStoreLocatorClick)
        )
        Text(
            text = "CUSTOMER CARE & SUPPORT",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = ObsidianBlack,
            modifier = Modifier.clickable(onClick = onCustomerCareClick)
        )
    }
}

/**
 * 6. Westside Express Delivery Strip
 */
@Composable
private fun WestsideDeliveryStrip(
    onCallSupport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = LiteAshBg,
        shape = RoundedCornerShape(0.dp),
        border = BorderStroke(1.dp, GrayBorder),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.FlashOn,
                    contentDescription = "Express Delivery",
                    tint = ObsidianBlack,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "2-HR EXPRESS DELIVERY IN KUKATPALLY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = ObsidianBlack
                    )
                    Text(
                        text = "100% Genuine Brands • 7-Day Exchange",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(2.dp),
                color = ObsidianBlack,
                modifier = Modifier.clickable(onClick = onCallSupport)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Phone,
                        contentDescription = "Call",
                        tint = PureWhite,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("CALL", color = PureWhite, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Kukatpally Store Contact & Guarantees
 */
@Composable
private fun WestsideTrustAndStoreFooter(
    onCallStore: () -> Unit,
    onStoreDirections: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LiteAshBg)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "U STORE",
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = ObsidianBlack
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Kukatpally Housing Board Colony, Hyderabad",
            style = MaterialTheme.typography.bodySmall,
            color = ObsidianBlack.copy(alpha = 0.8f)
        )
        Text(
            text = "Official Retail Partner: Titan, Fastrack, Sonata, Ajanta, Oreva",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.5.sp,
            color = ObsidianBlack.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onCallStore,
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ObsidianBlack, contentColor = PureWhite),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("CALL STORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            Button(
                onClick = onStoreDirections,
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PureWhite, contentColor = ObsidianBlack),
                border = BorderStroke(1.dp, ObsidianBlack),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("GET DIRECTIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

