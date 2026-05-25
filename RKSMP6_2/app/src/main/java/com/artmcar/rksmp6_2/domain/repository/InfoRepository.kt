package com.artmcar.rksmp6_2.domain.repository

import com.artmcar.rksmp6_2.domain.model.LaureateDetail
import com.artmcar.rksmp6_2.domain.model.LaureateItem


interface InfoRepository {
    suspend fun getLaureates(year: Int?, category: String?): List<LaureateItem>
    suspend fun getLaureateDetail(id: String): LaureateDetail
}