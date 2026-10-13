package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GrayBorder
import com.example.ui.theme.LiteAshBg
import com.example.ui.theme.LiteAshWhite
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SubtleDivider

/**
 * Creates a shimmering animated brush for loading skeletons
 */
@Composable
fun rememberSkeletonShimmerBrush(
    shimmerColor: Color = Color(0xFFECEFF1),
    highlightColor: Color = Color(0xFFFAFAFA),
    durationMillis: Int = 1100
): Brush {
    val transition = rememberInfiniteTransition(label = "skeleton_shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -300f,
        targetValue = 1300f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "skeleton_shimmer_translate"
    )

    return Brush.linearGradient(
        colors = listOf(
            shimmerColor,
            highlightColor,
            shimmerColor
        ),
        start = Offset(translateAnim, translateAnim * 0.5f),
        end = Offset(translateAnim + 400f, translateAnim * 0.5f + 400f)
    )
}

/**
 * Modifier extension to apply shimmer background easily
 */
@Composable
fun Modifier.shimmerBackground(
    shape: Shape = RoundedCornerShape(2.dp),
    brush: Brush = rememberSkeletonShimmerBrush()
): Modifier = this
    .clip(shape)
    .background(brush)

/**
 * Atomic skeleton box element
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    height: Dp? = null,
    width: Dp? = null,
    shape: Shape = RoundedCornerShape(2.dp),
    brush: Brush = rememberSkeletonShimmerBrush()
) {
    val mod = modifier
        .then(if (width != null) Modifier.width(width) else Modifier)
        .then(if (height != null) Modifier.height(height) else Modifier)
        .shimmerBackground(shape = shape, brush = brush)

    Box(modifier = mod)
}

/**
 * Skeleton representation of a single Watch Product Card
 * Matches exact geometry of WatchProductCard
 */
@Composable
fun WatchCardSkeleton(
    modifier: Modifier = Modifier,
    brush: Brush = rememberSkeletonShimmerBrush()
) {
    Card(
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, GrayBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("watch_card_skeleton")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Watch Image Placeholder (Aspect 1:1) with wishlist circle overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(LiteAshBg)
            ) {
                // Image shimmer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shimmerBackground(shape = RoundedCornerShape(0.dp), brush = brush)
                )

                // Top-left brand tag placeholder
                SkeletonBox(
                    width = 44.dp,
                    height = 14.dp,
                    shape = RoundedCornerShape(2.dp),
                    brush = brush,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )

                // Top-right wishlist icon placeholder
                SkeletonBox(
                    width = 28.dp,
                    height = 28.dp,
                    shape = CircleShape,
                    brush = brush,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }

            // Card Body details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                // Brand name line
                SkeletonBox(
                    width = 60.dp,
                    height = 10.dp,
                    shape = RoundedCornerShape(2.dp),
                    brush = brush
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Watch Title line 1
                SkeletonBox(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    height = 12.dp,
                    shape = RoundedCornerShape(2.dp),
                    brush = brush
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Watch Title line 2
                SkeletonBox(
                    modifier = Modifier.fillMaxWidth(0.55f),
                    height = 12.dp,
                    shape = RoundedCornerShape(2.dp),
                    brush = brush
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rating & Star Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(5) {
                        SkeletonBox(
                            width = 11.dp,
                            height = 11.dp,
                            shape = CircleShape,
                            brush = brush
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    SkeletonBox(
                        width = 24.dp,
                        height = 10.dp,
                        shape = RoundedCornerShape(2.dp),
                        brush = brush
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Price & MRP row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SkeletonBox(
                        width = 56.dp,
                        height = 14.dp,
                        shape = RoundedCornerShape(2.dp),
                        brush = brush
                    )
                    SkeletonBox(
                        width = 40.dp,
                        height = 11.dp,
                        shape = RoundedCornerShape(2.dp),
                        brush = brush
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Full-width Add To Bag action button placeholder
                SkeletonBox(
                    modifier = Modifier.fillMaxWidth(),
                    height = 30.dp,
                    shape = RoundedCornerShape(2.dp),
                    brush = brush
                )
            }
        }
    }
}

/**
 * Skeleton representation of a Watch Carousel (e.g. Trending Now, New In Store)
 */
@Composable
fun WatchCarouselSkeleton(
    modifier: Modifier = Modifier,
    brush: Brush = rememberSkeletonShimmerBrush()
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("watch_carousel_skeleton")
    ) {
        // Carousel Header (Title, Subtitle & View All)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                SkeletonBox(
                    width = 140.dp,
                    height = 16.dp,
                    shape = RoundedCornerShape(2.dp),
                    brush = brush
                )
                Spacer(modifier = Modifier.height(4.dp))
                SkeletonBox(
                    width = 200.dp,
                    height = 11.dp,
                    shape = RoundedCornerShape(2.dp),
                    brush = brush
                )
            }
            SkeletonBox(
                width = 54.dp,
                height = 12.dp,
                shape = RoundedCornerShape(2.dp),
                brush = brush
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Row of Watch Cards
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(4) {
                WatchCardSkeleton(
                    modifier = Modifier.width(175.dp),
                    brush = brush
                )
            }
        }
    }
}

/**
 * Skeleton Grid of Watches (Used in All Products, Category, Search screens)
 */
@Composable
fun WatchGridSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 6,
    brush: Brush = rememberSkeletonShimmerBrush()
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("watch_grid_skeleton")
    ) {
        items(itemCount) {
            WatchCardSkeleton(brush = brush)
        }
    }
}

