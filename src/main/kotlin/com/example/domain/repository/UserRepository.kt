package com.example.domain.repository

import com.example.domain.model.User
import com.example.domain.model.UserRole

interface UserRepository {
    fun getAllStudents(): List<User>
    fun getById(id: Int): User?
    fun getByName(name: String): User?
    fun create(name: String, role: UserRole): User
}
