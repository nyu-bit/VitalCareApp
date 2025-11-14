package cl.duoc.app.ui.animations

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*

/**
 * Componente para renderizar animaciones Lottie
 * HU-08: Animaciones visuales
 */
@Composable
fun LottieStatus(
    modifier: Modifier = Modifier,
    animationType: LottieAnimationType = LottieAnimationType.HEARTBEAT,
    iterations: Int = LottieConstants.IterateForever,
    speed: Float = 1f
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url(animationType.url)
    )
    
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
        speed = speed,
        restartOnPlay = true
    )
    
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.size(100.dp)
    )
}

/**
 * Tipos de animaciones Lottie disponibles
 */
enum class LottieAnimationType(val url: String) {
    HEARTBEAT("https://lottie.host/4e6b70a6-1db0-4f1b-8b84-0a3e3e8c5f5e/rk6PXOqhzg.json"),
    SUCCESS("https://lottie.host/7b5a1f5c-7b9a-4b1a-8b9a-4f5c5f5c5f5c/7b5a1f5c.json"),
    LOADING("https://lottie.host/embed/f4b5c5f5-7b9a-4b1a-8b9a-4f5c5f5c5f5c/7b5a1f5c.json"),
    HEALTH("https://lottie.host/4e6b70a6-1db0-4f1b-8b84-0a3e3e8c5f5e/rk6PXOqhzg.json")
}

/**
 * Animación Lottie con fallback a URL pública
 * Usa una animación de heartbeat de LottieFiles
 */
@Composable
fun LottieHeartbeat(
    modifier: Modifier = Modifier
) {
    // Animación de heartbeat desde LottieFiles (URL pública)
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url("https://lottie.host/4e6b70a6-1db0-4f1b-8b84-0a3e3e8c5f5e/rk6PXOqhzg.json")
    )
    
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.size(120.dp)
    )
}

/**
 * Animación Lottie de éxito
 */
@Composable
fun LottieSuccess(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url("https://lottie.host/embed/d5d3b3a0-17b0-4c3f-9b4a-3f5c5f5c5f5c/d5d3b3a0.json")
    )
    
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )
    
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.size(100.dp)
    )
}

/**
 * Vista previa de animación Lottie
 */
@Preview(showBackground = true)
@Composable
fun LottieStatusPreview() {
    LottieHeartbeat()
}
