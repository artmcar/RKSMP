package org.example.data.repository

import org.example.data.database.DatabaseFactory.dbQuery
import org.example.data.database.LaureatesTable
import org.example.data.database.PrizesTable
import org.example.domain.Laureate
import org.example.domain.Prize
import org.example.domain.PrizeRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll

private fun ResultRow.toPrize(laureates: List<Laureate>) = Prize(
    id = this[PrizesTable.id].value,
    awardYear = this[PrizesTable.awardYear],
    category = this[PrizesTable.category],
    fullName = this[PrizesTable.fullName],
    motivation = this[PrizesTable.motivation],
    detailLink = this[PrizesTable.detailLink],
    laureates = laureates
)

private fun ResultRow.toLaureate() = Laureate(
    id = this[LaureatesTable.id].value,
    prizeId = this[LaureatesTable.prizeId].value,
    fullName = this[LaureatesTable.fullName],
    portion = this[LaureatesTable.portion],
    motivation = this[LaureatesTable.motivation],
    portraitUrl = this[LaureatesTable.portraitUrl]
)

class PrizeRepositoryImpl : PrizeRepository {
    override suspend fun all(): List<Prize> = dbQuery {
        val allLaureates = LaureatesTable.selectAll().map { it.toLaureate() }.groupBy { it.prizeId }
        PrizesTable.selectAll()
            .orderBy(PrizesTable.awardYear, org.jetbrains.exposed.sql.SortOrder.DESC)
            .map { row -> row.toPrize(allLaureates[row[PrizesTable.id].value] ?: emptyList()) }
    }

    override suspend fun findById(id: Int): Prize? = dbQuery {
        val laureates = LaureatesTable.selectAll()
            .where { LaureatesTable.prizeId eq id }
            .map { it.toLaureate() }
        PrizesTable.selectAll()
            .where { PrizesTable.id eq id }
            .map { it.toPrize(laureates) }
            .singleOrNull()
    }

    override suspend fun ensureSeed(): Unit = dbQuery {
        val hasAny = PrizesTable.selectAll().any()
        if (hasAny) return@dbQuery

        SeedData.PRIZES.forEach { seed ->
            val prizeId = PrizesTable.insertAndGetId {
                it[awardYear] = seed.year
                it[category] = seed.category
                it[fullName] = seed.fullName
                it[motivation] = seed.motivation
                it[detailLink] = seed.detailLink
            }.value
            seed.laureates.forEach { lSeed ->
                LaureatesTable.insertAndGetId {
                    it[LaureatesTable.prizeId] = org.jetbrains.exposed.dao.id.EntityID(prizeId, PrizesTable)
                    it[fullName] = lSeed.fullName
                    it[portion] = lSeed.portion
                    it[motivation] = lSeed.motivation
                    it[portraitUrl] = lSeed.portraitUrl
                }
            }
        }
    }
}

private data class PrizeSeed(
    val year: Int, val category: String, val fullName: String,
    val motivation: String, val detailLink: String?,
    val laureates: List<LaureateSeed>
)
private data class LaureateSeed(
    val fullName: String, val portion: String,
    val motivation: String, val portraitUrl: String?
)

private object SeedData {
    val PRIZES = listOf(
        PrizeSeed(2023, "physics", "The Nobel Prize in Physics",
            "for experimental methods that generate attosecond pulses of light",
            "https://www.nobelprize.org/prizes/physics/2023/",
            listOf(
                LaureateSeed("Pierre Agostini", "1/3",
                    "for experimental methods that generate attosecond pulses of light", null),
                LaureateSeed("Ferenc Krausz", "1/3",
                    "for experimental methods that generate attosecond pulses of light", null),
                LaureateSeed("Anne L'Huillier", "1/3",
                    "for experimental methods that generate attosecond pulses of light", null)
            )),
        PrizeSeed(2023, "chemistry", "The Nobel Prize in Chemistry",
            "for the discovery and synthesis of quantum dots",
            "https://www.nobelprize.org/prizes/chemistry/2023/",
            listOf(
                LaureateSeed("Moungi Bawendi", "1/3", "quantum dots", null),
                LaureateSeed("Louis Brus", "1/3", "quantum dots", null),
                LaureateSeed("Aleksey Yekimov", "1/3", "quantum dots", null)
            )),
        PrizeSeed(2023, "literature", "The Nobel Prize in Literature",
            "for his innovative plays and prose which give voice to the unsayable",
            "https://www.nobelprize.org/prizes/literature/2023/",
            listOf(LaureateSeed("Jon Fosse", "1/1", "voice to the unsayable", null))),
        PrizeSeed(2023, "peace", "The Nobel Peace Prize",
            "for her fight against the oppression of women in Iran",
            "https://www.nobelprize.org/prizes/peace/2023/",
            listOf(LaureateSeed("Narges Mohammadi", "1/1",
                "oppression of women in Iran", null))),
        PrizeSeed(2022, "physics", "The Nobel Prize in Physics",
            "for experiments with entangled photons",
            "https://www.nobelprize.org/prizes/physics/2022/",
            listOf(
                LaureateSeed("Alain Aspect", "1/3", "entangled photons", null),
                LaureateSeed("John F. Clauser", "1/3", "entangled photons", null),
                LaureateSeed("Anton Zeilinger", "1/3", "entangled photons", null)
            )),
        PrizeSeed(2022, "medicine", "The Nobel Prize in Physiology or Medicine",
            "for his discoveries concerning the genomes of extinct hominins",
            "https://www.nobelprize.org/prizes/medicine/2022/",
            listOf(LaureateSeed("Svante Pääbo", "1/1",
                "genomes of extinct hominins", null)))
    )
}
