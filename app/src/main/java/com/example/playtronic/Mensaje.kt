package com.example.playtronic

data class Mensaje(

        val contenido: String = "",
        val idRemitente: String= "",
        val nombreRemitente: String = "",
        val fechaEnvio: Long = 0L // Usamos un Long para representar la fecha en milisegundos
    )
