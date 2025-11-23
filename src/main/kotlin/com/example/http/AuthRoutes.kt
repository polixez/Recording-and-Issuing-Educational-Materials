package com.example.http

import com.example.UserSession
import com.example.domain.model.UserRole
import com.example.domain.repository.Repositories
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.set
import io.ktor.server.sessions.sessions
import kotlinx.html.FormMethod
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.select
import kotlinx.html.title

private const val TEXT_LOGIN = "\u0412\u0445\u043e\u0434"
private const val TEXT_NAME = "\u0418\u043c\u044f: "
private const val TEXT_ROLE = "\u0420\u043e\u043b\u044c: "
private const val TEXT_TEACHER = "\u041f\u0440\u0435\u043f\u043e\u0434\u0430\u0432\u0430\u0442\u0435\u043b\u044c"
private const val TEXT_STUDENT = "\u0421\u0442\u0443\u0434\u0435\u043d\u0442"
private const val TEXT_SUBMIT = "\u0412\u043e\u0439\u0442\u0438"

fun Route.authRoutes() {
    val userRepo = Repositories.userRepository

    get("/login") {
        val existingSession = call.sessions.get<UserSession>()
        if (existingSession != null) {
            val user = userRepo.getById(existingSession.userId)
            if (user != null) {
                val redirectTarget = if (user.role == UserRole.TEACHER) "/teacher" else "/student"
                call.respondRedirect(redirectTarget)
                return@get
            }
        }

        call.respondHtml {
            head { title { +TEXT_LOGIN } }
            body {
                h1 { +TEXT_LOGIN }
                form(action = "/login", method = FormMethod.post) {
                    p {
                        label { +TEXT_NAME }
                        input {
                            name = "name"
                            required = true
                        }
                    }
                    p {
                        label { +TEXT_ROLE }
                        select {
                            name = "role"
                            option {
                                value = UserRole.TEACHER.name
                                +TEXT_TEACHER
                            }
                            option {
                                value = UserRole.STUDENT.name
                                +TEXT_STUDENT
                            }
                        }
                    }
                    button { +TEXT_SUBMIT }
                }
            }
        }
    }

    post("/login") {
        val params = call.receiveParameters()
        val name = params["name"]?.trim().orEmpty()
        val roleParam = params["role"]?.trim().orEmpty()
        val role = runCatching { UserRole.valueOf(roleParam) }.getOrNull() ?: UserRole.STUDENT

        if (name.isBlank()) {
            call.respondRedirect("/login")
            return@post
        }

        val user = userRepo.getByName(name) ?: userRepo.create(name = name, role = role)
        call.sessions.set(UserSession(userId = user.id))

        val redirectTarget = if (user.role == UserRole.TEACHER) "/teacher" else "/student"
        call.respondRedirect(redirectTarget)
    }

    get("/logout") {
        call.sessions.clear<UserSession>()
        call.respondRedirect("/")
    }
}
