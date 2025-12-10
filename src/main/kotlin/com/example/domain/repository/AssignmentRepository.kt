package com.example.domain.repository

import com.example.domain.model.Assignment
import com.example.domain.model.AssignmentStatus
import com.example.domain.model.AssignmentWithRelations
import com.example.domain.model.StudentAssignmentsReport
import java.time.LocalDate

interface AssignmentRepository {
    fun getAll(): List<Assignment>
    fun getByStudentId(studentId: Int): List<Assignment>
    fun getById(id: Int): Assignment?
    fun create(materialId: Int, studentId: Int, status: AssignmentStatus, dueDate: LocalDate? = null): Assignment
    fun update(assignment: Assignment)
    fun findWithFilters(status: AssignmentStatus?, groupId: Long?, sortAscending: Boolean): List<AssignmentWithRelations>
    fun getStudentAssignmentsReport(studentId: Int, today: LocalDate = LocalDate.now()): StudentAssignmentsReport
}
