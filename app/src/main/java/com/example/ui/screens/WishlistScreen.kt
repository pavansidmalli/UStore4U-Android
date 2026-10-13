package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.WishlistEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.WatchRatingBar
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.GrayBorder
import com.example.ui.theme.LiteAshBg
import com.example.ui.theme.LiteAshWhite
import com.example.ui.theme.LuxuryRed
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SubtleDivider
import com.example.ui.theme.WestsideTerracotta
import com.example.ui.viewmodel.WishlistViewModel

private fun formatInr(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        "₹%,d".format(amount.toLong())
    } else {
        "₹%,.2f".format(amount)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    viewModel: WishlistViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (Long) -> Unit,
    onNavigateToCart: () -> Unit,
    onExploreWatches: () -> Unit,
    modifier: Modifier = Modifier
) {
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()

    Scaffold(
        containerColor = LiteAshWhite,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Text(
                            text = "SAVED TIMEPIECES (${wishlistItems.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = ObsidianBlack
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("wishlist_back_button")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = ObsidianBlack
                            )
                        }
                    },
                    actions = {
                        if (wishlistItems.isNotEmpty()) {
                            TextButton(
                                onClick = { viewModel.clearWishlist() },
                                modifier = Modifier.testTag("clear_wishlist_button")
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

                        IconButton(onClick = onNavigateToCart, modifier = Modifier.testTag("wishlist_cart_button")) {
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
        modifier = modifier.testTag("wishlist_screen")
    ) { innerPadding ->
        if (wishlistItems.isEmpty()) {
            EmptyStateView(
                icon = Icons.Outlined.FavoriteBorder,
                title = "YOUR WISHLIST IS EMPTY",
                message = "Save your dream Titan, Fastrack, Sonata timepieces and wall decor here to purchase later.",
                actionButtonText = "EXPLORE COLLECTION",
                onActionClick = onExploreWatches,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(
                    items = wishlistItems,
                    key = { it.productId },
                    contentType = { "wishlist_item" }
                ) { item ->
                    WishlistItemCard(
                        item = item,
                        onClick = { onNavigateToProduct(item.productId) },
                        onMoveToCart = { viewModel.moveToCart(item) },
                        onRemove = { viewModel.removeFromWishlist(item.productId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WishlistItemCard(
    item: WishlistEntity,
    onClick: () -> Unit,
    onMoveToCart: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("wishlist_card_${item.productId}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.95f)
                    .background(LiteAshBg)
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PureWhite.copy(alpha = 0.9f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Remove from wishlist",
                        tint = WestsideTerracotta,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = ObsidianBlack,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatInr(item.price),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = ObsidianBlack
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onMoveToCart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ObsidianBlack,
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(2.dp),
                    contentPadding = PaddingValues(vertical = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("move_to_cart_${item.productId}")
                ) {
                    Text(
                        "MOVE TO BAG",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }
    }
}
