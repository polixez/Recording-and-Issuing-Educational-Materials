package com.example.database.tables

import com.example.domain.model.User
import com.example.domain.model.UserRole
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val role = varchar("role", 50)

    override val primaryKey = PrimaryKey(id)
}

fun ResultRow.toUser(): User =
    User(
        id = this[UsersTable.id],
        name = this[UsersTable.name],
        role = UserRole.valueOf(this[UsersTable.role])
    )
