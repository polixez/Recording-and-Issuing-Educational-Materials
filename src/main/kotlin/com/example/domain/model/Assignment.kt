package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Assignment(
    val id: Int,
    val materialId: Int,
    val studentId: Int,
    val status: AssignmentStatus
)
