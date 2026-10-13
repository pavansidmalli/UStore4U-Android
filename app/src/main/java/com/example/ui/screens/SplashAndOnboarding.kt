package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.components.UStoreBrandEmblem
import com.example.ui.components.UStoreLogo
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.GrayBorder
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RichCharcoal
import com.example.ui.theme.WarmGold
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "splash_alpha"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.9f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "splash_scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2200)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scaleAnim)
                .alpha(alphaAnim)
        ) {
            // Official Brand Monogram / Bezel Emblem
            UStoreBrandEmblem(
                size = 92.dp,
                containerColor = PureWhite,
                monogramColor = ObsidianBlack,
                borderColor = ObsidianBlack
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Official U STORE Wordmark & Subtitle
            UStoreLogo(
                color = ObsidianBlack,
                fontSize = 28.sp,
                letterSpacing = 3.5.sp,
                showSubtitle = true,
                subtitleText = "HYDERABAD • KUKATPALLY • 2-HR EXPRESS",
                horizontalAlignment = Alignment.CenterHorizontally
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "1000+ STYLES • WATCHES & CLOCKS",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = WarmGold
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Sleek preloader bar
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(2.5.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(GrayBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(2.5.dp)
                        .align(Alignment.CenterStart)
                        .background(
                            Brush.horizontalGradient(
                                listOf(ChampagneGold, ObsidianBlack, ChampagneGold)
                            )
                        )
                )
            }
        }

        // Bottom Footer Details
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Kukatpally, Hyderabad • ustore4u.com",
                style = MaterialTheme.typography.labelSmall,
                color = ObsidianBlack.copy(alpha = 0.55f),
                letterSpacing = 0.5.sp
            )
        }
    }
}

data class OnboardingPageData(
    val title: String,
    val subtitle: String,
    val imageUrl: String
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = listOf(
        OnboardingPageData(
            title = "1000+ Styles of Genuine Watches",
            subtitle = "Explore top collections from Titan, Fastrack, Sonata, Casio, and handcrafted wall clocks for your home.",
            imageUrl = "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=1000&auto=format&fit=crop&q=80"
        ),
        OnboardingPageData(
            title = "⚡ 2-Hour Express Delivery",
            subtitle = "Enjoy rapid 2-hour doorstep delivery within a 10 km radius of Kukatpally, plus fast Pan-India shipping.",
            imageUrl = "https://images.unsplash.com/photo-1547996160-71dfabbce5ed?w=1000&auto=format&fit=crop&q=80"
        ),
        OnboardingPageData(
            title = "100% Authentic & 7-Day Exchange",
            subtitle = "Official brand warranties on all watches, straightforward 7-day exchange policy, and secure UPI or COD checkout.",
            imageUrl = "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=1000&auto=format&fit=crop&q=80"
        )
    )

    var currentPage by remember { mutableIntStateOf(0) }
    val page = pages[currentPage]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("onboarding_screen")
    ) {
        // Hero Background Photo with Vignette Gradient
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(page.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = page.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark Luxury Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ObsidianBlack.copy(alpha = 0.4f),
                            ObsidianBlack.copy(alpha = 0.7f),
                            ObsidianBlack.copy(alpha = 0.96f)
                        )
                    )
                )
        )

        // Top Skip Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, end = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            if (currentPage < pages.size - 1) {
                TextButton(
                    onClick = onFinishOnboarding,
                    modifier = Modifier.testTag("onboarding_skip_button")
                ) {
                    Text(
                        text = "Skip",
                        color = PureWhite.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        // Bottom Content Card
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.indices.forEach { index ->
                    val isSelected = index == currentPage
                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .width(if (isSelected) 24.dp else 8.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(if (isSelected) PureWhite else PureWhite.copy(alpha = 0.35f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = page.title.uppercase(),
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                letterSpacing = 1.2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = page.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = PureWhite.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Action Button
            Button(
                onClick = {
                    if (currentPage < pages.size - 1) {
                        currentPage++
                    } else {
                        onFinishOnboarding()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PureWhite,
                    contentColor = ObsidianBlack
                ),
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("onboarding_next_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (currentPage == pages.size - 1) "EXPLORE COLLECTION" else "CONTINUE",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
