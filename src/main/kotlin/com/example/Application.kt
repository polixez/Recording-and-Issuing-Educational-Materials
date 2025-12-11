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
import java.io.File

/**
 * Данные сессии пользователя, хранящиеся в cookie `USER_SESSION`.
 */
data class UserSession(val userId: Int)

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    /**
     * Инициализирует БД, каталоги загрузок, логирование и cookie-сессии.
     * Cookie помечены HttpOnly и SameSite=Lax, secure берётся из конфига или наличия SSL-порта.
     */
    DatabaseFactory.init()
    val uploadDirPath = environment.config.propertyOrNull("ktor.myapp.uploadDir")?.getString() ?: "uploads"
    File(uploadDirPath).mkdirs()
    val secureCookie = environment.config.propertyOrNull("ktor.security.secureCookie")?.getString()?.toBoolean()
        ?: (environment.config.propertyOrNull("ktor.deployment.sslPort") != null)
    install(CallLogging)
    install(Sessions) {
        cookie<UserSession>("USER_SESSION") {
            cookie.path = "/"
            cookie.httpOnly = true
            cookie.extensions["SameSite"] = "Lax"
            cookie.secure = secureCookie
            cookie.maxAgeInSeconds = 60 * 60 * 12
        }
    }
    configureSerialization()
    configureRouting(uploadDirPath)
}
