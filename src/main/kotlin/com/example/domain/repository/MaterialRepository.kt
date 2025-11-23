package com.example.domain.repository

import com.example.domain.model.Material

interface MaterialRepository {
    fun getAll(): List<Material>
    fun getById(id: Int): Material?
    fun create(title: String, description: String, fileUrl: String?): Material
}
