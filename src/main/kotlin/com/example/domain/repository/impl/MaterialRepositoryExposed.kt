package com.example.domain.repository.impl

import com.example.database.tables.MaterialsTable
import com.example.database.tables.toMaterial
import com.example.domain.model.Material
import com.example.domain.repository.MaterialRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class MaterialRepositoryExposed : MaterialRepository {
    override fun getAll(): List<Material> = transaction {
        MaterialsTable.selectAll().map { it.toMaterial() }
    }

    override fun getById(id: Int): Material? = transaction {
        MaterialsTable.select { MaterialsTable.id eq id }.singleOrNull()?.toMaterial()
    }

    override fun create(title: String, description: String, fileUrl: String?): Material = transaction {
        val id = MaterialsTable.insert {
            it[this.title] = title
            it[this.description] = description
            it[this.fileUrl] = fileUrl?.ifBlank { null }
        }[MaterialsTable.id]

        Material(
            id = id,
            title = title,
            description = description,
            fileUrl = fileUrl?.ifBlank { null }
        )
    }
}
