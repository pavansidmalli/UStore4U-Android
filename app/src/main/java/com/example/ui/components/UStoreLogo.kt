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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.GrayBorder
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WarmGold

/**
 * Official U STORE Brand Logo Composable
 * Matches the bold serif "U STORE" brand typography
 */
@Composable
fun UStoreLogo(
    modifier: Modifier = Modifier,
    color: Color = ObsidianBlack,
    fontSize: TextUnit = 22.sp,
    letterSpacing: TextUnit = 1.5.sp,
    showSubtitle: Boolean = false,
    subtitleText: String = "HYDERABAD • KUKATPALLY • 2-HR EXPRESS",
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        modifier = modifier.testTag("ustore_logo"),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (horizontalAlignment == Alignment.CenterHorizontally) Arrangement.Center else Arrangement.Start
        ) {
            Text(
                text = "U STORE",
                color = color,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize,
                letterSpacing = letterSpacing,
                style = MaterialTheme.typography.titleLarge,
                textAlign = if (horizontalAlignment == Alignment.CenterHorizontally) TextAlign.Center else TextAlign.Start
            )
        }

        if (showSubtitle) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitleText,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.65f),
                letterSpacing = 0.6.sp,
                fontSize = (fontSize.value * 0.4f).coerceAtLeast(8f).sp,
                textAlign = if (horizontalAlignment == Alignment.CenterHorizontally) TextAlign.Center else TextAlign.Start
            )
        }
    }
}

/**
 * Luxury Brand Emblem featuring the circular timepiece bezel and serif "U" monogram
 */
@Composable
fun UStoreBrandEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    containerColor: Color = PureWhite,
    monogramColor: Color = ObsidianBlack,
    borderColor: Color = ObsidianBlack
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor)
            .border(BorderStroke(2.dp, borderColor), CircleShape)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(BorderStroke(0.8.dp, ChampagneGold), CircleShape)
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "U",
                color = monogramColor,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.52f).sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * App Preloader with Brand Logo and animated progress indicator
 */
@Composable
fun UStoreAppPreloader(
    modifier: Modifier = Modifier,
    statusText: String = "AUTHENTICATING TIMEPIECES..."
) {
    val transition = rememberInfiniteTransition(label = "preloader_transition")
    val pulseAnim by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emblem_pulse"
    )

    val progressShift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress_bar_shift"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite)
            .testTag("app_preloader"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Luxury Watch Emblem with subtle breathing pulse
        UStoreBrandEmblem(
            size = 86.dp,
            modifier = Modifier.scale(pulseAnim)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Official U STORE Wordmark
        UStoreLogo(
            fontSize = 26.sp,
            letterSpacing = 3.sp,
            showSubtitle = true,
            subtitleText = "HYDERABAD • KUKATPALLY • 2-HR EXPRESS",
            horizontalAlignment = Alignment.CenterHorizontally
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Tagline
        Text(
            text = "1000+ STYLES • WATCHES & CLOCKS",
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.SemiBold,
            color = WarmGold
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Sleek Animated Minimalist Progress Bar
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(2.5.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(GrayBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(2.5.dp)
                    .align(Alignment.CenterStart)
                    .background(
                        Brush.horizontalGradient(
                            listOf(ChampagneGold, ObsidianBlack, ChampagneGold)
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = statusText,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.5.sp,
            letterSpacing = 1.2.sp,
            color = ObsidianBlack.copy(alpha = 0.55f),
            fontWeight = FontWeight.Medium
        )
    }
}

