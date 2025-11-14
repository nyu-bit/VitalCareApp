package cl.duoc.app.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.tween

/**
 * Transiciones de navegación reutilizables
 * HU-08: Animaciones visuales
 */
object Transitions {
    
    /**
     * Duración estándar de las animaciones
     */
    const val ANIMATION_DURATION = 400
    const val FAST_ANIMATION_DURATION = 200
    
    /**
     * Transición de entrada desde la derecha (para navegación hacia adelante)
     */
    fun slideInFromRight(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(durationMillis = ANIMATION_DURATION)
        ) + fadeIn(
            animationSpec = tween(durationMillis = ANIMATION_DURATION)
        )
    }
    
    /**
     * Transición de salida hacia la izquierda (para navegación hacia adelante)
     */
    fun slideOutToLeft(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(durationMillis = ANIMATION_DURATION)
        ) + fadeOut(
            animationSpec = tween(durationMillis = ANIMATION_DURATION)
        )
    }
    
    /**
     * Transición de entrada desde la izquierda (para navegación hacia atrás)
     */
    fun slideInFromLeft(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(durationMillis = ANIMATION_DURATION)
        ) + fadeIn(
            animationSpec = tween(durationMillis = ANIMATION_DURATION)
        )
    }
    
    /**
     * Transición de salida hacia la derecha (para navegación hacia atrás)
     */
    fun slideOutToRight(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(durationMillis = ANIMATION_DURATION)
        ) + fadeOut(
            animationSpec = tween(durationMillis = ANIMATION_DURATION)
        )
    }
    
    /**
     * Fade simple para transiciones suaves
     */
    fun fadeTransition(): EnterTransition {
        return fadeIn(
            animationSpec = tween(durationMillis = FAST_ANIMATION_DURATION)
        )
    }
    
    /**
     * Fade out simple
     */
    fun fadeOutTransition(): ExitTransition {
        return fadeOut(
            animationSpec = tween(durationMillis = FAST_ANIMATION_DURATION)
        )
    }
    
    /**
     * Expandir verticalmente con fade
     */
    fun expandVerticallyWithFade(): EnterTransition {
        return expandVertically(
            animationSpec = tween(durationMillis = ANIMATION_DURATION)
        ) + fadeIn(
            animationSpec = tween(durationMillis = ANIMATION_DURATION)
        )
    }
    
    /**
     * Contraer verticalmente con fade
     */
    fun shrinkVerticallyWithFade(): ExitTransition {
        return shrinkVertically(
            animationSpec = tween(durationMillis = ANIMATION_DURATION)
        ) + fadeOut(
            animationSpec = tween(durationMillis = ANIMATION_DURATION)
        )
    }
}
