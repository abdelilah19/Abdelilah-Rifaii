package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.network.ConnectivityState
import com.example.ui.theme.*
import com.example.ui.viewmodel.NetworkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NetworkViewModel,
    onNavigateToRoute: (String) -> Unit
) {
    val wifiSsid by viewModel.wifiSsid.collectAsStateWithLifecycle()
    val localIp by viewModel.localIp.collectAsStateWithLifecycle()
    val gatewayIp by viewModel.gatewayIp.collectAsStateWithLifecycle()
    val connectionType by viewModel.connectionType.collectAsStateWithLifecycle()
    val wifiSignalStrength by viewModel.wifiSignalStrength.collectAsStateWithLifecycle()
    val wifiRssi by viewModel.wifiRssi.collectAsStateWithLifecycle()
    val connectionStatus by viewModel.connectionStatus.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = viewModel.getString("app_name").uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = ElectricBlue
                            )
                        )
                        Text(
                            text = viewModel.getString("subtitle"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshNetworkProfile() },
                        modifier = Modifier.testTag("refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Button",
                            tint = ElectricBlue
                        )
                    }
                }
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
            // General status ribbon
            item {
                Spacer(modifier = Modifier.height(8.dp))
                StatusRibbonCard(connectionStatus, viewModel)
            }

            // Connection metrics outline card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(ElegantBlueGradientStart, ElegantBlueGradientEnd)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SettingsInputAntenna,
                                contentDescription = "SSID",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = viewModel.getString("wifi_name").uppercase(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            )
                        }

                        Text(
                            text = wifiSsid,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(bottom = 14.dp)
                        )

                        Divider(color = Color.White.copy(alpha = 0.15f))

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = viewModel.getString("connection_type").uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 0.5.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = connectionType,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = viewModel.getString("signals").uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 0.5.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$wifiSignalStrength% ($wifiRssi dBm)",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (wifiSignalStrength > 75) CyberSuccessGreen else if (wifiSignalStrength > 40) CyberWarningOrange else CyberErrorRed
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // IPS details (Gateway IP / Local IP) side by side
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IpDashboardCard(
                        title = viewModel.getString("gateway_ip"),
                        ip = gatewayIp,
                        icon = Icons.Default.Router,
                        iconColor = ElectricBlueSecondary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToRoute("router") }
                    )

                    IpDashboardCard(
                        title = viewModel.getString("local_ip"),
                        ip = localIp,
                        icon = Icons.Default.Laptop,
                        iconColor = ElectricBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToRoute("scanner") }
                    )
                }
            }

            // Quick Operations Grid title
            item {
                Text(
                    text = viewModel.getString("net_tools").uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = ElectricBlue
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Dynamic grid list
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GridDashboardItem(
                            title = viewModel.getString("router_mgmt"),
                            subtitle = viewModel.getString("router_details"),
                            icon = Icons.Default.LockOpen,
                            tag = "router_card",
                            color = ElectricBlueSecondary,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToRoute("router") }
                        )
                        GridDashboardItem(
                            title = viewModel.getString("speed_test"),
                            subtitle = "Speedometer",
                            icon = Icons.Default.Speed,
                            tag = "speedtest_card",
                            color = ElectricBlue,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToRoute("speedtest") }
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GridDashboardItem(
                            title = viewModel.getString("net_scan"),
                            subtitle = "ARP Scan",
                            icon = Icons.Default.Radar,
                            tag = "scanner_card",
                            color = CyberSuccessGreen,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToRoute("scanner") }
                        )
                        GridDashboardItem(
                            title = viewModel.getString("ai_diag"),
                            subtitle = "AI Technician Partner",
                            icon = Icons.Default.Psychology,
                            tag = "ai_card",
                            color = Color(0xFFA855F7),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToRoute("ai_chat") }
                        )
                    }
                }
            }

            // Developed by attribution ribbon
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = viewModel.getString("developed_by"),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.9f)
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = viewModel.getString("author_title"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(top = 2.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun StatusRibbonCard(
    connectionStatus: ConnectivityState,
    viewModel: NetworkViewModel
) {
    val config = when (connectionStatus) {
        ConnectivityState.OPERATIONAL -> Triple(
            viewModel.getString("status_active"),
            CyberSuccessGreen,
            Icons.Default.CloudQueue
        )
        ConnectivityState.CONNECTED_WEAK -> Triple(
            "Signal WiFi faible - Diagnostics recommandés",
            CyberWarningOrange,
            Icons.Default.CloudDownload
        )
        ConnectivityState.ROUTER_ONLY -> Triple(
            viewModel.getString("status_router_only"),
            CyberWarningOrange,
            Icons.Default.PortableWifiOff
        )
        ConnectivityState.OFFLINE -> Triple(
            viewModel.getString("status_no_internet"),
            CyberErrorRed,
            Icons.Default.CloudOff
        )
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = config.second.copy(alpha = 0.08f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                config.second.copy(alpha = 0.25f),
                RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(config.second, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = viewModel.getString("connection_status"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = config.first,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Icon(
                imageVector = config.third,
                contentDescription = "Status icon",
                tint = config.second,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun IpDashboardCard(
    title: String,
    ip: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            Text(
                text = ip,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
fun GridDashboardItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tag: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .testTag(tag)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
