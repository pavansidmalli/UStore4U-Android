package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.ProductImage
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.GrayBorder
import com.example.ui.theme.LiteAshBg
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WarmGold
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Interactive inline zoomable watch image container that supports pinch-to-zoom,
 * pan gestures, double-tap zoom, and full-screen loupe inspection.
 */
@Composable
fun ZoomableWatchImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    onOpenFullscreenLoupe: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val scaleAnim = remember { Animatable(1f) }
    val offsetAnim = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    BoxWithConstraints(
        modifier = modifier
            .clipToBounds()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        coroutineScope.launch {
                            if (scale > 1.1f) {
                                // Reset to 1x
                                launch { scaleAnim.animateTo(1f, spring()) }
                                launch { offsetAnim.animateTo(Offset.Zero, spring()) }
                                scale = 1f
                                offset = Offset.Zero
                            } else {
                                // Zoom in to 2.5x
                                val targetScale = 2.5f
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val targetOffset = (center - tapOffset) * (targetScale - 1f)
                                launch { scaleAnim.animateTo(targetScale, spring()) }
                                launch { offsetAnim.animateTo(targetOffset, spring()) }
                                scale = targetScale
                                offset = targetOffset
                            }
                        }
                    },
                    onTap = {
                        // Open fullscreen loupe if user taps when not zoomed
                        if (scale <= 1.1f) {
                            onOpenFullscreenLoupe()
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    coroutineScope.launch {
                        val newScale = (scale * zoom).coerceIn(1f, 4.5f)
                        scale = newScale
                        scaleAnim.snapTo(newScale)

                        if (newScale > 1f) {
                            val maxX = (size.width * (newScale - 1f)) / 2f
                            val maxY = (size.height * (newScale - 1f)) / 2f
                            val newOffsetX = (offset.x + pan.x * newScale).coerceIn(-maxX, maxX)
                            val newOffsetY = (offset.y + pan.y * newScale).coerceIn(-maxY, maxY)
                            offset = Offset(newOffsetX, newOffsetY)
                            offsetAnim.snapTo(offset)
                        } else {
                            offset = Offset.Zero
                            offsetAnim.snapTo(Offset.Zero)
                        }
                    }
                }
            }
            .testTag("zoomable_watch_image_box")
    ) {
        val currentScale = if (scaleAnim.isRunning) scaleAnim.value else scale
        val currentOffset = if (offsetAnim.isRunning) offsetAnim.value else offset

        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = currentScale
                    scaleY = currentScale
                    translationX = currentOffset.x
                    translationY = currentOffset.y
                }
        )

        // Overlay Hint (Pinch to zoom / Double tap badge)
        AnimatedVisibility(
            visible = currentScale > 1.05f,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Surface(
                color = ObsidianBlack.copy(alpha = 0.85f),
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        launch { scaleAnim.animateTo(1f, spring()) }
                        launch { offsetAnim.animateTo(Offset.Zero, spring()) }
                        scale = 1f
                        offset = Offset.Zero
                    }
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = "Reset Zoom",
                        tint = ChampagneGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ZOOM ${"%.1f".format(currentScale)}X • RESET",
                        color = PureWhite,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Loupe Fullscreen Action Button (Top Right)
        Surface(
            color = PureWhite.copy(alpha = 0.92f),
            shape = CircleShape,
            border = androidx.compose.foundation.BorderStroke(0.8.dp, GrayBorder),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(36.dp)
                .clickable { onOpenFullscreenLoupe() }
                .testTag("open_fullscreen_loupe_button")
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Outlined.CenterFocusStrong,
                    contentDescription = "Inspect Craftsmanship",
                    tint = ObsidianBlack,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Bottom Right "Pinch to inspect" Subtle Pill when at 1x
        AnimatedVisibility(
            visible = currentScale <= 1.05f,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
        ) {
            Surface(
                color = ObsidianBlack.copy(alpha = 0.75f),
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier.clickable { onOpenFullscreenLoupe() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TouchApp,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PINCH TO ZOOM",
                        color = PureWhite,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 9.sp,
                        letterSpacing = 0.6.sp
                    )
                }
            }
        }
    }
}

/**
 * Fullscreen Luxury Loupe Inspection Theater.
 * Allows horology enthusiasts to examine watch dials, complications, finishing,
 * bevels, crowns, and movements up to 5x with smooth pan and zoom controls.
 */
