package com.example.domain.model

import java.time.LocalDateTime

data class Comment(
    val id: Int,
    val assignmentId: Int,
    val authorId: Int,
    val text: String,
    val createdAt: LocalDateTime
)
