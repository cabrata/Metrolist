/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest

/**
 * Apple Music style "living" artwork background.
 * A 24px thumbnail upscaled with bilinear filtering already looks like a soft mesh gradient,
 * so no RenderEffect blur is needed and it works on every API level.
 * Rotation runs in the draw phase only and stops when [animate] is false to save battery.
 */
@Composable
fun AppleMeshBackground(
    thumbnailUrl: String?,
    animate: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val spin = remember { Animatable(0f) }
    LaunchedEffect(animate) {
        if (animate) {
            spin.animateTo(
                spin.value + 360f,
                infiniteRepeatable(tween(durationMillis = 90_000, easing = LinearEasing)),
            )
        }
    }

    AnimatedContent(
        targetState = thumbnailUrl,
        transitionSpec = { fadeIn(tween(900)).togetherWith(fadeOut(tween(900))) },
        modifier = modifier.fillMaxSize().background(Color.Black),
        label = "appleMeshBackground",
    ) { url ->
        Box(Modifier.fillMaxSize()) {
            if (url != null) {
                val request = remember(url) {
                    ImageRequest.Builder(context).data(url).size(24, 24).build()
                }
                // Two counter-rotating, oversized copies give the slow "flowing colors" look.
                AsyncImage(
                    model = request,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    filterQuality = FilterQuality.High,
                    modifier = Modifier.fillMaxSize().graphicsLayer {
                        rotationZ = spin.value
                        scaleX = 2.4f
                        scaleY = 2.4f
                    },
                )
                AsyncImage(
                    model = request,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    filterQuality = FilterQuality.High,
                    modifier = Modifier.fillMaxSize().graphicsLayer {
                        rotationZ = -spin.value * 2f + 90f
                        scaleX = 1.8f
                        scaleY = 1.8f
                        alpha = 0.55f
                    },
                )
            }
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.25f), Color.Black.copy(alpha = 0.45f)),
                    ),
                ),
            )
        }
    }
}
