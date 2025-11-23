package com.example.http

import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.teacherRoutes() {
    get("/teacher") {
        call.respondText("Teacher area")
    }
}
