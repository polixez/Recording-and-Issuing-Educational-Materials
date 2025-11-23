package com.example.database.tables

import com.example.domain.model.Material
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object MaterialsTable : Table("materials") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val description = text("description")
    val fileUrl = varchar("file_url", 1024).nullable()

    override val primaryKey = PrimaryKey(id)
}

fun ResultRow.toMaterial(): Material =
    Material(
        id = this[MaterialsTable.id],
        title = this[MaterialsTable.title],
        description = this[MaterialsTable.description],
        fileUrl = this[MaterialsTable.fileUrl]
    )
