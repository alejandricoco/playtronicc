package com.example.playtronic

data class Question (
    val text: String = "",
    val options: List<String> = listOf(),
    var selectedOption: String = ""
)