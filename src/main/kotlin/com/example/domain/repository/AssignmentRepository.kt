package com.example.domain.repository

import com.example.domain.model.Assignment
import com.example.domain.model.AssignmentStatus

interface AssignmentRepository {
    fun getByStudentId(studentId: Int): List<Assignment>
    fun getById(id: Int): Assignment?
    fun create(materialId: Int, studentId: Int, status: AssignmentStatus): Assignment
    fun update(assignment: Assignment)
}
