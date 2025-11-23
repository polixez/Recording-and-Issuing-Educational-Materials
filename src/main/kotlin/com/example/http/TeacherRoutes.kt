package com.example.http

import com.example.UserSession
import com.example.domain.model.Assignment
import com.example.domain.model.AssignmentStatus
import com.example.domain.model.Material
import com.example.domain.model.User
import com.example.domain.model.UserRole
import com.example.domain.repository.Repositories
import com.example.domain.repository.UserRepository
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receiveMultipart
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.html.FormEncType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
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
import kotlinx.html.span
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.textArea
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.title
import kotlinx.html.tr
import kotlinx.html.ul
import kotlinx.html.style

private const val TITLE_TEACHER = "\u041a\u0430\u0431\u0438\u043d\u0435\u0442 \u043f\u0440\u0435\u043f\u043e\u0434\u0430\u0432\u0430\u0442\u0435\u043b\u044f"
private const val TEXT_MATERIALS = "\u041c\u0430\u0442\u0435\u0440\u0438\u0430\u043b\u044b"
private const val TEXT_NO_MATERIALS = "\u041c\u0430\u0442\u0435\u0440\u0438\u0430\u043b\u044b \u043e\u0442\u0441\u0443\u0442\u0441\u0442\u0432\u0443\u044e\u0442"
private const val TEXT_ADD_MATERIAL = "\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043c\u0430\u0442\u0435\u0440\u0438\u0430\u043b"
private const val TEXT_ASSIGN_LINK = "\u041d\u0430\u0437\u043d\u0430\u0447\u0438\u0442\u044c \u043c\u0430\u0442\u0435\u0440\u0438\u0430\u043b \u0441\u0442\u0443\u0434\u0435\u043d\u0442\u0443"
private const val TEXT_DEADLINES_LINK = "\u0414\u0435\u0434\u043b\u0430\u0439\u043d\u044b"
private const val TEXT_NEW_MATERIAL = "\u041d\u043e\u0432\u044b\u0439 \u043c\u0430\u0442\u0435\u0440\u0438\u0430\u043b"
private const val TEXT_NAME = "\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435"
private const val TEXT_DESCRIPTION = "\u041e\u043f\u0438\u0441\u0430\u043d\u0438\u0435"
private const val TEXT_FILE = "\u0424\u0430\u0439\u043b"
private const val TEXT_EXTERNAL_URL = "\u0412\u043d\u0435\u0448\u043d\u044f\u044f \u0441\u0441\u044b\u043b\u043a\u0430 (\u043e\u043f\u0446\u0438\u043e\u043d\u0430\u043b\u044c\u043d\u043e)"
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
private const val TEXT_LOGOUT = "\u0412\u044b\u0439\u0442\u0438"
private const val TEXT_OPEN_FILE = "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u0444\u0430\u0439\u043b"
private const val TEXT_DUE_DATE = "\u0414\u0435\u0434\u043b\u0430\u0439\u043d"
private const val TEXT_DEADLINES_HEADER = "\u0414\u0435\u0434\u043b\u0430\u0439\u043d\u044b \u043f\u043e \u043d\u0430\u0437\u043d\u0430\u0447\u0435\u043d\u043d\u044b\u043c \u043c\u0430\u0442\u0435\u0440\u0438\u0430\u043b\u0430\u043c"
private const val TEXT_STATUS = "\u0421\u0442\u0430\u0442\u0443\u0441"
private const val TEXT_OVERDUE = "(\u043f\u0440\u043e\u0441\u0440\u043e\u0447\u0435\u043d\u043e)"
private const val TEXT_NO_ASSIGNMENTS = "\u041d\u0430\u0437\u043d\u0430\u0447\u0435\u043d\u0438\u044f \u043e\u0442\u0441\u0443\u0442\u0441\u0442\u0432\u0443\u044e\u0442"
private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

private data class AssignmentWithUserAndMaterial(
    val assignment: Assignment,
    val material: Material,
    val student: User,
    val isOverdue: Boolean
)

