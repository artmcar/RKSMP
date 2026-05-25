package org.example.data.repository

import org.example.data.database.DatabaseFactory.dbQuery
import org.example.data.database.UsersTable
import org.example.domain.AppUser
import org.example.domain.UserRepository
import org.example.security.PasswordHasher
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

private fun ResultRow.toUser() = AppUser(
    id = this[UsersTable.id].value,
    username = this[UsersTable.username],
    passwordHash = this[UsersTable.passwordHash],
    role = this[UsersTable.role]
)

class UserRepositoryImpl : UserRepository {
    override suspend fun findByUsername(username: String): AppUser? = dbQuery {
        UsersTable.selectAll()
            .where { UsersTable.username eq username }
            .map { it.toUser() }
            .singleOrNull()
    }

    override suspend fun findById(id: Int): AppUser? = dbQuery {
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .map { it.toUser() }
            .singleOrNull()
    }

    override suspend fun ensureSeed(): Unit = dbQuery {
        listOf(
            Triple("admin", "secret123", "admin"),
            Triple("user", "userpass", "user")
        ).forEach { (name, pass, role) ->
            val exists = UsersTable.selectAll()
                .where { UsersTable.username eq name }
                .any()
            if (!exists) {
                UsersTable.insert {
                    it[username] = name
                    it[passwordHash] = PasswordHasher.hash(pass)
                    it[UsersTable.role] = role
                }
            }
        }
    }
}