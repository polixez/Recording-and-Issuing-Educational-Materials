package com.example.plugins

import com.example.http.authRoutes
import com.example.http.studentRoutes
import com.example.http.teacherRoutes
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.title

private const val TITLE_MAIN = "\u0423\u0447\u0451\u0442 \u0438 \u0432\u044b\u0434\u0430\u0447\u0430 \u0443\u0447\u0435\u0431\u043d\u044b\u0445 \u043c\u0430\u0442\u0435\u0440\u0438\u0430\u043b\u043e\u0432"
private const val TEXT_LOGIN = "\u0412\u043e\u0439\u0442\u0438"

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondHtml {
                head { title { +TITLE_MAIN } }
                body {
                    h1 { +TITLE_MAIN }
                    a(href = "/login") { +TEXT_LOGIN }
                }
            }
        }
        get("/health") {
            call.respondText("OK")
        }
        authRoutes()
        teacherRoutes()
        studentRoutes()
    }
}
