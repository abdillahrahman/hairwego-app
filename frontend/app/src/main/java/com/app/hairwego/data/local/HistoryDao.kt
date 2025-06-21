package com.app.hairwego.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaceScan(scan: FaceScanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendations(recommendations: List<RecommendationEntity>)

    @Transaction
    @Query("SELECT * FROM face_scans ORDER BY scan_date DESC")
    fun getAllHistory(): Flow<List<FaceScanWithRecommendations>>

    @Transaction
    @Query("SELECT * FROM face_scans WHERE face_scan_id = :faceScanId")
    suspend fun getHistoryDetail(faceScanId: String): FaceScanWithRecommendations?
}