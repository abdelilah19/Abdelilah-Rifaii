package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeedTestDao {
    @Query("SELECT * FROM speed_test_results ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<SpeedTestResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: SpeedTestResult)

    @Query("DELETE FROM speed_test_results")
    suspend fun clearHistory()
}
