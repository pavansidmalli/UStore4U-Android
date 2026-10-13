package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ErrorRetryView
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.ShimmerBrush
import com.example.ui.components.WatchGridSkeleton
import com.example.ui.components.WatchProductCard
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
import com.example.ui.theme.WestsideTerracotta
import com.example.ui.viewmodel.ProductListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    title: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (Long) -> Unit,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val wishlistIds by viewModel.wishlistProductIds.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = LiteAshWhite,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Text(
                            text = (title ?: uiState.selectedCategoryName ?: "ALL TIMEPIECES").uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("product_list_back_button")
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
                            onClick = { viewModel.openFilterSheet() },
                            modifier = Modifier.testTag("open_filter_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (uiState.activeFilterCount > 0) {
                                        Badge(containerColor = ObsidianBlack, contentColor = PureWhite) {
                                            Text(uiState.activeFilterCount.toString(), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FilterList,
                                    contentDescription = "Filter Watches",
                                    tint = ObsidianBlack
                                )
                            }
                        }

                        IconButton(
                            onClick = onNavigateToCart,
                            modifier = Modifier.testTag("product_list_cart_button")
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
                androidx.compose.material3.HorizontalDivider(thickness = 0.8.dp, color = SubtleDivider)
            }
        },
        modifier = modifier.testTag("product_list_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Search Bar & Quick Brand / Model Filter Section
            Surface(
                color = PureWhite,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Main Search Input Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            placeholder = {
                                Text(
                                    "Search by brand or model name...",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = "Search watches",
                                    tint = if (uiState.searchQuery.isNotBlank()) ChampagneGold else ObsidianBlack,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                if (uiState.searchQuery.isNotBlank()) {
                                    IconButton(
                                        onClick = { viewModel.clearSearch() },
                                        modifier = Modifier.testTag("clear_search_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Clear,
                                            contentDescription = "Clear search",
                                            tint = ObsidianBlack,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(2.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = LiteAshBg,
                                unfocusedContainerColor = LiteAshBg,
                                unfocusedBorderColor = GrayBorder.copy(alpha = 0.8f),
                                focusedBorderColor = ObsidianBlack
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("product_search_input")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Quick Filter sheet toggle button
                        Surface(
                            color = if (uiState.activeFilterCount > 0) ObsidianBlack else LiteAshBg,
                            shape = RoundedCornerShape(2.dp),
                            border = BorderStroke(1.dp, if (uiState.activeFilterCount > 0) ObsidianBlack else GrayBorder),
                            modifier = Modifier
                                .height(50.dp)
                                .width(50.dp)
                                .clickable { viewModel.openFilterSheet() }
                                .testTag("search_filter_toggle_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                BadgedBox(
                                    badge = {
                                        if (uiState.activeFilterCount > 0) {
                                            Badge(
                                                containerColor = ChampagneGold,
                                                contentColor = ObsidianBlack
                                            ) {
                                                Text(
                                                    uiState.activeFilterCount.toString(),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Tune,
                                        contentDescription = "Filter",
                                        tint = if (uiState.activeFilterCount > 0) PureWhite else ObsidianBlack,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Quick Brand Filter Chips Carousel
                    val brands = listOf("ALL", "Titan", "Fastrack", "Sonata", "Casio", "Ajanta", "Helix", "Timex")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BRANDS:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            letterSpacing = 0.6.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )

                        brands.forEach { brandName ->
                            val isSelected = if (brandName == "ALL") {
                                uiState.selectedBrand == null
                            } else {
                                uiState.selectedBrand.equals(brandName, ignoreCase = true)
                            }

                            Surface(
                                color = if (isSelected) ObsidianBlack else LiteAshBg,
                                shape = RoundedCornerShape(2.dp),
                                border = BorderStroke(0.8.dp, if (isSelected) ObsidianBlack else GrayBorder),
                                modifier = Modifier
                                    .clickable {
                                        viewModel.setBrand(if (brandName == "ALL") null else brandName)
                                    }
                                    .testTag("brand_chip_$brandName")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isSelected && brandName != "ALL") {
                                        Icon(
                                            imageVector = Icons.Outlined.Check,
                                            contentDescription = null,
                                            tint = ChampagneGold,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = brandName.uppercase(),
                                        color = if (isSelected) PureWhite else ObsidianBlack,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }

                    // Popular Model Quick Search Suggestions (when search is empty or active)
                    val popularModels = listOf("Octane", "Reflex Smart", "Poze", "Vintage", "Titan Raga", "Bandhan", "Chronograph", "Wall Clock")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MODELS:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            letterSpacing = 0.6.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )

                        popularModels.forEach { modelName ->
                            val isModelSelected = uiState.searchQuery.equals(modelName, ignoreCase = true)
                            Surface(
                                color = if (isModelSelected) ChampagneGold.copy(alpha = 0.25f) else PureWhite,
                                shape = RoundedCornerShape(2.dp),
                                border = BorderStroke(0.8.dp, if (isModelSelected) ObsidianBlack else GrayBorder.copy(alpha = 0.8f)),
                                modifier = Modifier
                                    .clickable {
                                        if (isModelSelected) {
                                            viewModel.clearSearch()
                                        } else {
                                            viewModel.onSearchQueryChanged(modelName)
                                        }
                                    }
                                    .testTag("model_suggestion_$modelName")
                            ) {
                                Text(
                                    text = modelName,
                                    color = ObsidianBlack,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isModelSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.5.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(thickness = 0.8.dp, color = SubtleDivider, modifier = Modifier.padding(top = 4.dp))
                }
            }

            // Category Filter Row
            if (uiState.categories.isNotEmpty()) {
                Surface(
                    color = PureWhite,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = uiState.selectedCategoryId == null,
                                    onClick = { viewModel.setCategory(null) },
                                    label = { 
                                        Text(
                                            "ALL CATEGORIES",
                                            fontWeight = if (uiState.selectedCategoryId == null) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp,
                                            letterSpacing = 0.8.sp
                                        ) 
                                    },
                                    shape = RoundedCornerShape(2.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ObsidianBlack,
                                        selectedLabelColor = PureWhite,
                                        containerColor = PureWhite,
                                        labelColor = ObsidianBlack
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = uiState.selectedCategoryId == null,
                                        borderColor = GrayBorder,
                                        selectedBorderColor = ObsidianBlack,
                                        borderWidth = 1.dp
                                    )
                                )
                            }
                            items(uiState.categories, key = { it.id }) { cat ->
                                val isSelected = uiState.selectedCategoryId == cat.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setCategory(cat.id, cat.name) },
                                    label = { 
                                        Text(
                                            cat.name.uppercase(),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp,
                                            letterSpacing = 0.8.sp
                                        ) 
                                    },
                                    shape = RoundedCornerShape(2.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ObsidianBlack,
                                        selectedLabelColor = PureWhite,
                                        containerColor = PureWhite,
                                        labelColor = ObsidianBlack
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = GrayBorder,
                                        selectedBorderColor = ObsidianBlack,
                                        borderWidth = 1.dp
                                    )
                                )
                            }
                        }
                        HorizontalDivider(thickness = 0.8.dp, color = SubtleDivider)
                    }
                }
            }

            // Results count and active filter indicators
            Surface(
                color = LiteAshWhite,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${uiState.products.size} WATCHES AVAILABLE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.openFilterSheet() }
                        ) {
                            Text(
                                text = "SORT: ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = uiState.sortOption.title.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = ObsidianBlack,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Active Search / Filter Badges (if any active filter or search query exists)
                    if (uiState.searchQuery.isNotBlank() || uiState.selectedBrand != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (uiState.searchQuery.isNotBlank()) {
                                Surface(
                                    color = PureWhite,
                                    shape = RoundedCornerShape(2.dp),
                                    border = BorderStroke(0.8.dp, GrayBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Query: \"${uiState.searchQuery}\"",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
                                            color = ObsidianBlack
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Remove query",
                                            tint = ObsidianBlack,
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clickable { viewModel.clearSearch() }
                                        )
                                    }
                                }
                            }

                            if (uiState.selectedBrand != null) {
                                Surface(
                                    color = PureWhite,
                                    shape = RoundedCornerShape(2.dp),
                                    border = BorderStroke(0.8.dp, GrayBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Brand: ${uiState.selectedBrand}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
                                            color = ObsidianBlack
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Remove brand filter",
                                            tint = ObsidianBlack,
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clickable { viewModel.setBrand(null) }
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "Clear All",
                                color = LuxuryRed,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.clearSearch()
                                        viewModel.setBrand(null)
                                    }
                                    .padding(start = 4.dp)
                            )
                        }
                    }
                }
            }

            // Main Product Grid
            val refreshState = rememberPullToRefreshState()

            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.loadProducts(isRefresh = true) },
                state = refreshState,
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = refreshState,
                        isRefreshing = uiState.isRefreshing,
                        containerColor = PureWhite,
                        color = ObsidianBlack,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                },
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    uiState.isLoading && !uiState.isRefreshing -> {
                        WatchGridSkeleton()
                    }
                    uiState.errorMessage != null && uiState.products.isEmpty() -> {
                        ErrorRetryView(
                            message = uiState.errorMessage ?: "Failed to load watch list.",
                            onRetry = { viewModel.loadProducts() }
                        )
                    }
                    uiState.products.isEmpty() -> {
                        EmptyStateView(
                            title = "No Watches Found",
                            message = "We couldn't find any watches matching your search or filters. Try adjusting your criteria.",
                            actionButtonText = "Reset Filters",
                            onActionClick = {
                                viewModel.applyFilters(
                                    categoryId = null,
                                    brand = null,
                                    minPrice = null,
                                    maxPrice = null,
                                    inStock = false,
                                    onSale = false,
                                    sort = com.example.data.repository.SortOption.LATEST
                                )
                                viewModel.clearSearch()
                            }
                        )
                    }
                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = uiState.products,
                                key = { it.id },
                                contentType = { "product_card" }
                            ) { product ->
                                WatchProductCard(
                                    product = product,
                                    isInWishlist = wishlistIds.contains(product.id),
                                    onProductClick = { onNavigateToProduct(product.id) },
                                    onWishlistToggle = { viewModel.toggleWishlist(product) },
                                    onQuickAddToCart = { viewModel.quickAddToCart(product) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Filter Modal Bottom Sheet
        if (uiState.isFilterSheetOpen) {
            FilterBottomSheet(
                onDismissRequest = { viewModel.closeFilterSheet() },
                categories = uiState.categories,
                selectedCategoryId = uiState.selectedCategoryId,
                selectedBrand = uiState.selectedBrand,
                minPrice = uiState.minPrice,
                maxPrice = uiState.maxPrice,
                onlyInStock = uiState.onlyInStock,
                onlyOnSale = uiState.onlyOnSale,
                currentSort = uiState.sortOption,
                onApplyFilters = { catId, brand, minP, maxP, inStock, onSale, sort ->
                    viewModel.applyFilters(catId, brand, minP, maxP, inStock, onSale, sort)
                }
            )
        }
    }
}

