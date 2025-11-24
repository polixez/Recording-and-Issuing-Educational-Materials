package com.example.domain.repository.impl

import com.example.database.tables.CommentsTable
import com.example.database.tables.toComment
import com.example.domain.model.Comment
import com.example.domain.repository.CommentRepository
import java.time.LocalDateTime
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class CommentRepositoryExposed : CommentRepository {
    override fun getByAssignmentId(assignmentId: Int): List<Comment> = transaction {
        CommentsTable
            .select { CommentsTable.assignmentId eq assignmentId }
            .orderBy(CommentsTable.createdAt to SortOrder.ASC)
            .map { it.toComment() }
    }

    override fun create(assignmentId: Int, authorId: Int, text: String): Comment = transaction {
        val now = LocalDateTime.now()
        val id = CommentsTable.insert {
            it[this.assignmentId] = assignmentId
            it[this.authorId] = authorId
            it[this.text] = text
            it[this.createdAt] = now
        }[CommentsTable.id]

        Comment(
            id = id,
            assignmentId = assignmentId,
            authorId = authorId,
            text = text,
            createdAt = now
        )
    }

    override fun delete(commentId: Int) {
        transaction {
            CommentsTable.deleteWhere { CommentsTable.id eq commentId }
        }
    }
}
