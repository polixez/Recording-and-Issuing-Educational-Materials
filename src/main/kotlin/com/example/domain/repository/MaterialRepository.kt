package com.example.domain.repository

import com.example.domain.model.Material

interface MaterialRepository {
    fun getAll(): List<Material>
    fun getById(id: Int): Material?
    fun create(title: String, description: String, fileUrl: String?): Material
}

class InMemoryMaterialRepository : MaterialRepository {
    private val materials = mutableListOf(
        Material(
            id = 1,
            title = "\u041e\u0441\u043d\u043e\u0432\u044b \u0430\u043b\u0433\u0435\u0431\u0440\u044b",
            description = "\u0412\u0432\u0435\u0434\u0435\u043d\u0438\u0435 \u0432 \u0431\u0430\u0437\u043e\u0432\u044b\u0435 \u043e\u043f\u0435\u0440\u0430\u0446\u0438\u0438 \u0438 \u0437\u0430\u0434\u0430\u0447\u0438.",
            fileUrl = "https://example.org/materials/algebra-intro.pdf"
        ),
        Material(
            id = 2,
            title = "\u0410\u043b\u0433\u043e\u0440\u0438\u0442\u043c\u044b \u0438 \u0441\u0442\u0440\u0443\u043a\u0442\u0443\u0440\u044b \u0434\u0430\u043d\u043d\u044b\u0445",
            description = "\u041a\u0440\u0430\u0442\u043a\u0438\u0439 \u043a\u043e\u043d\u0441\u043f\u0435\u043a\u0442 \u043f\u043e \u043a\u043b\u044e\u0447\u0435\u0432\u044b\u043c \u0442\u0435\u043c\u0430\u043c."
        )
    )

    private var nextId = (materials.maxOfOrNull { it.id } ?: 0) + 1

    override fun getAll(): List<Material> = materials.toList()

    override fun getById(id: Int): Material? = materials.firstOrNull { it.id == id }

    override fun create(title: String, description: String, fileUrl: String?): Material {
        val material = Material(
            id = nextId++,
            title = title,
            description = description,
            fileUrl = fileUrl?.ifBlank { null }
        )
        materials += material
        return material
    }
}
