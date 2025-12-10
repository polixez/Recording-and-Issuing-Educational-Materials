package com.example.http

import com.example.UserSession
import com.example.domain.model.Assignment
import com.example.domain.model.AssignmentStatus
import com.example.domain.model.AssignmentWithRelations
import com.example.domain.model.Group
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
import kotlinx.html.hr
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.li
import kotlinx.html.meta
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.select
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
import kotlinx.html.ul
import kotlinx.html.style

private const val TITLE_TEACHER = "Кабинет преподавателя"
private const val TEXT_MATERIALS = "Материалы"
private const val TEXT_NO_MATERIALS = "Материалов нет"
private const val TEXT_ADD_MATERIAL = "Добавить материал"
private const val TEXT_ASSIGN_LINK = "Назначить материал"
private const val TEXT_GROUPS_LINK = "Группы"
private const val TEXT_DEADLINES_LINK = "Дедлайны"
private const val TEXT_NEW_MATERIAL = "Новый материал"
private const val TEXT_NAME = "Название"
private const val TEXT_DESCRIPTION = "Описание"
private const val TEXT_FILE = "Файл"
private const val TEXT_EXTERNAL_URL = "Внешняя ссылка (опционально)"
private const val TEXT_SAVE = "Сохранить"
private const val TEXT_BACK = "Назад"
private const val TEXT_ASSIGN_TITLE = "Назначение материала"
private const val TEXT_ASSIGN_HEADER = "Назначить материал студенту или группе"
private const val TEXT_ASSIGN_SUCCESS = "Задание успешно создано"
private const val TEXT_MATERIAL_LABEL = "Материал"
private const val TEXT_STUDENT_LABEL = "Студент"
private const val TEXT_ASSIGN_BUTTON = "Назначить"
private const val TEXT_BAD_REQUEST = "Некорректный запрос"
private const val TEXT_FILL_FIELDS = "Заполните все обязательные поля"
private const val TEXT_LOGOUT = "Выход"
private const val TEXT_OPEN_FILE = "Открыть файл"
private const val TEXT_DUE_DATE = "Дедлайн"
private const val TEXT_DEADLINES_HEADER = "Дедлайны по заданиям"
private const val TEXT_STATUS = "Статус"
private const val TEXT_OVERDUE = "(Просрочено)"
private const val TEXT_NO_ASSIGNMENTS = "Заданий нет"
private const val TEXT_ASSIGN_DETAILS = "Детали задания"
private const val TEXT_DETAILS = "Подробнее"
private const val TEXT_GROUPS = "Группы"
private const val TEXT_CREATE_GROUP = "Создать группу"
private const val TEXT_GROUP_NAME = "Название группы"
private const val TEXT_GROUP_MEMBERS = "Состав группы"
private const val TEXT_ADD_STUDENT = "Добавить студента"
private const val TEXT_REMOVE = "Удалить"
private const val TEXT_GROUP_NOT_FOUND = "Группа не найдена"
private const val TEXT_ASSIGN_GROUP_LABEL = "Группа (опционально)"
private const val TEXT_NO_GROUP = "(Без группы)"
private const val TEXT_ASSIGNMENT_DETAILS_TITLE = "Детали задания"
private const val TEXT_ASSIGNMENT_NOT_FOUND = "Задание не найдено"
private const val TEXT_COMMENTS = "Комментарии"
private const val TEXT_NO_COMMENTS = "Комментариев пока нет"
private const val TEXT_COMMENT_PLACEHOLDER = "Введите комментарий"
private const val TEXT_COMMENT_SEND = "Отправить"
private const val TEXT_ASSIGNMENT_ID = "Задание №"
private const val TEXT_ASSIGNMENT_STUDENT = "Студент"
private const val TEXT_ASSIGNMENT_MATERIAL = "Материал"
private const val TEXT_ASSIGNMENT_STATUS = "Статус"
private const val TEXT_ASSIGNMENT_DUE = "Дедлайн"
private const val TEXT_AUTHOR_TEACHER = "Преподаватель"
private const val TEXT_AUTHOR_STUDENT = "Студент"
private const val TEXT_ASSIGNMENT_REASSIGN = "Назначить снова"
private const val TEXT_STATUS_ASSIGNED = "Назначено"
private const val TEXT_STATUS_DOWNLOADED = "Скачано"
private const val TEXT_STATUS_COMPLETED = "Выполнено"
private const val TEXT_FILTER_STATUS = "Статус"
private const val TEXT_FILTER_GROUP = "Группа"
private const val TEXT_FILTER_SORT = "Сортировка"
private const val TEXT_SORT_ASC = "По возрастанию"
private const val TEXT_SORT_DESC = "По убыванию"
private const val TEXT_FILTER_APPLY = "Применить"
private const val TEXT_ALL = "Все"
private const val TEXT_STUDENT_REPORT = "Отчёт по студенту"
private const val TEXT_STUDENT_NOT_FOUND = "Студент не найден"
private const val TEXT_TOTAL = "Всего"
private const val TEXT_COMPLETED = "Завершено"
private const val TEXT_OVERDUE_COUNT = "Просрочено"
private const val TEXT_REPORT_ASSIGNMENTS = "Задания"
private const val TEXT_BACK_TO_DEADLINES = "Назад к дедлайнам"
private const val TEXT_REPORT = "Отчёт"
private const val TEXT_REASSIGN_NOTICE = "Повторное назначение задания №"

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val commentDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

