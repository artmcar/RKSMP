package org.example

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() {
    runBlocking {
        val time = measureTimeMillis {
            val usersTask = async{
                try {
                    loadUsersList()
                }catch (e: Exception){
                    println("Что-то пошло не так: ${e.message}")
                    emptyList()
                }
            }
            val salesStatsTask = async{
                try {
                    loadSalesStats()
                }catch (e: Exception){
                    println("Что-то пошло не так: ${e.message}")
                    emptyMap()
                }
            }
            val weatherTask = async{
                try {
                    loadWeatherList()
                }catch (e: Exception){
                    println("Что-то пошло не так: ${e.message}")
                    emptyList()
                }
            }

            println("Список пользователей: ${usersTask.await()}\n" +
                    "Продажи за день: ${salesStatsTask.await()}\n" +
                    "Текущая погода: ${weatherTask.await()}")
        }
        println("Время выполнения: ${time/1000.0} сек.")
    }

}

suspend fun loadUsersList() : List<String>{
    delay(1800)
    val isCrashed: Boolean = (0..1).random() == 0
    if (isCrashed) throw RuntimeException("Произошлел сбой при загрузке списка пользователей")
    return listOf("Alice", "Bob", "Ivan", "Olga")
}

suspend fun loadSalesStats() : Map<String, Int>{
    delay(1200)
    val isCrashed: Boolean = (0..1).random() == 0
    if (isCrashed) throw RuntimeException("Произошлел сбой при загрузке продаж")
    return mapOf("Coffee" to 42, "Tea" to 19)
}

suspend fun loadWeatherList(): List<String> {
    delay(2500)
    val isCrashed: Boolean = (0..1).random() == 0
    if (isCrashed) throw RuntimeException("Произошлел сбой при загрузке погоды")
    return listOf("Moscow: -18°C, snow", "New York: -5°C, cloudy", "Tokyo: 11°C, rain")
}