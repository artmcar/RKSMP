package org.example.routing

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.di.ApplicationModule
import org.example.domain.ErrorResponse
import org.example.domain.LoginRequest
import org.example.domain.LoginResponse


fun Route.authRoutes(module: ApplicationModule) {
    route("/auth") {
        post("/login") {
            val body = try {
                call.receive<LoginRequest>()
            } catch (_: Throwable) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid body"))
                return@post
            }
            val user = module.authenticate(body.username, body.password)
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid credentials"))
                return@post
            }
            val token = module.jwt.generate(user.username, user.role)
            call.respond(LoginResponse(token.token, token.expiresAtEpochMs))
        }
    }
}