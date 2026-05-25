package com.artmcar.rksmp6_2.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NobelPrizesResponseDto(
    val nobelPrizes: List<NobelPrizeDto> = emptyList()
)

@Serializable
data class NobelPrizeDto(
    val awardYear: String,
    val category: LocalizedDto,
    val categoryFullName: LocalizedDto? = null,
    val laureates: List<LaureateShortDto> = emptyList(),
    val links: List<LinkDto> = emptyList(),
    val motivation: LocalizedDto? = null
)

@Serializable
data class LocalizedDto(
    val en: String? = null,
    val no: String? = null,
    val se: String? = null
)

@Serializable
data class LaureateShortDto(
    val id: String,
    val knownName: LocalizedDto? = null,
    val fullName: LocalizedDto? = null,
    val portion: String? = null,
    val sortOrder: String? = null,
    val motivation: LocalizedDto? = null
)

@Serializable
data class LinkDto(
    val rel: String? = null,
    val href: String? = null
)

@Serializable
data class LaureateDetailDto(
    val id: String,
    val knownName: LocalizedDto? = null,
    val fullName: LocalizedDto? = null,
    val gender: String? = null,
    val birth: BirthDeathDto? = null,
    val death: BirthDeathDto? = null,
    @SerialName("nobelPrizes") val prizes: List<NobelPrizeDto> = emptyList()
)

@Serializable
data class BirthDeathDto(
    val date: String? = null,
    val place: PlaceDto? = null
)

@Serializable
data class PlaceDto(
    val city: LocalizedDto? = null,
    val country: LocalizedDto? = null,
    val locationString: LocalizedDto? = null
)

class RemoteApi(private val client: HttpClient) {
    companion object {
        const val BASE = "https://api.nobelprize.org/2.1"
    }

    suspend fun getPrizes(
        limit: Int = 25,
        offset: Int = 0,
        year: Int? = null,
        category: String? = null
    ): NobelPrizesResponseDto = client.get("$BASE/nobelPrizes") {
        parameter("limit", limit)
        parameter("offset", offset)
        year?.let { parameter("nobelPrizeYear", it) }
        category?.let { parameter("nobelPrizeCategory", it) }
    }.body()

    suspend fun getLaureate(id: String): List<LaureateDetailDto> =
        client.get("$BASE/laureate/$id").body()
}