@Composable
fun WatchLoupeInspectionDialog(
    productName: String,
    brand: String,
    images: List<ProductImage>,
    selectedIndex: Int,
    onSelectImage: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var scale by remember { mutableFloatStateOf(1.8f) } // default zoomed in slightly for loupe feel
    var offset by remember { mutableStateOf(Offset.Zero) }

    val scaleAnim = remember { Animatable(1.8f) }
    val offsetAnim = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    val currentImage = images.getOrNull(selectedIndex)?.src ?: ""

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            color = ObsidianBlack,
            modifier = Modifier
                .fillMaxSize()
                .testTag("watch_loupe_inspection_dialog")
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val boxWidth = maxWidth
                val boxHeight = maxHeight

                // High-precision Zoomable Layer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(selectedIndex) {
                            detectTapGestures(
                                onDoubleTap = { tapOffset ->
                                    coroutineScope.launch {
                                        if (scale > 2.2f) {
                                            launch { scaleAnim.animateTo(1f, spring()) }
                                            launch { offsetAnim.animateTo(Offset.Zero, spring()) }
                                            scale = 1f
                                            offset = Offset.Zero
                                        } else {
                                            val targetScale = 3.5f
                                            val center = Offset(size.width / 2f, size.height / 2f)
                                            val targetOffset = (center - tapOffset) * (targetScale - 1f)
                                            launch { scaleAnim.animateTo(targetScale, spring()) }
                                            launch { offsetAnim.animateTo(targetOffset, spring()) }
                                            scale = targetScale
                                            offset = targetOffset
                                        }
                                    }
                                }
                            )
                        }
                        .pointerInput(selectedIndex) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                coroutineScope.launch {
                                    val newScale = (scale * zoom).coerceIn(1f, 5f)
                                    scale = newScale
                                    scaleAnim.snapTo(newScale)

                                    val maxX = (size.width * (newScale - 1f)) / 2f
                                    val maxY = (size.height * (newScale - 1f)) / 2f
                                    val newOffsetX = (offset.x + pan.x * newScale).coerceIn(-maxX, maxX)
                                    val newOffsetY = (offset.y + pan.y * newScale).coerceIn(-maxY, maxY)
                                    offset = Offset(newOffsetX, newOffsetY)
                                    offsetAnim.snapTo(offset)
                                }
                            }
                        }
                ) {
                    val currentScale = if (scaleAnim.isRunning) scaleAnim.value else scale
                    val currentOffset = if (offsetAnim.isRunning) offsetAnim.value else offset

                    AsyncImage(
                        model = currentImage,
                        contentDescription = productName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = currentScale
                                scaleY = currentScale
                                translationX = currentOffset.x
                                translationY = currentOffset.y
                            }
                    )
                }

                // Top Header Bar
                Surface(
                    color = ObsidianBlack.copy(alpha = 0.85f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = ChampagneGold.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(2.dp)
                                ) {
                                    Text(
                                        text = "HOROLOGY LOUPE",
                                        color = ChampagneGold,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${selectedIndex + 1} / ${images.size.coerceAtLeast(1)}",
                                    color = PureWhite.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = productName,
                                color = PureWhite,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PureWhite.copy(alpha = 0.12f))
                                .testTag("close_loupe_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close Loupe",
                                tint = PureWhite
                            )
                        }
                    }
                }

                // Bottom Floating Control Bar (Zoom steppers & Thumbnails)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(ObsidianBlack.copy(alpha = 0.90f))
                        .padding(bottom = 16.dp)
                ) {
                    // Zoom Controls & Magnification Level
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Craftsmanship indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.CenterFocusStrong,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MAGNIFICATION: ${"%.1f".format(scale)}X",
                                color = PureWhite,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                fontSize = 11.sp
                            )
                        }

                        // Preset Zoom Buttons (1x, 2.5x, 4x)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LoupePresetButton(
                                label = "1X",
                                isActive = scale in 0.9f..1.1f,
                                onClick = {
                                    coroutineScope.launch {
                                        launch { scaleAnim.animateTo(1f, spring()) }
                                        launch { offsetAnim.animateTo(Offset.Zero, spring()) }
                                        scale = 1f
                                        offset = Offset.Zero
                                    }
                                }
                            )

                            LoupePresetButton(
                                label = "2.5X",
                                isActive = scale in 2.3f..2.7f,
                                onClick = {
                                    coroutineScope.launch {
                                        launch { scaleAnim.animateTo(2.5f, spring()) }
                                        launch { offsetAnim.animateTo(Offset.Zero, spring()) }
                                        scale = 2.5f
                                        offset = Offset.Zero
                                    }
                                }
                            )

                            LoupePresetButton(
                                label = "4X",
                                isActive = scale in 3.8f..4.2f,
                                onClick = {
                                    coroutineScope.launch {
                                        launch { scaleAnim.animateTo(4f, spring()) }
                                        launch { offsetAnim.animateTo(Offset.Zero, spring()) }
                                        scale = 4f
                                        offset = Offset.Zero
                                    }
                                }
                            )
                        }
                    }

                    // Multi-Image Gallery Row (Dial, Caseback, Movement, Crown)
                    if (images.size > 1) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(images) { index, img ->
                                val isSelected = index == selectedIndex
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(PureWhite.copy(alpha = 0.1f))
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) ChampagneGold else PureWhite.copy(alpha = 0.25f),
                                            shape = RoundedCornerShape(2.dp)
                                        )
                                        .clickable {
                                            onSelectImage(index)
                                            coroutineScope.launch {
                                                launch { scaleAnim.animateTo(1.8f, spring()) }
                                                launch { offsetAnim.animateTo(Offset.Zero, spring()) }
                                                scale = 1.8f
                                                offset = Offset.Zero
                                            }
                                        }
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

                    // Craftsmanship highlights label
                    Text(
                        text = "Double-tap or pinch to inspect dial sunburst, indices, bezel click & sapphire clarity.",
                        color = PureWhite.copy(alpha = 0.55f),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        letterSpacing = 0.4.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun LoupePresetButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isActive) ChampagneGold else PureWhite.copy(alpha = 0.15f),
        shape = RoundedCornerShape(2.dp),
        modifier = Modifier
            .clickable { onClick() }
            .testTag("loupe_preset_$label")
    ) {
        Text(
            text = label,
            color = if (isActive) ObsidianBlack else PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 0.6.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}
