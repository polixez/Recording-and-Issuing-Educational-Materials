package com.example.database

import com.example.database.tables.AssignmentsTable
import com.example.database.tables.CommentsTable
import com.example.database.tables.GroupMembershipsTable
import com.example.database.tables.GroupsTable
import com.example.database.tables.MaterialsTable
import com.example.database.tables.UsersTable
import com.example.domain.model.UserRole
import com.example.security.PasswordService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    private const val DEFAULT_TEACHER_PASSWORD = "teacher123"
    private const val DEFAULT_STUDENT_PASSWORD = "student123"

    fun init() {
        Database.connect(
            url = "jdbc:sqlite:materials.db",
            driver = "org.sqlite.JDBC"
        )

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                UsersTable,
                MaterialsTable,
                AssignmentsTable,
                GroupsTable,
                GroupMembershipsTable,
                CommentsTable
            )
            seedInitialUsers()
        }
    }

    private fun seedInitialUsers() {
        val defaultTeacherHash = PasswordService.hash(DEFAULT_TEACHER_PASSWORD)
        val defaultStudentHash = PasswordService.hash(DEFAULT_STUDENT_PASSWORD)

        if (UsersTable.selectAll().count() == 0L) {
            UsersTable.insert {
                it[name] = "Teacher1"
                it[role] = UserRole.TEACHER.name
                it[passwordHash] = defaultTeacherHash
            }
            UsersTable.insert {
                it[name] = "Student1"
                it[role] = UserRole.STUDENT.name
                it[passwordHash] = defaultStudentHash
            }
        } else {
            ensurePasswords(defaultTeacherHash, defaultStudentHash)
        }
    }

    private fun ensurePasswords(defaultTeacherHash: String, defaultStudentHash: String) {
        UsersTable
            .slice(UsersTable.id, UsersTable.role, UsersTable.passwordHash)
            .selectAll()
            .filter { row -> row[UsersTable.passwordHash].isBlank() }
            .forEach { row ->
                val role = runCatching { UserRole.valueOf(row[UsersTable.role]) }.getOrNull()
                val hash = when (role) {
                    UserRole.TEACHER -> defaultTeacherHash
                    UserRole.STUDENT -> defaultStudentHash
                    else -> PasswordService.hash(DEFAULT_STUDENT_PASSWORD)
                }
                UsersTable.update({ UsersTable.id eq row[UsersTable.id] }) {
                    it[passwordHash] = hash
                }
            }
    }
}
