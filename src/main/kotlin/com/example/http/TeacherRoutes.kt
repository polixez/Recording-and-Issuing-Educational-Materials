package com.example.http

import com.example.domain.model.AssignmentStatus
import com.example.domain.repository.Repositories
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receiveParameters
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
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.li
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.select
import kotlinx.html.textArea
import kotlinx.html.title
import kotlinx.html.ul
private const val TITLE_TEACHER = "\u041a\u0430\u0431\u0438\u043d\u0435\u0442 \u043f\u0440\u0435\u043f\u043e\u0434\u0430\u0432\u0430\u0442\u0435\u043b\u044f"
private const val TEXT_MATERIALS = "\u041c\u0430\u0442\u0435\u0440\u0438\u0430\u043b\u044b"
private const val TEXT_NO_MATERIALS = "\u041c\u0430\u0442\u0435\u0440\u0438\u0430\u043b\u044b \u043e\u0442\u0441\u0443\u0442\u0441\u0442\u0432\u0443\u044e\u0442"
private const val TEXT_ADD_MATERIAL = "\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043c\u0430\u0442\u0435\u0440\u0438\u0430\u043b"
private const val TEXT_ASSIGN_LINK = "\u041d\u0430\u0437\u043d\u0430\u0447\u0438\u0442\u044c \u043c\u0430\u0442\u0435\u0440\u0438\u0430\u043b \u0441\u0442\u0443\u0434\u0435\u043d\u0442\u0443"
private const val TEXT_NEW_MATERIAL = "\u041d\u043e\u0432\u044b\u0439 \u043c\u0430\u0442\u0435\u0440\u0438\u0430\u043b"
private const val TEXT_NAME = "\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435"
private const val TEXT_DESCRIPTION = "\u041e\u043f\u0438\u0441\u0430\u043d\u0438\u0435"
private const val TEXT_FILE_URL = "\u0421\u0441\u044b\u043b\u043a\u0430 (\u043e\u043f\u0446\u0438\u043e\u043d\u0430\u043b\u044c\u043d\u043e)"
private const val TEXT_SAVE = "\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c"
private const val TEXT_BACK = "\u0412\u0435\u0440\u043d\u0443\u0442\u044c\u0441\u044f"
private const val TEXT_ASSIGN_TITLE = "\u041d\u0430\u0437\u043d\u0430\u0447\u0438\u0442\u044c \u043c\u0430\u0442\u0435\u0440\u0438\u0430\u043b"
private const val TEXT_ASSIGN_HEADER = "\u041d\u0430\u0437\u043d\u0430\u0447\u0438\u0442\u044c \u043c\u0430\u0442\u0435\u0440\u0438\u0430\u043b \u0441\u0442\u0443\u0434\u0435\u043d\u0442\u0443"
private const val TEXT_ASSIGN_SUCCESS = "\u041d\u0430\u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 \u0441\u043e\u0437\u0434\u0430\u043d\u043e"
private const val TEXT_MATERIAL_LABEL = "\u041c\u0430\u0442\u0435\u0440\u0438\u0430\u043b"
private const val TEXT_STUDENT_LABEL = "\u0421\u0442\u0443\u0434\u0435\u043d\u0442"
private const val TEXT_ASSIGN_BUTTON = "\u041d\u0430\u0437\u043d\u0430\u0447\u0438\u0442\u044c"
private const val TEXT_BAD_REQUEST = "\u041d\u0435\u043a\u043e\u0440\u0440\u0435\u043a\u0442\u043d\u044b\u0435 \u0434\u0430\u043d\u043d\u044b\u0435"
private const val TEXT_FILL_FIELDS = "\u0417\u0430\u043f\u043e\u043b\u043d\u0438\u0442\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u0438 \u043e\u043f\u0438\u0441\u0430\u043d\u0438\u0435"

fun Route.teacherRoutes() {
    val materialRepo = Repositories.materialRepository
    val assignmentRepo = Repositories.assignmentRepository
    val userRepo = Repositories.userRepository

    get("/teacher") {
        val materials = materialRepo.getAll()
        call.respondHtml {
            head { title { +TITLE_TEACHER } }
            body {
                h1 { +TITLE_TEACHER }
                h2 { +TEXT_MATERIALS }
                if (materials.isEmpty()) {
                    p { +TEXT_NO_MATERIALS }
                } else {
                    ul {
                        materials.forEach { material ->
                            li { +"${material.title}: ${material.description}" }
                        }
                    }
                }
                p { a(href = "/teacher/materials/new") { +TEXT_ADD_MATERIAL } }
                p { a(href = "/teacher/assign") { +TEXT_ASSIGN_LINK } }
            }
        }
    }

    get("/teacher/materials/new") {
        call.respondHtml {
            head { title { +TEXT_NEW_MATERIAL } }
            body {
                h1 { +TEXT_ADD_MATERIAL }
                form(action = "/teacher/materials", method = FormMethod.post) {
                    p {
                        label { +TEXT_NAME; input { name = "title"; required = true } }
                    }
                    p {
                        label { +TEXT_DESCRIPTION; textArea { name = "description"; required = true } }
                    }
                    p {
                        label { +TEXT_FILE_URL; input { name = "fileUrl" } }
                    }
                    button { +TEXT_SAVE }
                }
                p { a(href = "/teacher") { +TEXT_BACK } }
            }
        }
    }

    post("/teacher/materials") {
        val params = call.receiveParameters()
        val title = params["title"]?.trim().orEmpty()
        val description = params["description"]?.trim().orEmpty()
        val fileUrl = params["fileUrl"]?.trim().orEmpty()

        if (title.isBlank() || description.isBlank()) {
            call.respondText(TEXT_FILL_FIELDS, status = HttpStatusCode.BadRequest)
            return@post
        }

        materialRepo.create(title = title, description = description, fileUrl = fileUrl)
        call.respondRedirect("/teacher")
    }

    get("/teacher/assign") {
        val materials = materialRepo.getAll()
        val students = userRepo.getAllStudents()
        val success = call.request.queryParameters["success"] == "1"

        call.respondHtml {
            head { title { +TEXT_ASSIGN_TITLE } }
            body {
                h1 { +TEXT_ASSIGN_HEADER }
                if (success) {
                    p { +TEXT_ASSIGN_SUCCESS }
                }
                form(action = "/teacher/assign", method = FormMethod.post) {
                    p {
                        label { +TEXT_MATERIAL_LABEL }
                        select {
                            name = "materialId"
                            materials.forEach { material ->
                                option {
                                    value = material.id.toString()
                                    +material.title
                                }
                            }
                        }
                    }
                    p {
                        label { +TEXT_STUDENT_LABEL }
                        select {
                            name = "studentId"
                            students.forEach { student ->
                                option {
                                    value = student.id.toString()
                                    +"${student.name} (id=${student.id})"
                                }
                            }
                        }
                    }
                    button { +TEXT_ASSIGN_BUTTON }
                }
                p { a(href = "/teacher") { +TEXT_BACK } }
            }
        }
    }

    post("/teacher/assign") {
        val params = call.receiveParameters()
        val materialId = params["materialId"]?.toIntOrNull()
        val studentId = params["studentId"]?.toIntOrNull()

        if (materialId == null || studentId == null) {
            call.respondText(TEXT_BAD_REQUEST, status = HttpStatusCode.BadRequest)
            return@post
        }

        assignmentRepo.create(
            materialId = materialId,
            studentId = studentId,
            status = AssignmentStatus.ASSIGNED
        )
        call.respondRedirect("/teacher/assign?success=1")
    }
}
