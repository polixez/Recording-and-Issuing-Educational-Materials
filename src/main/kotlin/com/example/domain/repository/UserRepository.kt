package com.example.domain.repository

import com.example.domain.model.User
import com.example.domain.model.UserRole

interface UserRepository {
    fun getAllStudents(): List<User>
    fun getById(id: Int): User?
}

class InMemoryUserRepository : UserRepository {
    private val users = mutableListOf(
        User(id = 1, name = "\u041f\u0440\u0435\u043f\u043e\u0434\u0430\u0432\u0430\u0442\u0435\u043b\u044c 1", role = UserRole.TEACHER),
        User(id = 100, name = "\u0421\u0442\u0443\u0434\u0435\u043d\u0442 100", role = UserRole.STUDENT),
        User(id = 101, name = "\u0421\u0442\u0443\u0434\u0435\u043d\u0442 101", role = UserRole.STUDENT)
    )

    override fun getAllStudents(): List<User> = users.filter { it.role == UserRole.STUDENT }

    override fun getById(id: Int): User? = users.firstOrNull { it.id == id }
}
