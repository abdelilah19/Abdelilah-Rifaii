package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    appName: String,
    onSplashFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val scaleValue by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val alphaValue by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )

    // Animated wifi radar rings pulse
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAnimation"
    )
    val radarAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2600) // Beautiful 2.6 seconds delay
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CyberDarkBg,
                        Color(0xFF0F121C),
                        CyberSurface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .scale(scaleValue)
                    .alpha(alphaValue)
            ) {
                // Expanding glowing radar rings
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(radarPulse)
                        .alpha(radarAlpha)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    ElectricBlue.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            ),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
                
                // Outer Ring border
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(scaleValue)
                        .background(
                            Brush.linearGradient(
                                listOf(ElectricBlue.copy(alpha = 0.15f), ElectricBlueSecondary.copy(alpha = 0.15f))
                            ),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )

                // High-fidelity networking dual icons layered
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Router,
                        contentDescription = "Router Icon",
                        tint = ElectricBlueSecondary,
                        modifier = Modifier
                            .size(38.dp)
                            .padding(end = 4.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Wifi Icon",
                        tint = ElectricBlue,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Application Brand name
            Text(
                text = "TECHNICIEN",
                style = MaterialTheme.typography.labelLarge.copy(
                    letterSpacing = 6.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricBlue
                ),
                modifier = Modifier.alpha(alphaValue)
            )

            Text(
                text = "ABDELMOTALEB RIFAI",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    color = Color.White
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .alpha(alphaValue)
            )

            Text(
                text = "NETWORK SUITE PRO",
                style = MaterialTheme.typography.titleSmall.copy(
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Light,
                    color = Color(0xFF38BDF8)
                ),
                modifier = Modifier.alpha(alphaValue)
            )
        }

        // Developer attribution bottom anchor alignment
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 54.dp)
                .alpha(alphaValue),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(2.dp)
                    .background(ElectricBlue.copy(alpha = 0.5f))
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Developed by Technicien Abdelmotaleb Rifai",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                ),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Secured, Local, Expert diagnostics",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Light,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