private data class DeadlinesItem(
    val relations: AssignmentWithRelations,
    val isOverdue: Boolean
)

private data class AssignFormDefaults(
    val materialId: Int? = null,
    val studentId: Int? = null,
    val groupId: Int? = null,
    val dueDate: LocalDate? = null
)

fun Route.teacherRoutes(uploadDirPath: String) {
    val materialRepo = Repositories.materialRepository
    val assignmentRepo = Repositories.assignmentRepository
    val userRepo = Repositories.userRepository
    val groupRepo = Repositories.groupRepository
    val commentRepo = Repositories.commentRepository

    get("/teacher") {
        val teacher = call.requireTeacher(userRepo) ?: return@get
        val materials = materialRepo.getAll()
        call.respondHtml {
            head {
                meta { charset = "UTF-8" }
                title { +TITLE_TEACHER }
            }
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
                                material.fileUrl?.takeIf { it.isNotBlank() }?.let { url ->
                                    a(href = url) { +TEXT_OPEN_FILE }
                                }
                            }
                        }
                    }
                }
                p { a(href = "/teacher/materials/new") { +TEXT_ADD_MATERIAL } }
                p { a(href = "/teacher/assign") { +TEXT_ASSIGN_LINK } }
                p { a(href = "/teacher/groups") { +TEXT_GROUPS_LINK } }
                p { a(href = "/teacher/deadlines") { +TEXT_DEADLINES_LINK } }
                p { a(href = "/logout") { +TEXT_LOGOUT } }
            }
        }
    }

    get("/teacher/materials/new") {
        call.requireTeacher(userRepo) ?: return@get
        call.respondHtml {
            head {
                meta { charset = "UTF-8" }
                title { +TEXT_NEW_MATERIAL }
            }
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
        val groups = groupRepo.getAll()
        val success = call.request.queryParameters["success"] == "1"
        val defaults = AssignFormDefaults(
            materialId = call.request.queryParameters["materialId"]?.toIntOrNull(),
            studentId = call.request.queryParameters["studentId"]?.toIntOrNull(),
            groupId = call.request.queryParameters["groupId"]?.toIntOrNull(),
            dueDate = parseDueDate(call.request.queryParameters["dueDate"])
        )

        call.renderAssignPage(materials, students, groups, success, defaults, reassignFrom = null)
    }

    post("/teacher/assign") {
        call.requireTeacher(userRepo) ?: return@post
        val params = call.receiveParameters()
        val materialId = params["materialId"]?.toIntOrNull()
        val studentId = params["studentId"]?.toIntOrNull()
        val groupId = params["groupId"]?.toIntOrNull()
        val dueDate = parseDueDate(params["dueDate"])

        if (materialId == null) {
            call.respondText(TEXT_BAD_REQUEST, status = HttpStatusCode.BadRequest)
            return@post
        }

        if (groupId != null) {
            val group = groupRepo.getById(groupId)
            if (group == null) {
                call.respondText(TEXT_BAD_REQUEST, status = HttpStatusCode.BadRequest)
                return@post
            }
            val members = groupRepo.getMembers(groupId)
            members.forEach { member ->
                assignmentRepo.create(
                    materialId = materialId,
                    studentId = member.id,
                    status = AssignmentStatus.ASSIGNED,
                    dueDate = dueDate
                )
            }
        } else {
            if (studentId == null) {
                call.respondText(TEXT_BAD_REQUEST, status = HttpStatusCode.BadRequest)
                return@post
            }
            assignmentRepo.create(
                materialId = materialId,
                studentId = studentId,
                status = AssignmentStatus.ASSIGNED,
                dueDate = dueDate
            )
        }
        call.respondRedirect("/teacher/assign?success=1")
    }

    get("/teacher/assignments/{id}/reassign") {
        call.requireTeacher(userRepo) ?: return@get
        val assignmentId = call.parameters["id"]?.toIntOrNull()
        if (assignmentId == null) {
            call.respondText(TEXT_BAD_REQUEST, status = HttpStatusCode.BadRequest)
            return@get
        }
        val assignment = assignmentRepo.getById(assignmentId)
        if (assignment == null) {
            call.respondText(TEXT_ASSIGNMENT_NOT_FOUND, status = HttpStatusCode.NotFound)
            return@get
        }
        val materials = materialRepo.getAll()
        val students = userRepo.getAllStudents()
        val groups = groupRepo.getAll()
        val defaultGroupId = groupRepo.getGroupsForStudent(assignment.studentId).firstOrNull()?.id
        val defaults = AssignFormDefaults(
            materialId = assignment.materialId,
            studentId = assignment.studentId,
            groupId = defaultGroupId,
            dueDate = assignment.dueDate
        )

        call.renderAssignPage(materials, students, groups, success = false, defaults = defaults, reassignFrom = assignment.id)
    }

    get("/teacher/groups") {
        call.requireTeacher(userRepo) ?: return@get
        val groups = groupRepo.getAll()
        call.respondHtml {
            head {
                meta { charset = "UTF-8" }
                title { +TEXT_GROUPS }
            }
            body {
                h1 { +TEXT_GROUPS }
                if (groups.isEmpty()) {
                    p { +TEXT_NO_ASSIGNMENTS }
                } else {
                    ul {
                        groups.forEach { group ->
                            li {
                                a(href = "/teacher/groups/${group.id}") { +group.name }
                            }
                        }
                    }
                }
                h2 { +TEXT_CREATE_GROUP }
                form(action = "/teacher/groups", method = FormMethod.post) {
                    p {
                        label { +TEXT_GROUP_NAME }
                        input {
                            name = "name"
                            required = true
                        }
                    }
                    button { +TEXT_CREATE_GROUP }
                }
                p { a(href = "/teacher") { +TEXT_BACK } }
            }
        }
    }

    post("/teacher/groups") {
        call.requireTeacher(userRepo) ?: return@post
        val params = call.receiveParameters()
        val name = params["name"]?.trim().orEmpty()
        if (name.isBlank()) {
            call.respondText(TEXT_BAD_REQUEST, status = HttpStatusCode.BadRequest)
            return@post
        }
        groupRepo.create(name)
        call.respondRedirect("/teacher/groups")
    }

    get("/teacher/groups/{id}") {
        call.requireTeacher(userRepo) ?: return@get
        val groupId = call.parameters["id"]?.toIntOrNull()
        if (groupId == null) {
            call.respondText(TEXT_BAD_REQUEST, status = HttpStatusCode.BadRequest)
            return@get
        }
        val group = groupRepo.getById(groupId)
        if (group == null) {
            call.respondText(TEXT_GROUP_NOT_FOUND, status = HttpStatusCode.NotFound)
            return@get
        }
        val members = groupRepo.getMembers(groupId)
        val students = userRepo.getAllStudents()
        call.respondHtml {
            head {
                meta { charset = "UTF-8" }
                title { +"${TEXT_GROUP_NAME}: ${group.name}" }
            }
            body {
                h1 { +"${TEXT_GROUP_NAME}: ${group.name}" }
                h2 { +TEXT_GROUP_MEMBERS }
                if (members.isEmpty()) {
                    p { +TEXT_NO_ASSIGNMENTS }
                } else {
                    ul {
                        members.forEach { student ->
                            li {
                                +"${student.name} (id=${student.id}) "
                                a(href = "/teacher/students/${student.id}") { +TEXT_REPORT }
                                +" "
                                form(
                                    action = "/teacher/groups/${group.id}/remove-student",
                                    method = FormMethod.post
                                ) {
                                    input {
                                        type = InputType.hidden
                                        name = "studentId"
                                        value = student.id.toString()
                                    }
                                    button { +TEXT_REMOVE }
                                }
                            }
                        }
                    }
                }
                h2 { +TEXT_ADD_STUDENT }
                form(action = "/teacher/groups/${group.id}/add-student", method = FormMethod.post) {
                    p {
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
                    button { +TEXT_ADD_STUDENT }
                }
                p { a(href = "/teacher/groups") { +TEXT_BACK } }
            }
        }
    }

    post("/teacher/groups/{id}/add-student") {
        call.requireTeacher(userRepo) ?: return@post
        val groupId = call.parameters["id"]?.toIntOrNull()
        val studentId = call.receiveParameters()["studentId"]?.toIntOrNull()
        if (groupId == null || studentId == null) {
            call.respondText(TEXT_BAD_REQUEST, status = HttpStatusCode.BadRequest)
            return@post
        }
        groupRepo.addStudentToGroup(groupId, studentId)
        call.respondRedirect("/teacher/groups/$groupId")
    }

    post("/teacher/groups/{id}/remove-student") {
        call.requireTeacher(userRepo) ?: return@post
        val groupId = call.parameters["id"]?.toIntOrNull()
        val studentId = call.receiveParameters()["studentId"]?.toIntOrNull()
        if (groupId == null || studentId == null) {
            call.respondText(TEXT_BAD_REQUEST, status = HttpStatusCode.BadRequest)
            return@post
        }
        groupRepo.removeStudentFromGroup(groupId, studentId)
        call.respondRedirect("/teacher/groups/$groupId")
    }

    get("/teacher/assignments/{id}") {
        call.requireTeacher(userRepo) ?: return@get
        val assignmentId = call.parameters["id"]?.toIntOrNull()
        if (assignmentId == null) {
            call.respondText(TEXT_BAD_REQUEST, status = HttpStatusCode.BadRequest)
            return@get
        }
        val assignment = assignmentRepo.getById(assignmentId)
        if (assignment == null) {
            call.respondText(TEXT_ASSIGNMENT_NOT_FOUND, status = HttpStatusCode.NotFound)
            return@get
        }
        val material = materialRepo.getById(assignment.materialId)
        val student = userRepo.getById(assignment.studentId)
        val comments = commentRepo.getByAssignmentId(assignment.id)
        val authors = comments.map { it.authorId }.distinct().associateWith { userRepo.getById(it) }

        call.respondHtml {
            head {
                meta { charset = "UTF-8" }
                title { +"$TEXT_ASSIGNMENT_DETAILS_TITLE ${assignment.id}" }
            }
            body {
                h1 { +"$TEXT_ASSIGNMENT_ID${assignment.id}" }
                p {
                    strong { +TEXT_ASSIGNMENT_MATERIAL }
                    span { +": ${material?.title ?: "-"}" }
                    material?.fileUrl?.takeIf { it.isNotBlank() }?.let { url ->
                        span { +" " }
                        a(href = url) { +TEXT_OPEN_FILE }
                    }
                }
                p {
                    strong { +TEXT_ASSIGNMENT_STUDENT }
                    span { +": ${student?.name ?: "-"}" }
                }
                p {
                    strong { +TEXT_ASSIGNMENT_STATUS }
                    span { +": ${statusLabel(assignment.status)}" }
                }
                p {
                    strong { +TEXT_ASSIGNMENT_DUE }
                    span { +": ${assignment.dueDate?.format(dateFormatter) ?: "-"}" }
                }
                p { a(href = "/teacher/assignments/${assignment.id}/reassign") { +TEXT_ASSIGNMENT_REASSIGN } }

                h2 { +TEXT_COMMENTS }
                if (comments.isEmpty()) {
                    p { +TEXT_NO_COMMENTS }
                } else {
                    ul {
                        comments.forEach { comment ->
                            val author = authors[comment.authorId]
                            li {
                                p {
                                    strong { +(author?.name ?: "-") }
                                    span { +" (${roleLabel(author)}) • ${comment.createdAt.format(commentDateFormatter)}" }
                                }
                                p { +comment.text }
                            }
                        }
                    }
                }
                form(action = "/teacher/assignments/${assignment.id}/comments", method = FormMethod.post) {
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
                p { a(href = "/teacher/deadlines") { +TEXT_BACK } }
                p { a(href = "/teacher") { +TEXT_BACK } }
            }
        }
    }

    post("/teacher/assignments/{id}/comments") {
        val teacher = call.requireTeacher(userRepo) ?: return@post
        val assignmentId = call.parameters["id"]?.toIntOrNull()
        if (assignmentId == null) {
            call.respondText(TEXT_BAD_REQUEST, status = HttpStatusCode.BadRequest)
            return@post
        }
        val assignment = assignmentRepo.getById(assignmentId)
        if (assignment == null) {
            call.respondText(TEXT_ASSIGNMENT_NOT_FOUND, status = HttpStatusCode.NotFound)
            return@post
        }
        val text = call.receiveParameters()["text"]?.trim().orEmpty()
        if (text.isNotBlank()) {
            commentRepo.create(assignment.id, teacher.id, text)
        }
        call.respondRedirect("/teacher/assignments/$assignmentId")
    }

    get("/teacher/deadlines") {
        call.requireTeacher(userRepo) ?: return@get
        val statusFilter = call.request.queryParameters["status"]?.uppercase()?.let {
            runCatching { AssignmentStatus.valueOf(it) }.getOrNull()
        }
        val groupIdFilter = call.request.queryParameters["groupId"]?.toLongOrNull()
        val sortAscending = call.request.queryParameters["sort"]?.lowercase() != "desc"

        val assignments = assignmentRepo.findWithFilters(statusFilter, groupIdFilter, sortAscending)
        val today = LocalDate.now()
        val groups = groupRepo.getAll()

        val items = assignments.map { relation ->
            DeadlinesItem(
                relations = relation,
                isOverdue = isOverdue(relation.assignment, today)
            )
        }

        call.respondHtml {
            head {
                meta { charset = "UTF-8" }
                title { +TEXT_DEADLINES_HEADER }
            }
            body {
                h1 { +TEXT_DEADLINES_HEADER }
                form(action = "/teacher/deadlines", method = FormMethod.get) {
                    p {
                        label { +"$TEXT_FILTER_STATUS: " }
                        select {
                            name = "status"
                            option {
                                value = ""
                                if (statusFilter == null) selected = true
                                +TEXT_ALL
                            }
                            AssignmentStatus.values().forEach { status ->
                                option {
                                    value = status.name
                                    if (statusFilter == status) selected = true
                                    +statusLabel(status)
                                }
                            }
                        }
                    }
                    p {
                        label { +"$TEXT_FILTER_GROUP: " }
                        select {
                            name = "groupId"
                            option {
                                value = ""
                                if (groupIdFilter == null) selected = true
                                +TEXT_ALL
                            }
                            groups.forEach { group ->
                                option {
                                    value = group.id.toString()
                                    if (groupIdFilter?.toInt() == group.id) selected = true
                                    +group.name
                                }
                            }
                        }
                    }
                    p {
                        label { +"$TEXT_FILTER_SORT: " }
                        select {
                            name = "sort"
                            option {
                                value = "asc"
                                if (sortAscending) selected = true
                                +TEXT_SORT_ASC
                            }
                            option {
                                value = "desc"
                                if (!sortAscending) selected = true
                                +TEXT_SORT_DESC
                            }
                        }
                    }
                    button { +TEXT_FILTER_APPLY }
                }
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
                                th { +TEXT_DETAILS }
                            }
                        }
                        tbody {
                            items.forEach { item ->
                                tr {
                                    if (item.isOverdue) {
                                        attributes["style"] = "background-color:#ffe6e6;"
                                    }
                                    td { +item.relations.material.title }
                                    td {
                                        a(href = "/teacher/students/${item.relations.student.id}") { +item.relations.student.name }
                                    }
                                    td {
                                        +(item.relations.assignment.dueDate?.format(dateFormatter) ?: "-")
                                        if (item.isOverdue) {
                                            span {
                                                style = "color: red; margin-left: 6px;"
                                                +TEXT_OVERDUE
                                            }
                                        }
                                    }
                                    td { +statusLabel(item.relations.assignment.status) }
                                    td {
                                        a(href = "/teacher/assignments/${item.relations.assignment.id}") { +TEXT_ASSIGN_DETAILS }
                                    }
                                }
                            }
                        }
                    }
                }
                p { a(href = "/teacher") { +TEXT_BACK } }
            }
        }
    }

    get("/teacher/students/{id}") {
        call.requireTeacher(userRepo) ?: return@get
        val studentId = call.parameters["id"]?.toIntOrNull()
        if (studentId == null) {
            call.respondText(TEXT_BAD_REQUEST, status = HttpStatusCode.BadRequest)
            return@get
        }
        val student = userRepo.getById(studentId)
        if (student == null || student.role != UserRole.STUDENT) {
            call.respondText(TEXT_STUDENT_NOT_FOUND, status = HttpStatusCode.NotFound)
            return@get
        }
        val report = assignmentRepo.getStudentAssignmentsReport(studentId)
        val total = report.assignments.size
        val completed = report.assignments.count { it.assignment.status == AssignmentStatus.COMPLETED }
        val overdue = report.overdueCount
        val today = LocalDate.now()

        call.respondHtml {
            head {
                meta { charset = "UTF-8" }
                title { +"$TEXT_STUDENT_REPORT: ${student.name}" }
            }
            body {
                h1 { +"$TEXT_STUDENT_REPORT: ${student.name}" }
                p {
                    +"$TEXT_TOTAL: $total | $TEXT_COMPLETED: $completed | $TEXT_OVERDUE_COUNT: $overdue"
                }
                h2 { +TEXT_REPORT_ASSIGNMENTS }
                if (report.assignments.isEmpty()) {
                    p { +TEXT_NO_ASSIGNMENTS }
                } else {
                    table {
                        thead {
                            tr {
                                th { +TEXT_ASSIGNMENT_MATERIAL }
                                th { +TEXT_STATUS }
                                th { +TEXT_DUE_DATE }
                                th { +TEXT_DETAILS }
                            }
                        }
                        tbody {
                            report.assignments.forEach { item ->
                                val overdueRow = isOverdue(item.assignment, today)
                                tr {
                                    if (overdueRow) {
                                        attributes["style"] = "background-color:#ffe6e6;"
                                    }
                                    td {
                                        item.material.fileUrl?.takeIf { it.isNotBlank() }?.let { url ->
                                            a(href = url) { +item.material.title }
                                        } ?: span { +item.material.title }
                                    }
                                    td { +statusLabel(item.assignment.status) }
                                    td {
                                        +(item.assignment.dueDate?.format(dateFormatter) ?: "-")
                                        if (overdueRow) {
                                            span {
                                                style = "color: red; margin-left: 6px;"
                                                +TEXT_OVERDUE
                                            }
                                        }
                                    }
                                    td { a(href = "/teacher/assignments/${item.assignment.id}") { +TEXT_DETAILS } }
                                }
                            }
                        }
                    }
                }
                p { a(href = "/teacher/deadlines") { +TEXT_BACK_TO_DEADLINES } }
                p { a(href = "/teacher") { +TEXT_BACK } }
            }
        }
    }
}

