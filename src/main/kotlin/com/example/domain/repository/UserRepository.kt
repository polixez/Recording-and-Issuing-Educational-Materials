package com.example.domain.repository

import com.example.domain.model.User
import com.example.domain.model.UserRole

interface UserRepository {
    fun findAll(): List<User>
    fun findById(id: Int): User?
    fun findByRole(role: UserRole): List<User>
    fun add(user: User): User
}

class InMemoryUserRepository : UserRepository {
    private val users = mutableListOf(
        User(id = 1, name = "Ирина Петрова", role = UserRole.TEACHER),
        User(id = 2, name = "Алексей Смирнов", role = UserRole.TEACHER),
        User(id = 101, name = "Студент А", role = UserRole.STUDENT),
        User(id = 102, name = "Студент Б", role = UserRole.STUDENT)
    )

    override fun findAll(): List<User> = users.toList()

    override fun findById(id: Int): User? = users.firstOrNull { it.id == id }

    override fun findByRole(role: UserRole): List<User> = users.filter { it.role == role }

    override fun add(user: User): User {
        users += user
        return user
    }
}
