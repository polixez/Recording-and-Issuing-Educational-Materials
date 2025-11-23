package com.example.domain.repository

import com.example.domain.model.Assignment
import com.example.domain.model.AssignmentStatus

interface AssignmentRepository {
    fun findAll(): List<Assignment>
    fun findById(id: Int): Assignment?
    fun findByStudent(studentId: Int): List<Assignment>
    fun add(assignment: Assignment): Assignment
    fun updateStatus(id: Int, status: AssignmentStatus): Assignment?
}

class InMemoryAssignmentRepository : AssignmentRepository {
    private val assignments = mutableListOf(
        Assignment(
            id = 1,
            materialId = 1,
            studentId = 101,
            status = AssignmentStatus.ASSIGNED
        ),
        Assignment(
            id = 2,
            materialId = 2,
            studentId = 102,
            status = AssignmentStatus.DOWNLOADED
        )
    )

    override fun findAll(): List<Assignment> = assignments.toList()

    override fun findById(id: Int): Assignment? = assignments.firstOrNull { it.id == id }

    override fun findByStudent(studentId: Int): List<Assignment> =
        assignments.filter { it.studentId == studentId }

    override fun add(assignment: Assignment): Assignment {
        assignments += assignment
        return assignment
    }

    override fun updateStatus(id: Int, status: AssignmentStatus): Assignment? {
        val index = assignments.indexOfFirst { it.id == id }
        if (index == -1) return null
        val updated = assignments[index].copy(status = status)
        assignments[index] = updated
        return updated
    }
}
