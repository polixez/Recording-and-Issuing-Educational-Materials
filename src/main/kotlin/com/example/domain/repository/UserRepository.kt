package com.example.domain.repository

import com.example.domain.model.User
import com.example.domain.model.UserRole

interface UserRepository {
    /**
     * Возвращает всех студентов.
     */
    fun getAllStudents(): List<User>

    /**
     * Ищет пользователя по id.
     */
    fun getById(id: Int): User?

    /**
     * Ищет пользователя по имени.
     */
    fun getByName(name: String): User?

    /**
     * Создаёт пользователя с уже посчитанным хешем пароля и ролью.
     */
    fun create(name: String, passwordHash: String, role: UserRole): User
}
