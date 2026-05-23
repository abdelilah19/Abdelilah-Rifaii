package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.SpeedTestResult
import com.example.ui.theme.*
import com.example.ui.viewmodel.NetworkViewModel
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedTestScreen(
    viewModel: NetworkViewModel,
    onNavigateBack: () -> Unit
) {
    val isTesting by viewModel.isTestingSpeed.collectAsStateWithLifecycle()
    val testPhase by viewModel.speedTestPhase.collectAsStateWithLifecycle()
    val currentSpeed by viewModel.currentAnimSpeedValue.collectAsStateWithLifecycle()
    val finalDownload by viewModel.finalDownload.collectAsStateWithLifecycle()
    val finalUpload by viewModel.finalUpload.collectAsStateWithLifecycle()
    val finalPingVal by viewModel.finalPingVal.collectAsStateWithLifecycle()
    val graphPoints by viewModel.speedHistoryGraphPoints.collectAsStateWithLifecycle()
    val historyLogList by viewModel.speedHistory.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = viewModel.getString("speed_test").uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Speed indicator dial panel
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            RoundedCornerShape(20.dp)
                        )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // High-tech Dial Gauge
                        SpeedometerDial(currentSpeed = currentSpeed, maxTarget = 200f)

                        Spacer(modifier = Modifier.height(18.dp))

                        // Live status text indicator
                        AnimatedContent(
                            targetState = testPhase,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "testPhase"
                        ) { phase ->
                            val textStr = when (phase) {
                                "latency" -> viewModel.getString("ping").uppercase() + "..."
                                "download" -> viewModel.getString("download_speed").uppercase()
                                "upload" -> viewModel.getString("upload_speed").uppercase()
                                "finished" -> viewModel.getString("scan_done").uppercase()
                                else -> "IDLE"
                            }
                            Text(
                                text = textStr,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp,
                                    color = if (isTesting) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        // Giant Speed readout text
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        ) {
                            Text(
                                text = String.format(java.util.Locale.US, "%.1f", currentSpeed),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = viewModel.getString("mbps"),
                                style = MaterialTheme.typography.titleMedium,
                                color = ElectricBlue,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        // Summary readouts (Ping, Download, Upload)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SpeedMetricBox(
                                label = viewModel.getString("ping"),
                                value = if (finalPingVal > 0) "${finalPingVal}ms" else "--",
                                icon = Icons.Default.NetworkPing,
                                iconColor = CyberWarningOrange
                            )
                            SpeedMetricBox(
                                label = "DOWNLOAD",
                                value = if (finalDownload > 0) String.format(java.util.Locale.US, "%.1f", finalDownload) + " " + viewModel.getString("mbps") else "--",
                                icon = Icons.Default.ArrowDownward,
                                iconColor = ElectricBlue
                            )
                            SpeedMetricBox(
                                label = "UPLOAD",
                                value = if (finalUpload > 0) String.format(java.util.Locale.US, "%.1f", finalUpload) + " " + viewModel.getString("mbps") else "--",
                                icon = Icons.Default.ArrowUpward,
                                iconColor = CyberSuccessGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Large Action Button
                        Button(
                            onClick = { viewModel.runSpeedTest() },
                            enabled = !isTesting,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricBlueSecondary,
                                contentColor = Color.White,
                                disabledContainerColor = ElectricBlueSecondary.copy(alpha = 0.25f),
                                disabledContentColor = Color.White.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("run_test_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (isTesting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = viewModel.getString("testing"))
                                } else {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = viewModel.getString("start_test").uppercase(),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Real-time Waveform Canvas Chart
            if (graphPoints.isNotEmpty() || isTesting) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                RoundedCornerShape(16.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "BANDWIDTH FLUCTUATION GRAPH",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = ElectricBlue
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            // Live Graphics Drawing
                            SpeedSplineGraph(pointsList = graphPoints, maxVal = 200f)
                        }
                    }
                }
            }

            // Historical Logs Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = viewModel.getString("test_history").uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = ElectricBlue
                        )
                    )

                    if (historyLogList.isNotEmpty()) {
                        TextButton(
                            onClick = { viewModel.clearHistory() },
                            colors = ButtonDefaults.textButtonColors(contentColor = CyberErrorRed)
                        ) {
                            Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = viewModel.getString("clear_history"))
                        }
                    }
                }
            }

            // History Log items
            if (historyLogList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = viewModel.getString("no_history"),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(historyLogList) { log ->
                    HistoryResultCard(log = log, viewModel = viewModel)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SpeedometerDial(currentSpeed: Float, maxTarget: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = (currentSpeed / maxTarget).coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "pointer_rotation"
    )

    Canvas(
        modifier = Modifier
            .size(200.dp)
            .padding(10.dp)
    ) {
        val sizeSide = size.width
        val center = size.center
        val scopeRadius = sizeSide / 2f
        val startSweepAngle = 135f
        val totalSweepAngle = 270f

        // 1. Draw dial background arc
        drawArc(
            color = Color(0xFF1E293B),
            startAngle = startSweepAngle,
            sweepAngle = totalSweepAngle,
            useCenter = false,
            style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
        )

        // 2. Draw sweeping colored gradient arc
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    ElectricBlueSecondary.copy(alpha = 0.1f),
                    ElectricBlue,
                    CyberSuccessGreen
                )
            ),
            startAngle = startSweepAngle,
            sweepAngle = totalSweepAngle * animatedProgress,
            useCenter = false,
            style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
        )

        // 3. Tick Marks
        val totalTickMarksCount = 13
        for (i in 0 until totalTickMarksCount) {
            val angleDegree = startSweepAngle + (totalSweepAngle / (totalTickMarksCount - 1)) * i
            val angleRadian = Math.toRadians(angleDegree.toDouble())
            
            val outerX = center.x + scopeRadius * cos(angleRadian).toFloat()
            val outerY = center.y + scopeRadius * sin(angleRadian).toFloat()
            
            val tickLength = if (i % 3 == 0) 12.dp.toPx() else 6.dp.toPx()
            val tickWidth = if (i % 3 == 0) 3.dp.toPx() else 1.5.dp.toPx()
            val innerX = center.x + (scopeRadius - tickLength) * cos(angleRadian).toFloat()
            val innerY = center.y + (scopeRadius - tickLength) * sin(angleRadian).toFloat()

            drawLine(
                color = if (angleDegree <= startSweepAngle + (totalSweepAngle * animatedProgress)) ElectricBlue else Color(0xFF475569),
                start = Offset(innerX, innerY),
                end = Offset(outerX, outerY),
                strokeWidth = tickWidth
            )
        }

        // 4. Pointer needle
        val targetPointerAngleOffset = startSweepAngle + totalSweepAngle * animatedProgress
        rotate(degrees = targetPointerAngleOffset - 90f, pivot = center) {
            val needlePath = Path().apply {
                moveTo(center.x - 4.dp.toPx(), center.y)
                lineTo(center.x, center.y - (scopeRadius - 18.dp.toPx()))
                lineTo(center.x + 4.dp.toPx(), center.y)
                close()
            }
            drawPath(
                path = needlePath,
                color = ElectricBlue
            )
        }

        // Center solid hub
        drawCircle(
            color = CyberDarkBg,
            radius = 12.dp.toPx(),
            center = center
        )
        drawCircle(
            color = ElectricBlue,
            radius = 6.dp.toPx(),
            center = center
        )
    }
}

