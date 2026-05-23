package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.NetworkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    viewModel: NetworkViewModel,
    onNavigateBack: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var activeToolTab by remember { mutableStateOf("ping") } // ping, dns, ports, signal

    val tabs = listOf(
        Triple("ping", viewModel.getString("ping_tool"), Icons.Default.NetworkPing),
        Triple("dns", viewModel.getString("dns_checker"), Icons.Default.Dns),
        Triple("ports", viewModel.getString("port_scanner"), Icons.Default.Security),
        Triple("signal", viewModel.getString("wifi_analyzer"), Icons.Default.WifiTethering)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = viewModel.getString("net_tools").uppercase(),
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
        ) {
            // Horizontal tool tabs selector
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabs) { tab ->
                    val isSelected = activeToolTab == tab.first
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickableWithoutRipple {
                                activeToolTab = tab.first
                                keyboardController?.hide()
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = tab.third,
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFF0F111A) else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.second,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF0F111A) else Color.White
                                )
                            )
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

            // Tool active layout workspace
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                when (activeToolTab) {
                    "ping" -> PingToolLayout(viewModel = viewModel)
                    "dns" -> DnsToolLayout(viewModel = viewModel)
                    "ports" -> PortScannerToolLayout(viewModel = viewModel)
                    "signal" -> WifiSignalToolLayout(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun PingToolLayout(viewModel: NetworkViewModel) {
    val targetHost by viewModel.pingHostStr.collectAsStateWithLifecycle()
    val pingLogs by viewModel.pingResultsLog.collectAsStateWithLifecycle()
    val isProgress by viewModel.isPingInProgress.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = targetHost,
            onValueChange = { viewModel.pingHostStr.value = it },
            label = { Text("Adresse Hôte / IP") },
            placeholder = { Text(viewModel.getString("ping_placeholder")) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ping_host_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                focusedLabelColor = ElectricBlue
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Uri
            ),
            keyboardActions = KeyboardActions(
                onDone = { viewModel.runPing() }
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = { viewModel.runPing() },
            enabled = !isProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("ping_trigger_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlueSecondary)
        ) {
            if (isProgress) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            } else {
                Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = viewModel.getString("ping_start").uppercase())
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = viewModel.getString("ping_results").uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Vintage CLI terminal print logs box
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF030712), RoundedCornerShape(10.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (pingLogs.isEmpty()) {
                    item {
                        Text(
                            text = "${'$'} awaiting instruction...",
                            fontFamily = FontFamily.Monospace,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    items(pingLogs) { log ->
                        Text(
                            text = log,
                            fontFamily = FontFamily.Monospace,
                            color = if (log.contains("timeout")) CyberErrorRed else CyberSuccessGreen,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DnsToolLayout(viewModel: NetworkViewModel) {
    val targetDomain by viewModel.dnsHostStr.collectAsStateWithLifecycle()
    val dnsLogs by viewModel.dnsResultsLog.collectAsStateWithLifecycle()
    val isProgress by viewModel.isDnsInProgress.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = targetDomain,
            onValueChange = { viewModel.dnsHostStr.value = it },
            label = { Text("Domaine DNS (ex: cloudflare.com)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("dns_host_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                focusedLabelColor = ElectricBlue
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = { viewModel.runDnsLookup() },
            enabled = !isProgress,
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("dns_trigger_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlueSecondary)
        ) {
            if (isProgress) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            } else {
                Text(text = viewModel.getString("dns_check_btn").uppercase())
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF030712), RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (dnsLogs.isEmpty()) {
                    item {
                        Text(
                            text = "${'$'} ready for DNS lookup query.",
                            fontFamily = FontFamily.Monospace,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    items(dnsLogs) { log ->
                        Text(
                            text = log,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PortScannerToolLayout(viewModel: NetworkViewModel) {
    val targetIp by viewModel.portScanIpStr.collectAsStateWithLifecycle()
    val scanResults by viewModel.portScanResultsLog.collectAsStateWithLifecycle()
    val isProgress by viewModel.isPortScanInProgress.collectAsStateWithLifecycle()

    // Key port descriptive names
    val portDescriptions = mapOf(
        21 to "FTP (File Transfer)",
        22 to "SSH (Secure Shell Access)",
        23 to "Telnet (Remote CLI)",
        80 to "HTTP (Web Console Admin)",
        443 to "HTTPS (Secure Web Console)",
        8080 to "HTTP Alternate Server",
        8443 to "HTTPS Alternate Server"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = targetIp,
            onValueChange = { viewModel.portScanIpStr.value = it },
            label = { Text("Adresse IP Cible") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("port_target_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                focusedLabelColor = ElectricBlue
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = { viewModel.runPortScan() },
            enabled = !isProgress,
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("port_trigger_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlueSecondary)
        ) {
            if (isProgress) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            } else {
                Text(text = viewModel.getString("port_scan_btn").uppercase())
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = viewModel.getString("port_status").uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (scanResults.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucun port scanné. Lancez l'analyse ci-dessus.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(scanResults.toList()) { (port, isOpen) ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Port $port",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                )
                                Text(
                                    text = portDescriptions[port] ?: "Service",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(30.dp))
                                    .background(
                                        if (isOpen) CyberSuccessGreen.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.08f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isOpen) CyberSuccessGreen.copy(alpha = 0.3f) else Color.Red.copy(alpha = 0.2f),
                                        RoundedCornerShape(30.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isOpen) "OUVERT / OPEN" else "FERMÉ / CLOSED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOpen) CyberSuccessGreen else Color.Red
                                    ),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WifiSignalToolLayout(viewModel: NetworkViewModel) {
    val signalPercent by viewModel.wifiSignalStrength.collectAsStateWithLifecycle()
    val rssiVal by viewModel.wifiRssi.collectAsStateWithLifecycle()
    val ssidName by viewModel.wifiSsid.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = ssidName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = ElectricBlue)
                )
                Text(
                    text = viewModel.getString("wifi_strength") + "$signalPercent%",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                // dBm bar chart visualizations
                Row(
                    modifier = Modifier.height(130.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    val bars = listOf(-90, -80, -70, -60, -50, -40)
                    bars.forEach { barThreshold ->
                        val isActive = rssiVal >= barThreshold
                        val barHeightFactor = (barThreshold + 100) / 60.0f
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(barHeightFactor.coerceIn(0.1f, 1f))
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    if (isActive) ElectricBlue else Color.DarkGray.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "$rssiVal dBm",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, color = Color.White)
                )

                Text(
                    text = if (rssiVal >= -50) "EXCELLENT SIGNAL" else if (rssiVal >= -70) "STABLE SIGNAL" else "POOR WiFi LINK",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (rssiVal >= -60) CyberSuccessGreen else if (rssiVal >= -80) CyberWarningOrange else CyberErrorRed
                )
            }
        }
    }
}

// Utility extension to support click behavior without ripple if selected
private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = null,
    indication = null,
    onClick = onClick
)
