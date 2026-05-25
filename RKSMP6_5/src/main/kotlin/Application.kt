package org.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.di.AppModule
import org.example.plugins.*
import org.example.routing.authRoutes
import org.example.routing.publicPrizeRoutes
import org.example.routing.userRoutes

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    val deps = AppModule(environment)

    configureContentNegotiation()
    configureCORS()
    configureCallLogging()
    configureStatusPages()
    configureAuthentication(deps.jwt)

    routing {
        get("/") { call.respondText("Работает") }

        get("/openapi.yaml") {
            val stream = this::class.java.classLoader.getResourceAsStream("openapi.yaml")
            if (stream == null) call.respond(HttpStatusCode.NotFound)
            else call.respondBytes(stream.readBytes(), ContentType("application", "yaml"))
        }
        get("/docs") {
            val stream = this::class.java.classLoader.getResourceAsStream("redoc.html")
            if (stream == null) call.respond(HttpStatusCode.NotFound)
            else call.respondBytes(stream.readBytes(), ContentType.Text.Html)
        }

        authRoutes(deps)
        publicPrizeRoutes(deps)
        userRoutes(deps)
    }
}