private suspend fun ApplicationCall.renderAssignPage(
    materials: List<Material>,
    students: List<User>,
    groups: List<Group>,
    success: Boolean,
    defaults: AssignFormDefaults,
    reassignFrom: Int?
) {
    val dueValue = defaults.dueDate?.format(DateTimeFormatter.ISO_DATE)
    respondHtml {
        head {
            meta { charset = "UTF-8" }
            title { +TEXT_ASSIGN_TITLE }
        }
        body {
            h1 { +TEXT_ASSIGN_HEADER }
            reassignFrom?.let { p { +"$TEXT_REASSIGN_NOTICE$it" } }
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
                                if (defaults.materialId == material.id) selected = true
                                +material.title
                            }
                        }
                    }
                }
                p {
                    label { +TEXT_ASSIGN_GROUP_LABEL }
                    select {
                        name = "groupId"
                        option {
                            value = ""
                            if (defaults.groupId == null) selected = true
                            +TEXT_NO_GROUP
                        }
                        groups.forEach { group ->
                            option {
                                value = group.id.toString()
                                if (defaults.groupId == group.id) selected = true
                                +group.name
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
                                if (defaults.studentId == student.id) selected = true
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
                        if (!dueValue.isNullOrBlank()) {
                            value = dueValue
                        }
                    }
                }
                button { +TEXT_ASSIGN_BUTTON }
            }
            p { a(href = "/teacher") { +TEXT_BACK } }
        }
    }
}

private fun statusLabel(status: AssignmentStatus): String =
    when (status) {
        AssignmentStatus.ASSIGNED -> TEXT_STATUS_ASSIGNED
        AssignmentStatus.DOWNLOADED -> TEXT_STATUS_DOWNLOADED
        AssignmentStatus.COMPLETED -> TEXT_STATUS_COMPLETED
    }

private fun roleLabel(user: User?): String =
    when (user?.role) {
        UserRole.TEACHER -> TEXT_AUTHOR_TEACHER
        UserRole.STUDENT -> TEXT_AUTHOR_STUDENT
        else -> "-"
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