/**
 * Full Homepage Westside-Style Feed Skeleton
 * Mimics top categories, lookbook cards, sort bar, carousels, and express delivery banner
 */
@Composable
fun WestsideHomeFeedSkeleton(
    modifier: Modifier = Modifier
) {
    val brush = rememberSkeletonShimmerBrush()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite)
            .testTag("home_feed_skeleton"),
        contentPadding = PaddingValues(bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // 1. Editorial Category Tabs Shimmer
        item {
            Surface(
                color = PureWhite,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(56.dp, 64.dp, 52.dp, 48.dp, 72.dp, 60.dp).forEach { width ->
                        SkeletonBox(
                            width = width,
                            height = 14.dp,
                            shape = RoundedCornerShape(2.dp),
                            brush = brush
                        )
                    }
                }
            }
            HorizontalDivider(color = SubtleDivider)
        }

        // 2. Sort & Price Filter Chips Bar Shimmer
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PureWhite)
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SkeletonBox(width = 100.dp, height = 30.dp, shape = RoundedCornerShape(2.dp), brush = brush)
                    SkeletonBox(width = 120.dp, height = 30.dp, shape = RoundedCornerShape(2.dp), brush = brush)
                    SkeletonBox(width = 120.dp, height = 30.dp, shape = RoundedCornerShape(2.dp), brush = brush)
                    SkeletonBox(width = 90.dp, height = 30.dp, shape = RoundedCornerShape(2.dp), brush = brush)
                }
            }
        }

        // 3. Tall Visual Lookbook Cards Row Shimmer (Woman, Man, Special Prices, Clocks)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PureWhite)
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    repeat(3) {
                        Card(
                            shape = RoundedCornerShape(2.dp),
                            colors = CardDefaults.cardColors(containerColor = LiteAshBg),
                            border = BorderStroke(1.dp, GrayBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(210.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                SkeletonBox(
                                    modifier = Modifier.fillMaxSize(),
                                    shape = RoundedCornerShape(2.dp),
                                    brush = brush
                                )
                                SkeletonBox(
                                    width = 60.dp,
                                    height = 14.dp,
                                    shape = RoundedCornerShape(2.dp),
                                    brush = brush,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. 2-Hour Express Delivery Strip Shimmer
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .border(1.dp, GrayBorder, RoundedCornerShape(2.dp))
                    .background(LiteAshWhite)
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SkeletonBox(width = 36.dp, height = 36.dp, shape = CircleShape, brush = brush)
                    Column(modifier = Modifier.weight(1f)) {
                        SkeletonBox(width = 140.dp, height = 13.dp, shape = RoundedCornerShape(2.dp), brush = brush)
                        Spacer(modifier = Modifier.height(4.dp))
                        SkeletonBox(width = 210.dp, height = 10.dp, shape = RoundedCornerShape(2.dp), brush = brush)
                    }
                    SkeletonBox(width = 60.dp, height = 24.dp, shape = RoundedCornerShape(2.dp), brush = brush)
                }
            }
        }

        // 5. Trending Now Watch Carousel Skeleton
        item {
            Spacer(modifier = Modifier.height(10.dp))
            WatchCarouselSkeleton(brush = brush)
        }

        // 6. New In Store Watch Carousel Skeleton
        item {
            Spacer(modifier = Modifier.height(10.dp))
            WatchCarouselSkeleton(brush = brush)
        }

        // 7. Curated For You Watch Carousel Skeleton
        item {
            Spacer(modifier = Modifier.height(10.dp))
            WatchCarouselSkeleton(brush = brush)
        }
    }
}

