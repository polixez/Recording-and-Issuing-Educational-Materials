package com.example.domain.repository.impl

import com.example.database.tables.GroupMembershipsTable
import com.example.database.tables.GroupsTable
import com.example.database.tables.UsersTable
import com.example.database.tables.toGroup
import com.example.database.tables.toUser
import com.example.domain.model.Group
import com.example.domain.model.User
import com.example.domain.model.UserRole
import com.example.domain.repository.GroupRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class GroupRepositoryExposed : GroupRepository {
    override fun getAll(): List<Group> = transaction {
        GroupsTable.selectAll().map { it.toGroup() }
    }

    override fun getById(id: Int): Group? = transaction {
        GroupsTable.select { GroupsTable.id eq id }.singleOrNull()?.toGroup()
    }

    override fun create(name: String): Group = transaction {
        val id = GroupsTable.insert {
            it[this.name] = name
        }[GroupsTable.id]

        Group(id = id, name = name)
    }

    override fun delete(id: Int) {
        transaction {
            GroupMembershipsTable.deleteWhere { groupId eq id }
            GroupsTable.deleteWhere { GroupsTable.id eq id }
        }
    }

    override fun getMembers(groupId: Int): List<User> = transaction {
        UsersTable
            .select {
                (UsersTable.role eq UserRole.STUDENT.name) and exists(
                    GroupMembershipsTable.select {
                        (GroupMembershipsTable.groupId eq groupId) and (GroupMembershipsTable.studentId eq UsersTable.id)
                    }
                )
            }
            .map { it.toUser() }
    }

    override fun addStudentToGroup(groupId: Int, studentId: Int) {
        transaction {
            GroupMembershipsTable.insertIgnore {
                it[this.groupId] = groupId
                it[this.studentId] = studentId
            }
        }
    }

    override fun removeStudentFromGroup(groupId: Int, studentId: Int) {
        transaction {
            GroupMembershipsTable.deleteWhere {
                (GroupMembershipsTable.groupId eq groupId) and (GroupMembershipsTable.studentId eq studentId)
            }
        }
    }

    override fun getGroupsForStudent(studentId: Int): List<Group> = transaction {
        GroupsTable
            .select {
                exists(
                    GroupMembershipsTable.select {
                        (GroupMembershipsTable.studentId eq studentId) and (GroupMembershipsTable.groupId eq GroupsTable.id)
                    }
                )
            }
            .map { it.toGroup() }
    }
}
