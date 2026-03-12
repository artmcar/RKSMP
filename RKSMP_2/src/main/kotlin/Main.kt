package org.example

import kotlinx.coroutines.*
import java.io.File
import java.security.MessageDigest

fun main() {
    runBlocking {
        val timeout = 10000L
        val path = "./dataDirectory"
        val directory = File(path)

        val res = withTimeoutOrNull(timeout) {
            val hashMap = mutableMapOf<String, MutableList<File>>()
            val listOfJsons = directory.walkTopDown().filter { it.isFile && it.extension == "json"}.toList()

            coroutineScope {
                val jobs = listOfJsons.map {
                    file -> async {
                        val hash = findSHA256(file)
                        synchronized(hashMap) {
                            hashMap.getOrPut(hash) { mutableListOf() }.add(file)
                        }
                    }
                }
                jobs.awaitAll()
            }
            hashMap.filter { it.value.size > 1 }
        }
        if (res == null){
            println("Поиск прерван по таймауту")
        }
        else {
            if (res.isEmpty()) {
                println("Нет дубликатов")
            } else {
                println("Дубликаты:")
                res.forEach { (hash, files) ->
                    files.forEach { println(it.path) }
                }
            }
        }
    }
}

suspend fun findSHA256(file: File): String = withContext(Dispatchers.IO) {
    val bytes = file.readBytes()
    val digest = MessageDigest.getInstance("SHA-256")
    digest.digest(bytes).joinToString("") { "%02x".format(it) }
}