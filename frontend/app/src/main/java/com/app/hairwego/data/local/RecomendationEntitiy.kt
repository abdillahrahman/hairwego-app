package com.app.hairwego.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recommendations",
    foreignKeys = [ForeignKey(
        entity = FaceScanEntity::class,
        parentColumns = ["face_scan_id"],
        childColumns = ["face_scan_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["face_scan_id"])]
)
data class RecommendationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "face_scan_id")
    val faceScanId: String,

    @ColumnInfo(name = "haircut_name")
    val haircutName: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "image")
    val image: String
)
