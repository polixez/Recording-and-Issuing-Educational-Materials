package com.example.domain.repository

import com.example.domain.repository.impl.AssignmentRepositoryExposed
import com.example.domain.repository.impl.MaterialRepositoryExposed
import com.example.domain.repository.impl.UserRepositoryExposed

object Repositories {
    val materialRepository: MaterialRepository = MaterialRepositoryExposed()
    val assignmentRepository: AssignmentRepository = AssignmentRepositoryExposed()
    val userRepository: UserRepository = UserRepositoryExposed()
}
