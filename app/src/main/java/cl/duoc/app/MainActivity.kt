package cl.duoc.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cl.duoc.app.ui.HomeScreen
import cl.duoc.app.ui.HomeViewModel

class MainActivity : AppCompatActivity() {
    
    private val viewModel: HomeViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeScreen(
                viewModel = viewModel,
                onPrimaryAction = { viewModel.inc() },
                onSecondaryAction = { viewModel.reset() }
            )
        }
    }
}