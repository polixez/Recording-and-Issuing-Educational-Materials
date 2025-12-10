package com.example.domain.repository.impl

import com.example.database.tables.AssignmentsTable
import com.example.database.tables.GroupMembershipsTable
import com.example.database.tables.MaterialsTable
import com.example.database.tables.UsersTable
import com.example.database.tables.toAssignment
import com.example.database.tables.toMaterial
import com.example.database.tables.toUser
import com.example.domain.model.Assignment
import com.example.domain.model.AssignmentStatus
import com.example.domain.model.AssignmentWithRelations
import com.example.domain.model.StudentAssignmentsReport
import com.example.domain.repository.AssignmentRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class AssignmentRepositoryExposed : AssignmentRepository {
    override fun getAll(): List<Assignment> = transaction {
        AssignmentsTable.selectAll().map { it.toAssignment() }
    }

    override fun getByStudentId(studentId: Int): List<Assignment> = transaction {
        AssignmentsTable
            .select { AssignmentsTable.studentId eq studentId }
            .map { it.toAssignment() }
    }

    override fun getById(id: Int): Assignment? = transaction {
        AssignmentsTable.select { AssignmentsTable.id eq id }.singleOrNull()?.toAssignment()
    }

    override fun create(materialId: Int, studentId: Int, status: AssignmentStatus, dueDate: LocalDate?): Assignment = transaction {
        val id = AssignmentsTable.insert {
            it[this.materialId] = materialId
            it[this.studentId] = studentId
            it[this.status] = status.name
            it[this.dueDate] = dueDate
        }[AssignmentsTable.id]

        Assignment(
            id = id,
            materialId = materialId,
            studentId = studentId,
            status = status,
            dueDate = dueDate
        )
    }

    override fun update(assignment: Assignment) {
        transaction {
            AssignmentsTable.update({ AssignmentsTable.id eq assignment.id }) {
                it[materialId] = assignment.materialId
                it[studentId] = assignment.studentId
                it[status] = assignment.status.name
                it[dueDate] = assignment.dueDate
            }
        }
    }

    override fun findWithFilters(
        status: AssignmentStatus?,
        groupId: Long?,
        sortAscending: Boolean
    ): List<AssignmentWithRelations> = transaction {
        val joinedTables = AssignmentsTable
            .join(
                MaterialsTable,
                JoinType.INNER,
                additionalConstraint = { AssignmentsTable.materialId eq MaterialsTable.id }
            )
            .join(
                UsersTable,
                JoinType.INNER,
                additionalConstraint = { AssignmentsTable.studentId eq UsersTable.id }
            )

        val query = joinedTables.selectAll().apply {
            status?.let { andWhere { AssignmentsTable.status eq it.name } }
            groupId?.let { gid ->
                andWhere {
                    exists(
                        GroupMembershipsTable.select {
                            (GroupMembershipsTable.groupId eq gid.toInt()) and
                                (GroupMembershipsTable.studentId eq AssignmentsTable.studentId)
                        }
                    )
                }
            }
        }.orderBy(AssignmentsTable.dueDate to if (sortAscending) SortOrder.ASC else SortOrder.DESC)

        query.map { row ->
            AssignmentWithRelations(
                assignment = row.toAssignment(),
                material = row.toMaterial(),
                student = row.toUser()
            )
        }
    }

    override fun getStudentAssignmentsReport(
        studentId: Int,
        today: LocalDate
    ): StudentAssignmentsReport = transaction {
        val rows = AssignmentsTable
            .join(
                MaterialsTable,
                JoinType.INNER,
                additionalConstraint = { AssignmentsTable.materialId eq MaterialsTable.id }
            )
            .join(
                UsersTable,
                JoinType.INNER,
                additionalConstraint = { AssignmentsTable.studentId eq UsersTable.id }
            )
            .select { AssignmentsTable.studentId eq studentId }
            .orderBy(AssignmentsTable.dueDate to SortOrder.ASC)
            .toList()

        val assignments = rows.map { row ->
            AssignmentWithRelations(
                assignment = row.toAssignment(),
                material = row.toMaterial(),
                student = row.toUser()
            )
        }

        val overdueCount = assignments.count { relation ->
            relation.assignment.dueDate?.let { due ->
                due.isBefore(today) && relation.assignment.status != AssignmentStatus.COMPLETED
            } ?: false
        }

        StudentAssignmentsReport(assignments = assignments, overdueCount = overdueCount)
    }
}
