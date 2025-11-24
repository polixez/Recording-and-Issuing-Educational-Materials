package com.example.database.tables

import com.example.domain.model.GroupMembership
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object GroupMembershipsTable : Table("group_memberships") {
    val groupId = integer("group_id").references(GroupsTable.id)
    val studentId = integer("student_id").references(UsersTable.id)

    override val primaryKey = PrimaryKey(groupId, studentId)
}

fun ResultRow.toGroupMembership(): GroupMembership =
    GroupMembership(
        groupId = this[GroupMembershipsTable.groupId],
        studentId = this[GroupMembershipsTable.studentId]
    )
