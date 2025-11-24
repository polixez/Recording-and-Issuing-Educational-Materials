package com.example.database.tables

import com.example.domain.model.Group
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object GroupsTable : Table("groups") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)

    override val primaryKey = PrimaryKey(id)
}

fun ResultRow.toGroup(): Group =
    Group(
        id = this[GroupsTable.id],
        name = this[GroupsTable.name]
    )
