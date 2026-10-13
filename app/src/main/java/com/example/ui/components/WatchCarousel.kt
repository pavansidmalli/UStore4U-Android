package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.WestsideTerracotta

@Composable
fun WatchCarousel(
    title: String,
    subtitle: String? = null,
    products: List<Product>,
    wishlistProductIds: Set<Long>,
    onProductClick: (Long) -> Unit,
    onWishlistToggle: (Product) -> Unit,
    onQuickAddToCart: (Product) -> Unit,
    onViewAllClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (products.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        // Westside Section Header with Tracked Uppercase
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (onViewAllClick != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = onViewAllClick)
                        .padding(vertical = 4.dp)
                        .testTag("view_all_${title.replace(" ", "_").lowercase()}")
                ) {
                    Text(
                        text = "VIEW ALL",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = ObsidianBlack
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = ObsidianBlack,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Product List
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                items = products,
                key = { it.id },
                contentType = { "product_card" }
            ) { product ->
                WatchProductCard(
                    product = product,
                    isInWishlist = wishlistProductIds.contains(product.id),
                    onProductClick = { onProductClick(product.id) },
                    onWishlistToggle = { onWishlistToggle(product) },
                    onQuickAddToCart = { onQuickAddToCart(product) },
                    modifier = Modifier.width(165.dp)
                )
            }
        }
    }
}

