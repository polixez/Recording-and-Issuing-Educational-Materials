package com.example.domain.repository.impl

import com.example.database.tables.AssignmentsTable
import com.example.database.tables.toAssignment
import com.example.domain.model.Assignment
import com.example.domain.model.AssignmentStatus
import com.example.domain.repository.AssignmentRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class AssignmentRepositoryExposed : AssignmentRepository {
    override fun getByStudentId(studentId: Int): List<Assignment> = transaction {
        AssignmentsTable
            .select { AssignmentsTable.studentId eq studentId }
            .map { it.toAssignment() }
    }

    override fun getById(id: Int): Assignment? = transaction {
        AssignmentsTable.select { AssignmentsTable.id eq id }.singleOrNull()?.toAssignment()
    }

    override fun create(materialId: Int, studentId: Int, status: AssignmentStatus): Assignment = transaction {
        val id = AssignmentsTable.insert {
            it[this.materialId] = materialId
            it[this.studentId] = studentId
            it[this.status] = status.name
        }[AssignmentsTable.id]

        Assignment(
            id = id,
            materialId = materialId,
            studentId = studentId,
            status = status
        )
    }

    override fun update(assignment: Assignment) {
        transaction {
            AssignmentsTable.update({ AssignmentsTable.id eq assignment.id }) {
                it[materialId] = assignment.materialId
                it[studentId] = assignment.studentId
                it[status] = assignment.status.name
            }
        }
    }
}
