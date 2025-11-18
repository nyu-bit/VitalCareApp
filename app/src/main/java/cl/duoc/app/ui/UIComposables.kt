package cl.duoc.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Extension function para animar entrada y salida
@Composable
fun Modifier.animateEnterExit(
    enter: EnterTransition = expandVertically(),
    exit: ExitTransition = shrinkVertically(),
    label: String = "AnimateEnterExit"
): Modifier = this

// Reimplementación de Preview anotación para compatibilidad
annotation class Preview(
    val name: String = "",
    val group: String = "",
    val apiLevel: Int = 31,
    val widthDp: Int = 412,
    val heightDp: Int = 732,
    val showBackground: Boolean = false,
    val backgroundColor: Long = 0xFFFFFFFF
)

