package org.example.routing

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.di.AppModule
import org.example.domain.ErrorResponse
import org.example.domain.LoginRequest
import org.example.domain.LoginResponse
import org.example.domain.MessageResponse
import org.example.domain.UserProfile


fun Route.authRoutes(module: AppModule) {
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
        val token = module.jwt.generate(user.id, user.username, user.role)
        call.respond(LoginResponse(token.token, token.expiresAtEpochMs))
    }
}

fun Route.publicPrizeRoutes(module: AppModule) {
    get("/prizes") {
        call.respond(module.getAllPrizes())
    }
}

fun Route.userRoutes(module: AppModule) {
    authenticate("auth-jwt") {
        route("/users/me") {
            get {
                val principal = call.principal<JWTPrincipal>()!!
                val username = principal.payload.getClaim("username").asString()
                val user = module.getProfile(username)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Profile not found"))
                } else {
                    call.respond(UserProfile(user.id, user.username, user.role))
                }
            }
            get("/prizes") {
                val principal = call.principal<JWTPrincipal>()!!
                val uid = principal.payload.getClaim("uid").asInt()
                call.respond(module.getFavorites(uid))
            }
            post("/prizes/{prizeId}") {
                val principal = call.principal<JWTPrincipal>()!!
                val uid = principal.payload.getClaim("uid").asInt()
                val prizeId = call.parameters["prizeId"]?.toIntOrNull()
                if (prizeId == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad prizeId"))
                    return@post
                }
                val ok = module.addFavorite(uid, prizeId)
                if (!ok) call.respond(HttpStatusCode.NotFound, ErrorResponse("Prize not found"))
                else call.respond(MessageResponse("Added"))
            }
            delete("/prizes/{prizeId}") {
                val principal = call.principal<JWTPrincipal>()!!
                val uid = principal.payload.getClaim("uid").asInt()
                val prizeId = call.parameters["prizeId"]?.toIntOrNull()
                if (prizeId == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad prizeId"))
                    return@delete
                }
                val ok = module.removeFavorite(uid, prizeId)
                if (!ok) call.respond(HttpStatusCode.NotFound, ErrorResponse("Not in favorites"))
                else call.respond(MessageResponse("Removed"))
            }
        }
    }
}