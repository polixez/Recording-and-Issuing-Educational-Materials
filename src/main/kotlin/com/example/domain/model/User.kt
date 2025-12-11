package com.example.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class User(
    val id: Int,
    val name: String,
    val role: UserRole,
    @Transient
    val passwordHash: String = ""
)

@Serializable
enum class UserRole {
    TEACHER,
    STUDENT
}
