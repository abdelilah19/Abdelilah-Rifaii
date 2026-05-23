package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Device
import com.example.data.model.DeviceType
import com.example.ui.theme.*
import com.example.ui.viewmodel.NetworkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkScannerScreen(
    viewModel: NetworkViewModel,
    onNavigateBack: () -> Unit
) {
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val progress by viewModel.scanProgress.collectAsStateWithLifecycle()
    val devices by viewModel.scannedDevices.collectAsStateWithLifecycle()
    val localIp by viewModel.localIp.collectAsStateWithLifecycle()

    val subnetVal = if (localIp.contains(".")) {
        localIp.substringBeforeLast(".") + ".X"
    } else {
        "192.168.1.X"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = viewModel.getString("net_scan").uppercase(),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Scanner Control Panel Card
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
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = viewModel.getString("scan_network"),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = viewModel.getString("target_subnet"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = subnetVal,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ElectricBlue)
                                )
                            }
                        }

                        // Play scan trigger button
                        Button(
                            onClick = { viewModel.startNetworkScan() },
                            enabled = !isScanning,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricBlueSecondary,
                                contentColor = Color.White,
                                disabledContainerColor = ElectricBlueSecondary.copy(alpha = 0.2f),
                                disabledContentColor = Color.White.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.testTag("trigger_scan_btn")
                        ) {
                            if (isScanning) {
                                Text(text = viewModel.getString("scanning"))
                            } else {
                                Icon(imageVector = Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = viewModel.getString("ping_start").uppercase())
                            }
                        }
                    }

                    if (isScanning) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = ElectricBlue,
                                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricBlue
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total dynamic list feedback message
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = viewModel.getString("scan_devices").uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = ElectricBlue
                    )
                )

                Text(
                    text = "${devices.size} ${viewModel.getString("active_devices")}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Results lazy list framework
            if (devices.isEmpty() && !isScanning) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DevicesOther,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "DÉBUTEZ L'ANALYSE LOCAL",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Text(
                            text = "Lancez un scan pour découvrir l'ensemble des ports et adresses IP actives sur votre segment réseau.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(top = 4.dp, start = 12.dp, end = 12.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(devices) { device ->
                        DeviceItemCard(device = device, viewModel = viewModel)
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceItemCard(
    device: Device,
    viewModel: NetworkViewModel
) {
    val config = when (device.type) {
        DeviceType.ROUTER -> Pair(Icons.Default.Router, ElectricBlueSecondary)
        DeviceType.PHONE -> Pair(Icons.Default.PhoneIphone, ElectricBlue)
        DeviceType.COMPUTER -> Pair(Icons.Default.Laptop, Color(0xFFA855F7))
        DeviceType.SMART_TV -> Pair(Icons.Default.Tv, Color(0xFFEC4899))
        DeviceType.PRINTER -> Pair(Icons.Default.Print, Color(0xFFF59E0B))
        DeviceType.IOT -> Pair(Icons.Default.SettingsInputAntenna, CyberSuccessGreen)
        DeviceType.UNKNOWN -> Pair(Icons.Default.Devices, Color(0xFF64748B))
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (device.isRouter) ElectricBlue.copy(alpha = 0.25f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon backdrop circle frame
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(config.second.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = config.first,
                    contentDescription = null,
                    tint = config.second,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Text detail segment
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = device.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                    if (device.isRouter) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(ElectricBlue.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ROUTER",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp, color = ElectricBlue)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row {
                    Text(
                        text = device.ipAddress,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = device.macAddress,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                Text(
                    text = "Mfg: " + device.manufacturer,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            // Latency feedback block
            if (device.latencyMs >= 0) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "RTT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp
                    )
                    Text(
                        text = "${device.latencyMs}ms",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (device.latencyMs < 12) CyberSuccessGreen else if (device.latencyMs < 60) CyberWarningOrange else CyberErrorRed
                        )
                    )
                }
            }
        }
    }
}
