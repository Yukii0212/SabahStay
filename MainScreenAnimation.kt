import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class MainScreenAnimation : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimationScreen()
        }
    }
}

@Composable
fun AnimationScreen() {
    var step by remember { mutableStateOf(0) }

    // Controls animation timing
    LaunchedEffect(Unit) {
        delay(700)  // Show first screen for 0.7s
        step = 1
        delay(700)  // Show second screen for 0.7s
        step = 2
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1E3A)), // Dark blue background
        contentAlignment = Alignment.Center
    ) {
        Crossfade(targetState = step, animationSpec = tween(700)) { screen ->
            when (screen) {
                0 -> CloudsScreen()
                1 -> LogoScreen()
                2 -> WelcomeScreen()
            }
        }
    }
}

@Composable
fun CloudsScreen() {
    Image(
        painter = painterResource(id = R.drawable.clouds), // Replace with actual cloud image
        contentDescription = "Clouds",
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun LogoScreen() {
    Image(
        painter = painterResource(id = R.drawable.mountain_logo), // Replace with mountain logo
        contentDescription = "Mountain Logo",
        modifier = Modifier.size(150.dp)
    )
}

@Composable
fun WelcomeScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.mountain_logo), // Same mountain logo
            contentDescription = "Mountain Logo",
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "SABAH STAY",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Welcome to Sabah",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
