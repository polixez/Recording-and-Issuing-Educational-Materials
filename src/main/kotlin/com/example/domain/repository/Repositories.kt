package com.example.domain.repository

import com.example.domain.repository.impl.AssignmentRepositoryExposed
import com.example.domain.repository.impl.CommentRepositoryExposed
import com.example.domain.repository.impl.GroupRepositoryExposed
import com.example.domain.repository.impl.MaterialRepositoryExposed
import com.example.domain.repository.impl.UserRepositoryExposed

object Repositories {
    /**
     * Единая точка создания репозиториев для DI/фасада в роутингах.
     */
    val materialRepository: MaterialRepository = MaterialRepositoryExposed()
    val assignmentRepository: AssignmentRepository = AssignmentRepositoryExposed()
    val userRepository: UserRepository = UserRepositoryExposed()
    val groupRepository: GroupRepository = GroupRepositoryExposed()
    val commentRepository: CommentRepository = CommentRepositoryExposed()
}
