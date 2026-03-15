package com.artmcar.rksmp_13

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MyViewModel : ViewModel() {

    private val _rate = MutableStateFlow(90.5)
    val rate: StateFlow<Double> = _rate
    var newRate = 90.5

    private var previousRate = _rate.value

    val isGrowing = MutableStateFlow<Boolean?>(null)

    init {
        viewModelScope.launch {
            while (true) {
                delay(5000)
                generateNewRate()
            }
        }
    }
    fun generateNewRate() {
        previousRate = _rate.value
        if(Random.nextBoolean()){
            newRate = 90.5 + 2.5
        }
        else{
            newRate = 90.5 - 2.5
        }
        _rate.value = newRate
        isGrowing.value = when {
            newRate > previousRate -> true
            newRate < previousRate -> false
            else -> null
        }
    }
}
