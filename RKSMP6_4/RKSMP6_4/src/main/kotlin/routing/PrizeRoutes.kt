package org.example.routing

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.di.ApplicationModule
import org.example.domain.ErrorResponse


fun Route.prizeRoutes(module: ApplicationModule) {
    authenticate("auth-jwt") {
        route("/prizes") {
            get {
                call.respond(module.getAllPrizes())
            }
            get("/{year}/{category}") {
                val year = call.parameters["year"]?.toIntOrNull()
                val category = call.parameters["category"]
                if (year == null || category.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad path params"))
                    return@get
                }
                val prize = module.getPrize(year, category)
                if (prize == null) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Prize not found"))
                } else {
                    call.respond(prize)
                }
            }
            get("/{year}/{category}/laureates") {
                val year = call.parameters["year"]?.toIntOrNull()
                val category = call.parameters["category"]
                if (year == null || category.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad path params"))
                    return@get
                }
                val list = module.getLaureates(year, category)
                call.respond(list)
            }
        }
    }
}