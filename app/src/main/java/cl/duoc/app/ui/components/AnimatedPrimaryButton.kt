package cl.duoc.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Botón principal con animaciones de escala y color
 * HU-08: Animaciones visuales
 */
@Composable
fun AnimatedPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    // Detectar si el botón está presionado
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animar la escala cuando se presiona
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "scale"
    )
    
    // Animar el color del botón
    val backgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            isPressed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(durationMillis = 150),
        label = "backgroundColor"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            ),
        enabled = enabled,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Vista previa del botón animado
 */
@Preview(showBackground = true)
@Composable
fun AnimatedPrimaryButtonPreview() {
    MaterialTheme {
        AnimatedPrimaryButton(
            text = "Iniciar Sesión",
            onClick = { }
        )
    }
}

/**
 * Vista previa del botón deshabilitado
 */
@Preview(showBackground = true)
@Composable
fun AnimatedPrimaryButtonDisabledPreview() {
    MaterialTheme {
        AnimatedPrimaryButton(
            text = "Guardando...",
            onClick = { },
            enabled = false
        )
    }
}
