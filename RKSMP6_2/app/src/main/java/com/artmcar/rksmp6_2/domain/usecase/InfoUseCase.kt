package com.artmcar.rksmp6_2.domain.usecase

import com.artmcar.rksmp6_2.domain.model.LaureateDetail
import com.artmcar.rksmp6_2.domain.model.LaureateItem
import com.artmcar.rksmp6_2.domain.repository.InfoRepository


class GetLaureatesUseCase(private val repo: InfoRepository) {
    suspend operator fun invoke(year: Int?, category: String?): List<LaureateItem> =
        repo.getLaureates(year, category)
}

class GetLaureateDetailUseCase(private val repo: InfoRepository) {
    suspend operator fun invoke(id: String): LaureateDetail = repo.getLaureateDetail(id)
}