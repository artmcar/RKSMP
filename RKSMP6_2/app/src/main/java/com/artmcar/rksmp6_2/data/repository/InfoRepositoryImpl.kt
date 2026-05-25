package com.artmcar.rksmp6_2.data.repository

import com.artmcar.rksmp6_2.data.LaureateDetailDto
import com.artmcar.rksmp6_2.data.NobelPrizeDto
import com.artmcar.rksmp6_2.data.RemoteApi
import com.artmcar.rksmp6_2.domain.model.LaureateDetail
import com.artmcar.rksmp6_2.domain.model.LaureateItem
import com.artmcar.rksmp6_2.domain.repository.InfoRepository


private fun String?.orDash() = this ?: "—"

class InfoRepositoryImpl(private val api: RemoteApi) : InfoRepository {

    override suspend fun getLaureates(year: Int?, category: String?): List<LaureateItem> {
        val resp = api.getPrizes(limit = 50, year = year, category = category)
        return resp.nobelPrizes.flatMap { prize ->
            prize.laureates.map { l ->
                LaureateItem(
                    laureateId = l.id,
                    awardYear = prize.awardYear,
                    category = prize.category.en ?: "—",
                    fullName = l.fullName?.en ?: l.knownName?.en ?: "—",
                    motivation = l.motivation?.en ?: ""
                )
            }
        }
    }

    override suspend fun getLaureateDetail(id: String): LaureateDetail {
        val responseList = api.getLaureate(id)
        val d: LaureateDetailDto = responseList.firstOrNull()
            ?: throw NoSuchElementException("Лауреат с ID $id не найден")
        val prize: NobelPrizeDto? = d.prizes.firstOrNull()
        val fullName = d.fullName?.en ?: d.knownName?.en ?: "—"
        val awardYear = prize?.awardYear ?: "—"
        val category = prize?.category?.en ?: "—"
        val motivation = prize?.motivation?.en ?: ""
        val birthPlace = d.birth?.place?.locationString?.en ?: d.birth?.place?.city?.en
        val birthCountry = d.birth?.place?.country?.en
        return LaureateDetail(
            id = d.id,
            fullName = fullName,
            awardYear = awardYear,
            category = category,
            motivation = motivation,
            birthCountry = birthCountry,
            birthPlace = birthPlace,
            portraitUrl = null
        )
    }
}