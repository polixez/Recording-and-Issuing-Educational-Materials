package com.example

import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.callloging.CallLogging

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(CallLogging)
    configureSerialization()
    configureRouting()
}
