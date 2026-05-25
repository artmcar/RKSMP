package org.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.di.ApplicationModule
import org.example.plugin.*
import org.example.routing.authRoutes
import org.example.routing.prizeRoutes

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    val deps = ApplicationModule(environment)
    configureContentNegotiation()
    configureCORS()
    configureCallLogging()
    configureStatusPages()
    configureAuthentication(deps.jwt)

    routing {
        get("/") { call.respondText("Работает") }
        authRoutes(deps)
        prizeRoutes(deps)
    }
}