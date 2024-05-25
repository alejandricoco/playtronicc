package com.example.playtronic

data class Partido(
    val id: String = "",
    val creadoPor: String = "",
    val deporte: String = "",
    val horarioPreferido: String = "",
    val nivelOponente: String = "",
    val contador: Int = 0,
    val usuariosUnidos: List<String> = listOf()
)