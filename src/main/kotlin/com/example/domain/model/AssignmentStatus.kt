package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class AssignmentStatus {
    ASSIGNED,
    DOWNLOADED,
    COMPLETED
}