fun Route.teacherRoutes(uploadDirPath: String) {
    val materialRepo = Repositories.materialRepository
    val assignmentRepo = Repositories.assignmentRepository
    val userRepo = Repositories.userRepository

    get("/teacher") {
        val teacher = call.requireTeacher(userRepo) ?: return@get
        val materials = materialRepo.getAll()
        call.respondHtml {
            head { title { +TITLE_TEACHER } }
            body {
                h1 { +"$TITLE_TEACHER (${teacher.name})" }
                h2 { +TEXT_MATERIALS }
                if (materials.isEmpty()) {
                    p { +TEXT_NO_MATERIALS }
                } else {
                    ul {
                        materials.forEach { material ->
                            li {
                                +"${material.title}: ${material.description} "
                                material.fileUrl?.let { url ->
                                    a(href = url) { +TEXT_OPEN_FILE }
                                }
                            }
                        }
                    }
                }
                p { a(href = "/teacher/materials/new") { +TEXT_ADD_MATERIAL } }
                p { a(href = "/teacher/assign") { +TEXT_ASSIGN_LINK } }
                p { a(href = "/teacher/deadlines") { +TEXT_DEADLINES_LINK } }
                p { a(href = "/logout") { +TEXT_LOGOUT } }
            }
        }
    }

    get("/teacher/materials/new") {
        call.requireTeacher(userRepo) ?: return@get
        call.respondHtml {
            head { title { +TEXT_NEW_MATERIAL } }
            body {
                h1 { +TEXT_ADD_MATERIAL }
                form(action = "/teacher/materials", method = FormMethod.post, encType = FormEncType.multipartFormData) {
                    p {
                        label { +TEXT_NAME }
                        input { name = "title"; required = true }
                    }
                    p {
                        label { +TEXT_DESCRIPTION }
                        textArea { name = "description"; required = true }
                    }
                    p {
                        label { +TEXT_FILE }
                        input {
                            type = InputType.file
                            name = "file"
                        }
                    }
                    p {
                        label { +TEXT_EXTERNAL_URL }
                        input { name = "externalUrl" }
                    }
                    button { +TEXT_SAVE }
                }
                p { a(href = "/teacher") { +TEXT_BACK } }
            }
        }
    }

    post("/teacher/materials") {
        call.requireTeacher(userRepo) ?: return@post
        val multipart = call.receiveMultipart()
        var title: String? = null
        var description: String? = null
        var externalUrl: String? = null
        var storedFileName: String? = null

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> when (part.name) {
                    "title" -> title = part.value.trim()
                    "description" -> description = part.value.trim()
                    "externalUrl" -> externalUrl = part.value.trim()
                }
                is PartData.FileItem -> if (part.name == "file") {
                    storedFileName = saveUploadedFile(part, uploadDirPath) ?: storedFileName
                }
                else -> {}
            }
            part.dispose()
        }

        val finalTitle = title?.trim().orEmpty()
        val finalDescription = description?.trim().orEmpty()
        val finalExternalUrl = externalUrl?.trim().orEmpty()

        if (finalTitle.isBlank() || finalDescription.isBlank()) {
            call.respondText(TEXT_FILL_FIELDS, status = HttpStatusCode.BadRequest)
            return@post
        }

        val fileUrl = when {
            !storedFileName.isNullOrBlank() -> "/files/$storedFileName"
            finalExternalUrl.isNotBlank() -> finalExternalUrl
            else -> null
        }

        materialRepo.create(title = finalTitle, description = finalDescription, fileUrl = fileUrl)
        call.respondRedirect("/teacher")
    }

    get("/teacher/assign") {
        call.requireTeacher(userRepo) ?: return@get
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
                    p {
                        label { +TEXT_DUE_DATE }
                        input {
                            type = InputType.date
                            name = "dueDate"
                        }
                    }
                    button { +TEXT_ASSIGN_BUTTON }
                }
                p { a(href = "/teacher") { +TEXT_BACK } }
            }
        }
    }

    post("/teacher/assign") {
        call.requireTeacher(userRepo) ?: return@post
        val params = call.receiveParameters()
        val materialId = params["materialId"]?.toIntOrNull()
        val studentId = params["studentId"]?.toIntOrNull()
        val dueDate = parseDueDate(params["dueDate"])

        if (materialId == null || studentId == null) {
            call.respondText(TEXT_BAD_REQUEST, status = HttpStatusCode.BadRequest)
            return@post
        }

        assignmentRepo.create(
            materialId = materialId,
            studentId = studentId,
            status = AssignmentStatus.ASSIGNED,
            dueDate = dueDate
        )
        call.respondRedirect("/teacher/assign?success=1")
    }

    get("/teacher/deadlines") {
        call.requireTeacher(userRepo) ?: return@get
        val assignments = assignmentRepo.getAll()
        val materialsById = materialRepo.getAll().associateBy { it.id }
        val studentsById = userRepo.getAllStudents().associateBy { it.id }
        val today = LocalDate.now()

        val items = assignments.mapNotNull { assignment ->
            val material = materialsById[assignment.materialId] ?: return@mapNotNull null
            val student = studentsById[assignment.studentId] ?: userRepo.getById(assignment.studentId) ?: return@mapNotNull null
            AssignmentWithUserAndMaterial(
                assignment = assignment,
                material = material,
                student = student,
                isOverdue = isOverdue(assignment, today)
            )
        }.sortedBy { it.assignment.dueDate ?: LocalDate.MAX }

        call.respondHtml {
            head { title { +TEXT_DEADLINES_HEADER } }
            body {
                h1 { +TEXT_DEADLINES_HEADER }
                if (items.isEmpty()) {
                    p { +TEXT_NO_ASSIGNMENTS }
                } else {
                    table {
                        thead {
                            tr {
                                th { +TEXT_MATERIAL_LABEL }
                                th { +TEXT_STUDENT_LABEL }
                                th { +TEXT_DUE_DATE }
                                th { +TEXT_STATUS }
                            }
                        }
                        tbody {
                            items.forEach { item ->
                                tr {
                                    if (item.isOverdue) {
                                        attributes["style"] = "background-color:#ffe6e6;"
                                    }
                                    td { +item.material.title }
                                    td { +item.student.name }
                                    td {
                                        +(item.assignment.dueDate?.format(dateFormatter) ?: "-")
                                        if (item.isOverdue) {
                                            span {
                                                style = "color: red; margin-left: 6px;"
                                                +TEXT_OVERDUE
                                            }
                                        }
                                    }
                                    td { +statusLabel(item.assignment.status) }
                                }
                            }
                        }
                    }
                }
                p { a(href = "/teacher") { +TEXT_BACK } }
            }
        }
    }
}

