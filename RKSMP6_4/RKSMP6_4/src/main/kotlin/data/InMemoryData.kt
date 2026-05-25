package org.example.data

import org.example.domain.*

class InMemoryPrizeRepository : PrizeRepository {
    private val prizes: List<NobelPrize> = listOf(
        NobelPrize(
            awardYear = 2023,
            category = "physics",
            categoryFullName = "The Nobel Prize in Physics",
            prizeAmount = 11_000_000,
            dateAwarded = "2023-12-10",
            laureates = listOf(
                Laureate("1030", "Pierre Agostini", "1/3",
                    "for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter"),
                Laureate("1031", "Ferenc Krausz", "1/3",
                    "for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter"),
                Laureate("1032", "Anne L'Huillier", "1/3",
                    "for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter")
            )
        ),
        NobelPrize(
            awardYear = 2023,
            category = "chemistry",
            categoryFullName = "The Nobel Prize in Chemistry",
            prizeAmount = 11_000_000,
            dateAwarded = "2023-12-10",
            laureates = listOf(
                Laureate("1033", "Moungi Bawendi", "1/3", "for the discovery and synthesis of quantum dots"),
                Laureate("1034", "Louis Brus", "1/3", "for the discovery and synthesis of quantum dots"),
                Laureate("1035", "Aleksey Yekimov", "1/3", "for the discovery and synthesis of quantum dots")
            )
        ),
        NobelPrize(
            awardYear = 2023,
            category = "literature",
            categoryFullName = "The Nobel Prize in Literature",
            prizeAmount = 11_000_000,
            dateAwarded = "2023-10-05",
            laureates = listOf(
                Laureate("1036", "Jon Fosse", "1/1",
                    "for his innovative plays and prose which give voice to the unsayable")
            )
        ),
        NobelPrize(
            awardYear = 2023,
            category = "peace",
            categoryFullName = "The Nobel Peace Prize",
            prizeAmount = 11_000_000,
            dateAwarded = "2023-10-06",
            laureates = listOf(
                Laureate("1037", "Narges Mohammadi", "1/1",
                    "for her fight against the oppression of women in Iran and her fight to promote human rights and freedom for all")
            )
        ),
        NobelPrize(
            awardYear = 2022,
            category = "physics",
            categoryFullName = "The Nobel Prize in Physics",
            prizeAmount = 10_000_000,
            dateAwarded = "2022-12-10",
            laureates = listOf(
                Laureate("1000", "Alain Aspect", "1/3",
                    "for experiments with entangled photons, establishing the violation of Bell inequalities and pioneering quantum information science"),
                Laureate("1001", "John F. Clauser", "1/3",
                    "for experiments with entangled photons, establishing the violation of Bell inequalities and pioneering quantum information science"),
                Laureate("1002", "Anton Zeilinger", "1/3",
                    "for experiments with entangled photons, establishing the violation of Bell inequalities and pioneering quantum information science")
            )
        ),
        NobelPrize(
            awardYear = 2022,
            category = "medicine",
            categoryFullName = "The Nobel Prize in Physiology or Medicine",
            prizeAmount = 10_000_000,
            dateAwarded = "2022-10-03",
            laureates = listOf(
                Laureate("900", "Svante Pääbo", "1/1",
                    "for his discoveries concerning the genomes of extinct hominins and human evolution")
            )
        )
    )

    override suspend fun all(): List<NobelPrize> = prizes

    override suspend fun byYearAndCategory(year: Int, category: String): NobelPrize? =
        prizes.firstOrNull {
            it.awardYear == year && it.category.equals(category, ignoreCase = true)
        }

    override suspend fun laureatesFor(year: Int, category: String): List<Laureate> =
        byYearAndCategory(year, category)?.laureates ?: emptyList()
}

class InMemoryUserRepository : UserRepository {
    private val users = listOf(
        AppUser(1, "admin", "secret123", "admin"),
        AppUser(2, "user",  "userpass", "user")
    )
    override suspend fun findByUsername(username: String): AppUser? =
        users.firstOrNull { it.username == username }
}
