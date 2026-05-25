package com.artmcar.rksmp6_2.domain.model

data class LaureateItem(
    val laureateId: String,
    val awardYear: String,
    val category: String,
    val fullName: String,
    val motivation: String
) {
    val motivationShort: String
        get() = if (motivation.length <= 100) motivation else motivation.take(100) + "…"
}

data class LaureateDetail(
    val id: String,
    val fullName: String,
    val awardYear: String,
    val category: String,
    val motivation: String,
    val birthCountry: String?,
    val birthPlace: String?,
    val portraitUrl: String?
)

enum class NobelCategory(val apiValue: String, val title: String) {
    PHYSICS("physics", "Physics"),
    CHEMISTRY("chemistry", "Chemistry"),
    LITERATURE("literature", "Literature"),
    PEACE("peace", "Peace"),
    MEDICINE("medicine", "Medicine"),
    ECONOMICS("economics", "Economics"),
}