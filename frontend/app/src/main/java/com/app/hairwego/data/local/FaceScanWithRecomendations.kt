package com.app.hairwego.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class FaceScanWithRecommendations(
    @Embedded val faceScan: FaceScanEntity,

    @Relation(
        parentColumn = "face_scan_id",
        entityColumn = "face_scan_id"
    )
    val recommendations: List<RecommendationEntity>
)
