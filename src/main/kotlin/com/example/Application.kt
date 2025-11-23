package com.example

import com.example.database.DatabaseFactory
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie

data class UserSession(val userId: Int)

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    install(CallLogging)
    install(Sessions) {
        cookie<UserSession>("USER_SESSION") {
            cookie.path = "/"
        }
    }
    configureSerialization()
    configureRouting()
}
