package com.example.http

import com.example.domain.model.AssignmentStatus
import com.example.domain.repository.Repositories
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.html.FormMethod
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.p
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.title
import kotlinx.html.tr

private const val STUDENT_ID = 100
private const val TITLE_STUDENT = "\u041a\u0430\u0431\u0438\u043d\u0435\u0442 \u0441\u0442\u0443\u0434\u0435\u043d\u0442\u0430"
private const val TEXT_HEADER_MATERIAL = "\u041c\u0430\u0442\u0435\u0440\u0438\u0430\u043b"
private const val TEXT_HEADER_DESCRIPTION = "\u041e\u043f\u0438\u0441\u0430\u043d\u0438\u0435"
private const val TEXT_HEADER_STATUS = "\u0421\u0442\u0430\u0442\u0443\u0441"
private const val TEXT_HEADER_ACTIONS = "\u0414\u0435\u0439\u0441\u0442\u0432\u0438\u044f"
private const val TEXT_DOWNLOAD = "\u0421\u043a\u0430\u0447\u0430\u0442\u044c"
private const val TEXT_COMPLETE = "\u041e\u0442\u043c\u0435\u0442\u0438\u0442\u044c \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u043d\u044b\u043c"
private const val TEXT_DONE = "\u0413\u043e\u0442\u043e\u0432\u043e"
private const val TEXT_BACK_HOME = "\u041d\u0430 \u0433\u043b\u0430\u0432\u043d\u0443\u044e"
private const val TEXT_ASSIGNMENT_NOT_FOUND = "\u041d\u0430\u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e"

fun Route.studentRoutes() {
    val assignmentRepo = Repositories.assignmentRepository
    val materialRepo = Repositories.materialRepository

    get("/student") {
        val assignments = assignmentRepo.getByStudentId(STUDENT_ID)

        call.respondHtml {
            head { title { +TITLE_STUDENT } }
            body {
                h1 { +TITLE_STUDENT }
                table {
                    thead {
                        tr {
                            th { +TEXT_HEADER_MATERIAL }
                            th { +TEXT_HEADER_DESCRIPTION }
                            th { +TEXT_HEADER_STATUS }
                            th { +TEXT_HEADER_ACTIONS }
                        }
                    }
                    tbody {
                        assignments.forEach { assignment ->
                            val material = materialRepo.getById(assignment.materialId)
                            tr {
                                td { +(material?.title ?: "-") }
                                td { +(material?.description ?: "") }
                                td { +statusLabel(assignment.status) }
                                td {
                                    when (assignment.status) {
                                        AssignmentStatus.ASSIGNED -> {
                                            form(
                                                action = "/student/assignments/${assignment.id}/download",
                                                method = FormMethod.post
                                            ) {
                                                button { +TEXT_DOWNLOAD }
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
            }
        }
    }

    post("/student/assignments/{id}/download") {
        val id = call.parameters["id"]?.toIntOrNull()
        val assignment = id?.let { assignmentRepo.getById(it) }

        if (assignment == null || assignment.studentId != STUDENT_ID) {
            call.respondText(TEXT_ASSIGNMENT_NOT_FOUND, status = HttpStatusCode.NotFound)
            return@post
        }

        assignmentRepo.update(assignment.copy(status = AssignmentStatus.DOWNLOADED))
        call.respondRedirect("/student")
    }

    post("/student/assignments/{id}/complete") {
        val id = call.parameters["id"]?.toIntOrNull()
        val assignment = id?.let { assignmentRepo.getById(it) }

        if (assignment == null || assignment.studentId != STUDENT_ID) {
            call.respondText(TEXT_ASSIGNMENT_NOT_FOUND, status = HttpStatusCode.NotFound)
            return@post
        }

        assignmentRepo.update(assignment.copy(status = AssignmentStatus.COMPLETED))
        call.respondRedirect("/student")
    }
}

private fun statusLabel(status: AssignmentStatus): String =
    when (status) {
        AssignmentStatus.ASSIGNED -> "\u041d\u0430\u0437\u043d\u0430\u0447\u0435\u043d"
        AssignmentStatus.DOWNLOADED -> "\u0421\u043a\u0430\u0447\u0430\u043d"
        AssignmentStatus.COMPLETED -> "\u0412\u044b\u043f\u043e\u043b\u043d\u0435\u043d"
    }
