package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val role: UserRole
)

@Serializable
enum class UserRole {
    TEACHER,
    STUDENT
}
