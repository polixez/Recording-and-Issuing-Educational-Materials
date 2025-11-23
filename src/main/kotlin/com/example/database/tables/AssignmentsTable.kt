package com.example.database.tables

import com.example.domain.model.Assignment
import com.example.domain.model.AssignmentStatus
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object AssignmentsTable : Table("assignments") {
    val id = integer("id").autoIncrement()
    val materialId = integer("material_id").references(MaterialsTable.id)
    val studentId = integer("student_id").references(UsersTable.id)
    val status = varchar("status", 50)

    override val primaryKey = PrimaryKey(id)
}

fun ResultRow.toAssignment(): Assignment =
    Assignment(
        id = this[AssignmentsTable.id],
        materialId = this[AssignmentsTable.materialId],
        studentId = this[AssignmentsTable.studentId],
        status = AssignmentStatus.valueOf(this[AssignmentsTable.status])
    )