/**
 * Skeleton representation for Product Detail Screen
 */
@Composable
fun WatchDetailSkeleton(
    modifier: Modifier = Modifier
) {
    val brush = rememberSkeletonShimmerBrush()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite)
            .testTag("watch_detail_skeleton")
    ) {
        // Main Hero Image Area Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(LiteAshBg)
        ) {
            SkeletonBox(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(0.dp),
                brush = brush
            )
            // Wishlist icon circle placeholder top-right
            SkeletonBox(
                width = 36.dp,
                height = 36.dp,
                shape = CircleShape,
                brush = brush,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )
        }

        // Thumbnail selector strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(4) {
                SkeletonBox(
                    width = 56.dp,
                    height = 56.dp,
                    shape = RoundedCornerShape(2.dp),
                    brush = brush
                )
            }
        }

        HorizontalDivider(color = SubtleDivider)

        // Detail Content Area
        Column(modifier = Modifier.padding(16.dp)) {
            // Brand Name Tag
            SkeletonBox(
                width = 80.dp,
                height = 14.dp,
                shape = RoundedCornerShape(2.dp),
                brush = brush
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Product Title
            SkeletonBox(
                modifier = Modifier.fillMaxWidth(0.85f),
                height = 20.dp,
                shape = RoundedCornerShape(2.dp),
                brush = brush
            )
            Spacer(modifier = Modifier.height(6.dp))
            SkeletonBox(
                modifier = Modifier.fillMaxWidth(0.5f),
                height = 16.dp,
                shape = RoundedCornerShape(2.dp),
                brush = brush
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Rating Stars & Reviews Count
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(5) {
                    SkeletonBox(width = 16.dp, height = 16.dp, shape = CircleShape, brush = brush)
                }
                Spacer(modifier = Modifier.width(6.dp))
                SkeletonBox(width = 60.dp, height = 12.dp, shape = RoundedCornerShape(2.dp), brush = brush)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price & MRP Block
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SkeletonBox(width = 100.dp, height = 24.dp, shape = RoundedCornerShape(2.dp), brush = brush)
                SkeletonBox(width = 70.dp, height = 16.dp, shape = RoundedCornerShape(2.dp), brush = brush)
                SkeletonBox(width = 60.dp, height = 18.dp, shape = RoundedCornerShape(2.dp), brush = brush)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Express Delivery Box Shimmer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GrayBorder, RoundedCornerShape(2.dp))
                    .background(LiteAshWhite)
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SkeletonBox(width = 28.dp, height = 28.dp, shape = CircleShape, brush = brush)
                    Column(modifier = Modifier.weight(1f)) {
                        SkeletonBox(width = 130.dp, height = 12.dp, shape = RoundedCornerShape(2.dp), brush = brush)
                        Spacer(modifier = Modifier.height(4.dp))
                        SkeletonBox(width = 180.dp, height = 10.dp, shape = RoundedCornerShape(2.dp), brush = brush)
                    }
                }
            }
        }
    }
}
