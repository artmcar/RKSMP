package org.example.data.repository

import org.example.data.database.DatabaseFactory.dbQuery
import org.example.data.database.LaureatesTable
import org.example.data.database.PrizesTable
import org.example.data.database.UserPrizesTable
import org.example.data.database.UsersTable
import org.example.domain.Laureate
import org.example.domain.Prize
import org.example.domain.FavoriteRepository
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDateTime
class FavoriteRepositoryImpl : FavoriteRepository {

    override suspend fun favoritesOf(userId: Int): List<Prize> = dbQuery {
        val prizeIds = UserPrizesTable.selectAll()
            .where { UserPrizesTable.userId eq userId }
            .map { it[UserPrizesTable.prizeId].value }
        if (prizeIds.isEmpty()) return@dbQuery emptyList()

        val laureatesByPrize: Map<Int, List<Laureate>> = LaureatesTable
            .selectAll()
            .where { LaureatesTable.prizeId inList prizeIds.map { EntityID(it, PrizesTable) } }
            .map {
                Laureate(
                    id = it[LaureatesTable.id].value,
                    prizeId = it[LaureatesTable.prizeId].value,
                    fullName = it[LaureatesTable.fullName],
                    portion = it[LaureatesTable.portion],
                    motivation = it[LaureatesTable.motivation],
                    portraitUrl = it[LaureatesTable.portraitUrl]
                )
            }
            .groupBy { it.prizeId }

        PrizesTable.selectAll()
            .where { PrizesTable.id inList prizeIds.map { EntityID(it, PrizesTable) } }
            .map { row ->
                val pid = row[PrizesTable.id].value
                Prize(
                    id = pid,
                    awardYear = row[PrizesTable.awardYear],
                    category = row[PrizesTable.category],
                    fullName = row[PrizesTable.fullName],
                    motivation = row[PrizesTable.motivation],
                    detailLink = row[PrizesTable.detailLink],
                    laureates = laureatesByPrize[pid] ?: emptyList()
                )
            }
    }

    override suspend fun add(userId: Int, prizeId: Int): Boolean = dbQuery {
        val prizeExists = PrizesTable.selectAll()
            .where { PrizesTable.id eq prizeId }.any()
        if (!prizeExists) return@dbQuery false
        val already = UserPrizesTable.selectAll().where {
            (UserPrizesTable.userId eq userId) and (UserPrizesTable.prizeId eq prizeId)
        }.any()
        if (already) return@dbQuery true
        UserPrizesTable.insert {
            it[UserPrizesTable.userId] = EntityID(userId, UsersTable)
            it[UserPrizesTable.prizeId] = EntityID(prizeId, PrizesTable)
            it[addedAt] = LocalDateTime.now()
        }
        true
    }

    override suspend fun remove(userId: Int, prizeId: Int): Boolean = dbQuery {
        val affected = UserPrizesTable.deleteWhere {
            (UserPrizesTable.userId eq userId) and (UserPrizesTable.prizeId eq prizeId)
        }
        affected > 0
    }
}