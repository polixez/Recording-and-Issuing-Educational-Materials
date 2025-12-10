package com.example.domain.model

data class AssignmentWithRelations(
    val assignment: Assignment,
    val material: Material,
    val student: User
)

data class StudentAssignmentsReport(
    val assignments: List<AssignmentWithRelations>,
    val overdueCount: Int
)