private fun statusLabel(status: AssignmentStatus): String =
    when (status) {
        AssignmentStatus.ASSIGNED -> "\u041d\u0430\u0437\u043d\u0430\u0447\u0435\u043d"
        AssignmentStatus.DOWNLOADED -> "\u0421\u043a\u0430\u0447\u0430\u043d"
        AssignmentStatus.COMPLETED -> "\u0412\u044b\u043f\u043e\u043b\u043d\u0435\u043d"
    }

private fun parseDueDate(raw: String?): LocalDate? =
    raw?.takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

private fun isOverdue(assignment: Assignment, today: LocalDate = LocalDate.now()): Boolean =
    assignment.dueDate?.let { due ->
        assignment.status != AssignmentStatus.COMPLETED && today.isAfter(due)
    } ?: false

private fun saveUploadedFile(fileItem: PartData.FileItem, uploadDirPath: String): String? {
    val extension = fileItem.originalFileName
        ?.substringAfterLast('.', "")
        ?.takeIf { it.isNotBlank() }
    val storedFileName = buildString {
        append("material_")
        append(System.currentTimeMillis())
        if (!extension.isNullOrBlank()) {
            append(".")
            append(extension)
        }
    }
    val targetFile = File(uploadDirPath, storedFileName)
    targetFile.parentFile?.mkdirs()
    val bytesWritten = runCatching {
        fileItem.streamProvider().use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }.getOrElse {
        targetFile.delete()
        return null
    }

    return if (bytesWritten > 0) storedFileName else {
        targetFile.delete()
        null
    }
}

private suspend fun ApplicationCall.requireTeacher(userRepo: UserRepository): User? {
    val session = sessions.get<UserSession>()
    val user = session?.let { userRepo.getById(it.userId) }
    if (user == null || user.role != UserRole.TEACHER) {
        respondRedirect("/login")
        return null
    }
    return user
}
