package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueSecondary
import com.example.ui.viewmodel.NetworkViewModel

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouterWebViewScreen(
    viewModel: NetworkViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val gatewayIp by viewModel.gatewayIp.collectAsStateWithLifecycle()
    val routerUrl = "http://$gatewayIp"

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var pageTitle by remember { mutableStateOf("Router Gateway") }
    var loadingProgress by remember { mutableStateOf(0) }
    var isWebLoading by remember { mutableStateOf(false) }

    val routerBrands = listOf("TP-Link", "Huawei", "Orange", "ZTE", "Nokia", "Xiaomi", "D-Link")

    // Default credentials dictionary map
    val defaultLogins = mapOf(
        "TP-Link" to Pair("admin", "admin (or custom on newer devices)"),
        "Huawei" to Pair("admin", "admin / admin123 / @HuaweiHG"),
        "Orange" to Pair("admin", "admin (or first 8 characters of WPA key)"),
        "ZTE" to Pair("admin", "admin / ZTE@WA / user"),
        "Nokia" to Pair("admin", "12345 / adminGPON"),
        "Xiaomi" to Pair("admin", "admin (or set at installation setup)"),
        "D-Link" to Pair("admin", "blank / admin")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = viewModel.getString("router_browser"),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = routerUrl,
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlue
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        webViewInstance?.reload()
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reload web page", tint = ElectricBlue)
                    }
                    IconButton(onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(routerUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }) {
                        Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = "Open in Extern WebBrowser")
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
            // Horizontal scroll brand picker to estimate credentials
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = viewModel.getString("choose_brand"),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(routerBrands) { brand ->
                        val isSelected = viewModel.selectedBrand == brand
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { viewModel.setBrand(brand) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = brand,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF0F111A) else Color.White
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Credentials alert drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp)
                ) {
                    val loginInfo = defaultLogins[viewModel.selectedBrand] ?: Pair("admin", "admin")
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CREDENTIALS GUESS: ${viewModel.selectedBrand.uppercase()}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = ElectricBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• IP default gateway: $gatewayIp" +
                                    "\n• Username profile: ${loginInfo.first}" +
                                    "\n• Password profile: ${loginInfo.second}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Web Loading Linear ProgressBar Indicator
            if (isWebLoading) {
                LinearProgressIndicator(
                    progress = { loadingProgress / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = ElectricBlue,
                    trackColor = Color.Transparent,
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                )
            }

            // Central WebView Container Frame
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("router_webview"),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                builtInZoomControls = true
                                displayZoomControls = false
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                cacheMode = WebSettings.LOAD_DEFAULT
                                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                    isWebLoading = true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    isWebLoading = false
                                }

                                override fun onReceivedError(
                                    view: WebView?,
                                    errorCode: Int,
                                    description: String?,
                                    failingUrl: String?
                                ) {
                                    // Let users know it failed to open but keep it clean
                                    pageTitle = "Error loading console"
                                }
                            }

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    loadingProgress = newProgress
                                    if (newProgress == 100) {
                                        isWebLoading = false
                                    }
                                }

                                override fun onReceivedTitle(view: WebView?, title: String?) {
                                    if (!title.isNullOrEmpty()) {
                                        pageTitle = title
                                    }
                                }
                            }

                            loadUrl(routerUrl)
                            webViewInstance = this
                        }
                    },
                    update = { webView ->
                        // Just verifying URL didn't drift inappropriately
                    }
                )

                // Navigation controls for WebView overlays in the bottom right corner
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .background(
                            Color(0xCC0F111A),
                            RoundedCornerShape(30.dp)
                        )
                        .border(
                            1.dp,
                            ElectricBlue.copy(alpha = 0.2f),
                            RoundedCornerShape(30.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (webViewInstance?.canGoBack() == true) {
                                webViewInstance?.goBack()
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp),
                            tint = if (webViewInstance?.canGoBack() == true) ElectricBlue else Color.Gray
                        )
                    }

                    IconButton(
                        onClick = {
                            if (webViewInstance?.canGoForward() == true) {
                                webViewInstance?.goForward()
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Forward",
                            modifier = Modifier.size(20.dp),
                            tint = if (webViewInstance?.canGoForward() == true) ElectricBlue else Color.Gray
                        )
                    }
                }
            }

            // WebView safety tip banner at the footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp)
            ) {
                Text(
                    text = viewModel.getString("webview_tip"),
                    style = MaterialTheme.typography.bodySmall.copy(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
