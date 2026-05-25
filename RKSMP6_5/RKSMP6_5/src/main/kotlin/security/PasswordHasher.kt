package org.example.security

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordHasher {
    fun hash(raw: String): String =
        BCrypt.withDefaults().hashToString(10, raw.toCharArray())

    fun verify(raw: String, hash: String): Boolean =
        BCrypt.verifyer().verify(raw.toCharArray(), hash).verified
}