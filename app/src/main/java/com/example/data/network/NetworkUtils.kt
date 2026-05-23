package com.example.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import kotlin.system.measureTimeMillis

object NetworkUtils {

    fun getRouterIp(context: Context): String {
        try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val dhcpInfo = wifiManager?.dhcpInfo
            if (dhcpInfo != null && dhcpInfo.gateway != 0) {
                return formatIpAddress(dhcpInfo.gateway)
            }
        } catch (e: Exception) {
            Log.e("NetworkUtils", "Error getting gateway", e)
        }
        
        // Fallback checks
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val element = interfaces.nextElement()
                val addresses = element.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (!addr.isLoopbackAddress && addr.isSiteLocalAddress) {
                        val parts = addr.hostAddress.split(".")
                        if (parts.size == 4) {
                            // Guessed Gateway IP
                            return "${parts[0]}.${parts[1]}.${parts[2]}.1"
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("NetworkUtils", "Error guessing gateway", e)
        }
        return "192.168.1.1"
    }

    fun getLocalIpAddress(context: Context): String {
        try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val ipAddress = wifiManager?.connectionInfo?.ipAddress
            if (ipAddress != null && ipAddress != 0) {
                return formatIpAddress(ipAddress)
            }
        } catch (e: Exception) {
            Log.e("NetworkUtils", "Error standard wireless local ip", e)
        }

        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val element = interfaces.nextElement()
                val addresses = element.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (!addr.isLoopbackAddress && addr is InetAddress) {
                        val ip = addr.hostAddress ?: ""
                        if (!ip.contains(":") && ip != "127.0.0.1") { // IPv4 only
                            return ip
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("NetworkUtils", "Error fetching from Interfaces", e)
        }
        return "127.0.0.1"
    }

    private fun formatIpAddress(ip: Int): String {
        return String.format(
            java.util.Locale.US,
            "%d.%d.%d.%d",
            ip and 0xff,
            ip shr 8 and 0xff,
            ip shr 16 and 0xff,
            ip shr 24 and 0xff
        )
    }

    fun getWifiSsid(context: Context): String {
        try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val info = wifiManager?.connectionInfo
            if (info != null) {
                val ssid = info.ssid
                if (ssid != null && ssid != "<unknown ssid>" && ssid != "0x") {
                    return ssid.trim('"')
                }
            }
        } catch (e: Exception) {
            Log.e("NetworkUtils", "Error SSId fetch", e)
        }
        return "Al Rifai WiFi"
    }

    fun getWifiRssi(context: Context): Int {
        try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val info = wifiManager?.connectionInfo
            if (info != null) {
                return info.rssi
            }
        } catch (e: Exception) {
            Log.e("NetworkUtils", "Error signal rssi", e)
        }
        return -60
    }

    fun getWifiSignalPercentage(rssi: Int): Int {
        return when {
            rssi >= -40 -> 100
            rssi <= -100 -> 0
            else -> ((rssi - (-100)) * 5 / 3).coerceIn(0, 100)
        }
    }

    fun getConnectionType(context: Context): String {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return "Offline"
        val activeNet = cm.activeNetwork ?: return "Offline"
        val caps = cm.getNetworkCapabilities(activeNet) ?: return "Offline"
        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular Mobile"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet Network"
            else -> "VPN / Other"
        }
    }

    fun checkInternetAccess(): ConnectivityState {
        return try {
            // Check real internet via high availability DNS or host socket
            val socket = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)
            val timeoutMs = 1500
            val time = measureTimeMillis {
                socket.connect(socketAddress, timeoutMs)
                socket.close()
            }
            if (time < 300) {
                ConnectivityState.OPERATIONAL
            } else {
                ConnectivityState.CONNECTED_WEAK
            }
        } catch (e: Exception) {
            ConnectivityState.ROUTER_ONLY
        }
    }

    fun pingHost(host: String): PingResult {
        return try {
            val cleanHost = host.trim().replace("https://", "").replace("http://", "").split("/")[0]
            val address = InetAddress.getByName(cleanHost)
            val startTime = System.currentTimeMillis()
            val reachable = address.isReachable(1500)
            val duration = System.currentTimeMillis() - startTime
            if (reachable) {
                PingResult(true, duration, address.hostAddress ?: "")
            } else {
                // Secondary check using custom socket
                val socket = Socket()
                val elapsed = measureTimeMillis {
                    socket.connect(InetSocketAddress(address, 80), 1000)
                    socket.close()
                }
                PingResult(true, elapsed, address.hostAddress ?: "")
            }
        } catch (e: Exception) {
            PingResult(false, -1, "", e.message ?: "Failed target lookup")
        }
    }

    fun checkDns(domain: String): DnsResult {
        return try {
            val cleanDomain = domain.trim().replace("https://", "").replace("http://", "").split("/")[0]
            val startTime = System.currentTimeMillis()
            val addresses = InetAddress.getAllByName(cleanDomain)
            val duration = System.currentTimeMillis() - startTime
            val ipList = addresses.mapNotNull { it.hostAddress }
            DnsResult(true, ipList, duration)
        } catch (e: Exception) {
            DnsResult(false, emptyList(), -1, e.message ?: "DNS Resolve failed")
        }
    }

    fun checkPort(ip: String, port: Int): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(ip, port), 400)
            socket.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}

enum class ConnectivityState {
    OPERATIONAL,
    CONNECTED_WEAK,
    ROUTER_ONLY,
    OFFLINE
}

data class PingResult(
    val success: Boolean,
    val rtt: Long,
    val resolvedIp: String,
    val errorMessage: String = ""
)

data class DnsResult(
    val success: Boolean,
    val ipAddresses: List<String>,
    val rtt: Long,
    val errorMessage: String = ""
)
