package com.example.domain.repository

import com.example.domain.model.Material

interface MaterialRepository {
    fun findAll(): List<Material>
    fun findById(id: Int): Material?
    fun add(material: Material): Material
    fun update(material: Material): Material?
    fun delete(id: Int): Boolean
}

class InMemoryMaterialRepository : MaterialRepository {
    private val materials = mutableListOf(
        Material(
            id = 1,
            title = "Алгебра: Введение",
            description = "Базовые понятия и упражнения для первых занятий",
            fileUrl = "https://example.org/materials/algebra-intro.pdf"
        ),
        Material(
            id = 2,
            title = "Геометрия: Теорема Пифагора",
            description = "Конспект и задачи для самостоятельной работы"
        )
    )

    override fun findAll(): List<Material> = materials.toList()

    override fun findById(id: Int): Material? = materials.firstOrNull { it.id == id }

    override fun add(material: Material): Material {
        materials += material
        return material
    }

    override fun update(material: Material): Material? {
        val index = materials.indexOfFirst { it.id == material.id }
        if (index == -1) return null
        materials[index] = material
        return material
    }

    override fun delete(id: Int): Boolean = materials.removeIf { it.id == id }
}
