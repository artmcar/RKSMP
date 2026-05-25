package org.example.data.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object UsersTable : IntIdTable("users") {
    val username = varchar("username", 64).uniqueIndex()
    val passwordHash = varchar("password_hash", 128)
    val role = varchar("role", 32).default("user")
}

object PrizesTable : IntIdTable("prizes") {
    val awardYear = integer("award_year")
    val category = varchar("category", 32)
    val fullName = varchar("full_name", 128)
    val motivation = text("motivation")
    val detailLink = text("detail_link").nullable()

    init {
        uniqueIndex("prizes_year_category", awardYear, category)
    }
}

object LaureatesTable : IntIdTable("laureates") {
    val prizeId = reference("prize_id", PrizesTable)
    val fullName = varchar("full_name", 128)
    val portion = varchar("portion", 16)
    val motivation = text("motivation")
    val portraitUrl = text("portrait_url").nullable()
}

object UserPrizesTable : Table("user_prizes") {
    val userId = reference("user_id", UsersTable)
    val prizeId = reference("prize_id", PrizesTable)
    val addedAt = datetime("added_at")

    override val primaryKey = PrimaryKey(userId, prizeId)
}