package com.example.domain.repository

import com.example.domain.model.Group
import com.example.domain.model.User

interface GroupRepository {
    fun getAll(): List<Group>
    fun getById(id: Int): Group?
    fun create(name: String): Group
    fun delete(id: Int)

    fun getMembers(groupId: Int): List<User>
    fun addStudentToGroup(groupId: Int, studentId: Int)
    fun removeStudentFromGroup(groupId: Int, studentId: Int)
    fun getGroupsForStudent(studentId: Int): List<Group>
}
