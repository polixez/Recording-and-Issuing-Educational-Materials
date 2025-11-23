package com.example.plugins

import com.example.http.studentRoutes
import com.example.http.teacherRoutes
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.li
import kotlinx.html.title
import kotlinx.html.ul
import kotlinx.html.a

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondHtml {
                head { title { +"Учёт и выдача учебных материалов" } }
                body {
                    h1 { +"Учёт и выдача учебных материалов" }
                    ul {
                        li { a(href = "/teacher") { +"Войти как преподаватель" } }
                        li { a(href = "/student") { +"Войти как студент" } }
                    }
                }
            }
        }
        get("/health") {
            call.respondText("OK")
        }
        teacherRoutes()
        studentRoutes()
    }
}
