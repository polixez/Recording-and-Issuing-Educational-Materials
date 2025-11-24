package com.example.http

import com.example.UserSession
import com.example.domain.model.Assignment
import com.example.domain.model.AssignmentStatus
import com.example.domain.model.User
import com.example.domain.model.UserRole
import com.example.domain.repository.Repositories
import com.example.domain.repository.UserRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.html.FormMethod
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.button
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.hr
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.strong
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.textArea
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.title
import kotlinx.html.tr
import kotlinx.html.style

private const val TITLE_STUDENT = "Кабинет студента"
private const val TEXT_HEADER_MATERIAL = "Материал"
private const val TEXT_HEADER_DESCRIPTION = "Описание"
private const val TEXT_HEADER_STATUS = "Статус"
private const val TEXT_HEADER_DEADLINE = "Дедлайн"
private const val TEXT_HEADER_DETAILS = "Детали"
private const val TEXT_HEADER_ACTIONS = "Действия"
private const val TEXT_DOWNLOAD_FILE = "Скачать файл"
private const val TEXT_MARK_DOWNLOADED = "Отметить скачанным"
private const val TEXT_COMPLETE = "Отметить выполненным"
private const val TEXT_DONE = "Готово"
private const val TEXT_BACK_HOME = "На главную"
private const val TEXT_ASSIGNMENT_NOT_FOUND = "Назначение не найдено"
private const val TEXT_STATUS_ASSIGNED = "Назначен"
private const val TEXT_STATUS_DOWNLOADED = "Скачан"
private const val TEXT_STATUS_COMPLETED = "Выполнен"
private const val TEXT_LOGOUT = "Выйти"
private const val TEXT_OVERDUE = "(просрочено)"
private const val TEXT_OPEN_DETAILS = "Подробнее"
private const val TEXT_MY_GROUPS = "Мои группы"
private const val TEXT_NO_GROUPS = "Группы не назначены"
private const val TEXT_ASSIGNMENT_ID = "Назначение №"
private const val TEXT_ASSIGNMENT_STATUS = "Статус"
private const val TEXT_ASSIGNMENT_DUE = "Дедлайн"
private const val TEXT_COMMENTS = "Комментарии"
private const val TEXT_NO_COMMENTS = "Комментариев пока нет"
private const val TEXT_COMMENT_PLACEHOLDER = "Текст комментария"
private const val TEXT_COMMENT_SEND = "Отправить"
private const val TEXT_AUTHOR_TEACHER = "Преподаватель"
private const val TEXT_AUTHOR_STUDENT = "Студент"
private const val TEXT_BACK = "Назад"

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val commentDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

