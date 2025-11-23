package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Material(
    val id: Int,
    val title: String,
    val description: String,
    val fileUrl: String? = null
)
