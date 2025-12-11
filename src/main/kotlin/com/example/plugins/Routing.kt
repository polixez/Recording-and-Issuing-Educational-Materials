package com.example.plugins

import com.example.http.authRoutes
import com.example.http.studentRoutes
import com.example.http.teacherRoutes
import com.example.http.commonMetaAndStyles
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respondText
import io.ktor.server.http.content.staticFiles
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.title
import java.io.File

private const val TITLE_MAIN = "Учёт и выдача учебных материалов"
private const val TEXT_LOGIN = "Войти"
private const val TEXT_TEACHER = "Кабинет преподавателя"
private const val TEXT_STUDENT = "Кабинет студента"
private const val TEXT_WELCOME = "Простое приложение для хранения, выдачи и контроля выполнения учебных материалов."

fun Application.configureRouting(uploadDirPath: String) {
    routing {
        staticFiles("/files", File(uploadDirPath))
        get("/") {
            call.respondHtml {
                head {
                    commonMetaAndStyles()
                    title { +TITLE_MAIN }
                }
                body {
                    div(classes = "page") {
                        div(classes = "card") {
                            h1 { +TITLE_MAIN }
                            p(classes = "muted") { +TEXT_WELCOME }
                            div(classes = "stack") {
                                a(href = "/login", classes = "btn") { +TEXT_LOGIN }
                                a(href = "/teacher", classes = "btn secondary") { +TEXT_TEACHER }
                                a(href = "/student", classes = "btn secondary") { +TEXT_STUDENT }
                            }
                        }
                        div(classes = "grid") {
                            div(classes = "card") {
                                h2 { +TEXT_TEACHER }
                                p { +"Создание материалов, назначение студентам и группам, дедлайны, комментарии и отчёты." }
                            }
                            div(classes = "card") {
                                h2 { +TEXT_STUDENT }
                                p { +"Список назначений, скачивание файлов, отметка статусов и общение через комментарии." }
                            }
                        }
                    }
                }
            }
        }
        get("/health") {
            call.respondText("OK")
        }
        authRoutes()
        teacherRoutes(uploadDirPath)
        studentRoutes()
    }
}
