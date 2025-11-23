package com.example.domain.repository

import com.example.domain.model.Assignment
import com.example.domain.model.AssignmentStatus

interface AssignmentRepository {
    fun getByStudentId(studentId: Int): List<Assignment>
    fun getById(id: Int): Assignment?
    fun create(materialId: Int, studentId: Int, status: AssignmentStatus): Assignment
    fun update(assignment: Assignment)
}

class InMemoryAssignmentRepository : AssignmentRepository {
    private val assignments = mutableListOf(
        Assignment(
            id = 1,
            materialId = 1,
            studentId = 100,
            status = AssignmentStatus.ASSIGNED
        ),
        Assignment(
            id = 2,
            materialId = 2,
            studentId = 100,
            status = AssignmentStatus.DOWNLOADED
        )
    )

    private var nextId = (assignments.maxOfOrNull { it.id } ?: 0) + 1

    override fun getByStudentId(studentId: Int): List<Assignment> =
        assignments.filter { it.studentId == studentId }

    override fun getById(id: Int): Assignment? = assignments.firstOrNull { it.id == id }

    override fun create(materialId: Int, studentId: Int, status: AssignmentStatus): Assignment {
        val assignment = Assignment(
            id = nextId++,
            materialId = materialId,
            studentId = studentId,
            status = status
        )
        assignments += assignment
        return assignment
    }

    override fun update(assignment: Assignment) {
        val index = assignments.indexOfFirst { it.id == assignment.id }
        if (index != -1) {
            assignments[index] = assignment
        }
    }
}