@Composable
fun SpeedSplineGraph(pointsList: List<Float>, maxVal: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(Color(0xFF0F111A), RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp, horizontal = 12.dp)
    ) {
        if (pointsList.size < 2) return@Canvas
        val canvasWidth = size.width
        val canvasHeight = size.height

        val stepX = canvasWidth / (pointsList.size - 1)
        val splinePath = Path()
        val fillPath = Path()

        pointsList.forEachIndexed { idx, value ->
            val ratioY = (value / maxVal).coerceIn(0f, 1f)
            val coordinateX = idx * stepX
            val coordinateY = canvasHeight - (ratioY * canvasHeight)

            if (idx == 0) {
                splinePath.moveTo(coordinateX, coordinateY)
                fillPath.moveTo(coordinateX, canvasHeight)
                fillPath.lineTo(coordinateX, coordinateY)
            } else {
                // Draw curve connection
                val prevRatioY = (pointsList[idx - 1] / maxVal).coerceIn(0f, 1f)
                val prevX = (idx - 1) * stepX
                val prevY = canvasHeight - (prevRatioY * canvasHeight)

                splinePath.cubicTo(
                    prevX + stepX / 2f, prevY,
                    coordinateX - stepX / 2f, coordinateY,
                    coordinateX, coordinateY
                )
                fillPath.cubicTo(
                    prevX + stepX / 2f, prevY,
                    coordinateX - stepX / 2f, coordinateY,
                    coordinateX, coordinateY
                )
            }

            if (idx == pointsList.size - 1) {
                fillPath.lineTo(coordinateX, canvasHeight)
                fillPath.close()
            }
        }

        // Render filled gradient
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    ElectricBlue.copy(alpha = 0.35f),
                    Color.Transparent
                )
            )
        )

        // Render line stroke
        drawPath(
            path = splinePath,
            color = ElectricBlue,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun SpeedMetricBox(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

@Composable
fun HistoryResultCard(
    log: SpeedTestResult,
    viewModel: NetworkViewModel
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ElectricBlue.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NetworkCheck,
                    contentDescription = null,
                    tint = ElectricBlueSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (log.wifiSsid.isNotEmpty()) log.wifiSsid else "Al Rifai Network",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
                Text(
                    text = log.getFormattedDate(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "DL",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp
                    )
                    Text(
                        text = String.format(java.util.Locale.US, "%.1f", log.downloadSpeed),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black, color = ElectricBlue)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "UL",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp
                    )
                    Text(
                        text = String.format(java.util.Locale.US, "%.1f", log.uploadSpeed),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black, color = CyberSuccessGreen)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "PING",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp
                    )
                    Text(
                        text = "${log.ping}ms",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = CyberWarningOrange)
                    )
                }
            }
        }
    }
}
