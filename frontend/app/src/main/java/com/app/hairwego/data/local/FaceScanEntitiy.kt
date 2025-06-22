package com.app.hairwego.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "face_scans")
data class FaceScanEntity(
    @PrimaryKey
    @ColumnInfo(name = "face_scan_id")
    val faceScanId: String,

    @ColumnInfo(name = "face_shape")
    val faceShape: String,

    @ColumnInfo(name = "scan_date")
    val scanDate: String,

    @ColumnInfo(name = "scan_image")
    val scanImage: String,

    @ColumnInfo(name = "scan_image_cropped")
    val scanImageCropped: String
)

