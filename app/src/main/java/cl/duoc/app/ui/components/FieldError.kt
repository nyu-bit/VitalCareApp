package cl.duoc.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Mensaje de error animado con AnimatedVisibility
 * HU-08: Animaciones visuales
 */
@Composable
fun FieldError(
    errorMessage: String,
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible && errorMessage.isNotEmpty(),
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { -it / 2 }
        ),
        exit = fadeOut() + slideOutVertically(
            targetOffsetY = { -it / 2 }
        ),
        modifier = modifier
    ) {
        Text(
            text = errorMessage,
            color = Color.Red,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
        )
    }
}

/**
 * Vista previa con error visible
 */
@Preview(showBackground = true)
@Composable
fun FieldErrorVisiblePreview() {
    MaterialTheme {
        FieldError(
            errorMessage = "El correo electrónico no es válido",
            visible = true
        )
    }
}

/**
 * Vista previa con error oculto
 */
@Preview(showBackground = true)
@Composable
fun FieldErrorHiddenPreview() {
    MaterialTheme {
        FieldError(
            errorMessage = "El correo electrónico no es válido",
            visible = false
        )
    }
}
