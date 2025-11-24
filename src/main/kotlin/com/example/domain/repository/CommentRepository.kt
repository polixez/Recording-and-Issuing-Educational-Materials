package com.example.domain.repository

import com.example.domain.model.Comment

interface CommentRepository {
    fun getByAssignmentId(assignmentId: Int): List<Comment>
    fun create(assignmentId: Int, authorId: Int, text: String): Comment
    fun delete(commentId: Int)
}
