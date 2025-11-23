package com.example.database

import com.example.database.tables.AssignmentsTable
import com.example.database.tables.MaterialsTable
import com.example.database.tables.UsersTable
import com.example.domain.model.UserRole
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(
            url = "jdbc:sqlite:materials.db",
            driver = "org.sqlite.JDBC"
        )

        transaction {
            SchemaUtils.create(UsersTable, MaterialsTable, AssignmentsTable)
            seedInitialUsers()
        }
    }

    private fun seedInitialUsers() {
        if (UsersTable.selectAll().count() == 0L) {
            UsersTable.insert {
                it[name] = "Teacher1"
                it[role] = UserRole.TEACHER.name
            }
            UsersTable.insert {
                it[name] = "Student1"
                it[role] = UserRole.STUDENT.name
            }
        }
    }
}
