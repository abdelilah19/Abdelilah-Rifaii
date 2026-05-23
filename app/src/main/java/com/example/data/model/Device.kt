package com.example.data.model

data class Device(
    val ipAddress: String,
    val macAddress: String = "00:00:00:00:00:00",
    val name: String,
    val type: DeviceType,
    val manufacturer: String = "Unknown",
    val latencyMs: Long = -1,
    val isRouter: Boolean = false
)

enum class DeviceType {
    ROUTER,
    PHONE,
    COMPUTER,
    SMART_TV,
    PRINTER,
    IOT,
    UNKNOWN
}