fun Route.studentRoutes() {
    val assignmentRepo = Repositories.assignmentRepository
    val materialRepo = Repositories.materialRepository
    val userRepo = Repositories.userRepository
    val groupRepo = Repositories.groupRepository
    val commentRepo = Repositories.commentRepository

    get("/student") {
        val student = call.requireStudent(userRepo) ?: return@get
        val assignments = assignmentRepo.getByStudentId(student.id)
        val groups = groupRepo.getGroupsForStudent(student.id)

        call.respondHtml {
            head { title { +TITLE_STUDENT } }
            body {
                h1 { +"$TITLE_STUDENT (${student.name})" }
                p {
                    span { +"$TEXT_MY_GROUPS: " }
                    if (groups.isEmpty()) {
                        span { +TEXT_NO_GROUPS }
                    } else {
                        span { +groups.joinToString { it.name } }
                    }
                }
                table {
                    thead {
                        tr {
                            th { +TEXT_HEADER_MATERIAL }
                            th { +TEXT_HEADER_DESCRIPTION }
                            th { +TEXT_HEADER_STATUS }
                            th { +TEXT_HEADER_DEADLINE }
                            th { +TEXT_HEADER_DETAILS }
                            th { +TEXT_HEADER_ACTIONS }
                        }
                    }
                    tbody {
                        assignments.forEach { assignment ->
                            val material = materialRepo.getById(assignment.materialId)
                            val overdue = isOverdue(assignment)
                            tr {
                                if (overdue) {
                                    attributes["style"] = "background-color:#ffe6e6;"
                                }
                                td { +(material?.title ?: "-") }
                                td { +(material?.description ?: "") }
                                td { +statusLabel(assignment.status) }
                                td {
                                    +(assignment.dueDate?.format(dateFormatter) ?: "-")
                                    if (overdue) {
                                        span {
                                            style = "color: red; margin-left: 6px;"
                                            +TEXT_OVERDUE
                                        }
                                    }
                                }
                                td {
                                    a(href = "/student/assignments/${assignment.id}") { +TEXT_OPEN_DETAILS }
                                }
                                td {
                                    material?.fileUrl?.takeIf { it.isNotBlank() }?.let { url ->
                                        a(href = url) { +TEXT_DOWNLOAD_FILE }
                                        br {}
                                    }
                                    when (assignment.status) {
                                        AssignmentStatus.ASSIGNED -> {
                                            form(
                                                action = "/student/assignments/${assignment.id}/download",
                                                method = FormMethod.post
                                            ) {
                                                button { +TEXT_MARK_DOWNLOADED }
                                            }
                                        }

                                        AssignmentStatus.DOWNLOADED -> {
                                            form(
                                                action = "/student/assignments/${assignment.id}/complete",
                                                method = FormMethod.post
                                            ) {
                                                button { +TEXT_COMPLETE }
                                            }
                                        }

                                        AssignmentStatus.COMPLETED -> {
                                            p { +TEXT_DONE }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                p { a(href = "/") { +TEXT_BACK_HOME } }
                p { a(href = "/logout") { +TEXT_LOGOUT } }
            }
        }
    }

    get("/student/assignments/{id}") {
        val student = call.requireStudent(userRepo) ?: return@get
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respondText(TEXT_ASSIGNMENT_NOT_FOUND, status = HttpStatusCode.NotFound)
            return@get
        }
        val assignment = assignmentRepo.getById(id)
        if (assignment == null || assignment.studentId != student.id) {
            call.respondText(TEXT_ASSIGNMENT_NOT_FOUND, status = HttpStatusCode.NotFound)
            return@get
        }
        val material = materialRepo.getById(assignment.materialId)
        val comments = commentRepo.getByAssignmentId(assignment.id)
        val authors = comments.map { it.authorId }.distinct().associateWith { userRepo.getById(it) }
        val overdue = isOverdue(assignment)

        call.respondHtml {
            head { title { +"$TEXT_ASSIGNMENT_ID${assignment.id}" } }
            body {
                h1 { +"$TEXT_ASSIGNMENT_ID${assignment.id}" }
                p {
                    span { +"$TEXT_HEADER_MATERIAL: ${material?.title ?: "-"}" }
                    material?.fileUrl?.takeIf { it.isNotBlank() }?.let { url ->
                        span { +" • " }
                        a(href = url) { +TEXT_DOWNLOAD_FILE }
                    }
                }
                p { span { +"$TEXT_HEADER_DESCRIPTION: ${material?.description ?: ""}" } }
                p {
                    span { +"$TEXT_ASSIGNMENT_STATUS: ${statusLabel(assignment.status)}" }
                }
                p {
                    span { +"$TEXT_ASSIGNMENT_DUE: ${assignment.dueDate?.format(dateFormatter) ?: "-"}" }
                    if (overdue) {
                        span { +" $TEXT_OVERDUE" }
                    }
                }
                p {
                    when (assignment.status) {
                        AssignmentStatus.ASSIGNED -> {
                            form(
                                action = "/student/assignments/${assignment.id}/download",
                                method = FormMethod.post
                            ) {
                                button { +TEXT_MARK_DOWNLOADED }
                            }
                        }
                        AssignmentStatus.DOWNLOADED -> {
                            form(
                                action = "/student/assignments/${assignment.id}/complete",
                                method = FormMethod.post
                            ) {
                                button { +TEXT_COMPLETE }
                            }
                        }
                        AssignmentStatus.COMPLETED -> {
                            span { +TEXT_DONE }
                        }
                    }
                }

                hr {}
                h2 { +TEXT_COMMENTS }
                if (comments.isEmpty()) {
                    p { +TEXT_NO_COMMENTS }
                } else {
                    comments.forEach { comment ->
                        val author = authors[comment.authorId]
                        p {
                            strong { +(author?.name ?: "-") }
                            span { +" (${roleLabel(author)}) • ${comment.createdAt.format(commentDateFormatter)}" }
                        }
                        p { +comment.text }
                    }
                }
                form(action = "/student/assignments/${assignment.id}/comments", method = FormMethod.post) {
                    p {
                        textArea {
                            name = "text"
                            placeholder = TEXT_COMMENT_PLACEHOLDER
                            required = true
                        }
                    }
                    button { +TEXT_COMMENT_SEND }
                }
                hr {}
                p { a(href = "/student") { +TEXT_BACK } }
            }
        }
    }

    post("/student/assignments/{id}/download") {
        val student = call.requireStudent(userRepo) ?: return@post
        val id = call.parameters["id"]?.toIntOrNull()
        val assignment = id?.let { assignmentRepo.getById(it) }

        if (assignment == null || assignment.studentId != student.id) {
            call.respondText(TEXT_ASSIGNMENT_NOT_FOUND, status = HttpStatusCode.NotFound)
            return@post
        }

        assignmentRepo.update(assignment.copy(status = AssignmentStatus.DOWNLOADED))
        call.respondRedirect("/student")
    }

    post("/student/assignments/{id}/complete") {
        val student = call.requireStudent(userRepo) ?: return@post
        val id = call.parameters["id"]?.toIntOrNull()
        val assignment = id?.let { assignmentRepo.getById(it) }

        if (assignment == null || assignment.studentId != student.id) {
            call.respondText(TEXT_ASSIGNMENT_NOT_FOUND, status = HttpStatusCode.NotFound)
            return@post
        }

        assignmentRepo.update(assignment.copy(status = AssignmentStatus.COMPLETED))
        call.respondRedirect("/student")
    }

    post("/student/assignments/{id}/comments") {
        val student = call.requireStudent(userRepo) ?: return@post
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respondText(TEXT_ASSIGNMENT_NOT_FOUND, status = HttpStatusCode.NotFound)
            return@post
        }
        val assignment = assignmentRepo.getById(id)
        if (assignment == null || assignment.studentId != student.id) {
            call.respondText(TEXT_ASSIGNMENT_NOT_FOUND, status = HttpStatusCode.NotFound)
            return@post
        }
        val text = call.receiveParameters()["text"]?.trim().orEmpty()
        if (text.isNotBlank()) {
            commentRepo.create(assignment.id, student.id, text)
        }
        call.respondRedirect("/student/assignments/$id")
    }
}

private fun statusLabel(status: AssignmentStatus): String =
    when (status) {
        AssignmentStatus.ASSIGNED -> TEXT_STATUS_ASSIGNED
        AssignmentStatus.DOWNLOADED -> TEXT_STATUS_DOWNLOADED
        AssignmentStatus.COMPLETED -> TEXT_STATUS_COMPLETED
    }

private fun isOverdue(assignment: Assignment): Boolean =
    assignment.dueDate?.let { due ->
        assignment.status != AssignmentStatus.COMPLETED && LocalDate.now().isAfter(due)
    } ?: false

private fun roleLabel(user: User?): String =
    when (user?.role) {
        UserRole.TEACHER -> TEXT_AUTHOR_TEACHER
        UserRole.STUDENT -> TEXT_AUTHOR_STUDENT
        else -> "-"
    }

private suspend fun ApplicationCall.requireStudent(userRepo: UserRepository): User? {
    val session = sessions.get<UserSession>()
    val user = session?.let { userRepo.getById(it.userId) }
    if (user == null || user.role != UserRole.STUDENT) {
        respondRedirect("/login")
        return null
    }
    return user
}
