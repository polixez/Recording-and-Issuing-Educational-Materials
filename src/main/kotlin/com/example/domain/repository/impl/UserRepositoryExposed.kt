package com.example.domain.repository.impl

import com.example.database.tables.UsersTable
import com.example.database.tables.toUser
import com.example.domain.model.User
import com.example.domain.model.UserRole
import com.example.domain.repository.UserRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryExposed : UserRepository {
    override fun getAllStudents(): List<User> = transaction {
        UsersTable
            .select { UsersTable.role eq UserRole.STUDENT.name }
            .map { it.toUser() }
    }

    override fun getById(id: Int): User? = transaction {
        UsersTable.select { UsersTable.id eq id }.singleOrNull()?.toUser()
    }

    override fun getByName(name: String): User? = transaction {
        UsersTable.select { UsersTable.name eq name }.singleOrNull()?.toUser()
    }

    override fun create(name: String, role: UserRole): User = transaction {
        val id = UsersTable.insert {
            it[this.name] = name
            it[this.role] = role.name
        }[UsersTable.id]

        User(
            id = id,
            name = name,
            role = role
        )
    }
}
