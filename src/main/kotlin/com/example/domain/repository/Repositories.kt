package com.example.domain.repository

object Repositories {
    val materialRepository: MaterialRepository = InMemoryMaterialRepository()
    val assignmentRepository: AssignmentRepository = InMemoryAssignmentRepository()
    val userRepository: UserRepository = InMemoryUserRepository()
}
