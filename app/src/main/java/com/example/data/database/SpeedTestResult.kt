package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "speed_test_results")
data class SpeedTestResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val downloadSpeed: Double, // in Mbps
    val uploadSpeed: Double,   // in Mbps
    val ping: Int,             // in ms
    val wifiSsid: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
