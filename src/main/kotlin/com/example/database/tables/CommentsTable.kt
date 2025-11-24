package com.example.database.tables

import com.example.domain.model.Comment
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object CommentsTable : Table("comments") {
    val id = integer("id").autoIncrement()
    val assignmentId = integer("assignment_id").references(AssignmentsTable.id)
    val authorId = integer("author_id").references(UsersTable.id)
    val text = text("text")
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

fun ResultRow.toComment(): Comment =
    Comment(
        id = this[CommentsTable.id],
        assignmentId = this[CommentsTable.assignmentId],
        authorId = this[CommentsTable.authorId],
        text = this[CommentsTable.text],
        createdAt = this[CommentsTable.createdAt]
    )
