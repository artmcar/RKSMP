package org.example.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

data class JwtSettings(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val expirationMinutes: Long
)

class JwtConfig(private val settings: JwtSettings) {
    private val algorithm = Algorithm.HMAC256(settings.secret)
    val realm: String get() = settings.realm

    val verifier: JWTVerifier = JWT.require(algorithm)
        .withAudience(settings.audience)
        .withIssuer(settings.issuer)
        .build()

    data class TokenResult(val token: String, val expiresAtEpochMs: Long)

    fun generate(userId: Int, username: String, role: String): TokenResult {
        val expiresAt = System.currentTimeMillis() + settings.expirationMinutes * 60_000
        val token = JWT.create()
            .withAudience(settings.audience)
            .withIssuer(settings.issuer)
            .withClaim("uid", userId)
            .withClaim("username", username)
            .withClaim("role", role)
            .withExpiresAt(Date(expiresAt))
            .sign(algorithm)
        return TokenResult(token, expiresAt)
    }
}
