package com.example.playtronic.fragments.fragmentsMenu

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class NivelView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint()
    private val puntos = mutableListOf<Float>()
    private val marginDp = 40f // Margen en dp
    private val density = context.resources.displayMetrics.density
    private val marginPx = marginDp * density
    private val textMarginDp = 3f // Margen para los números en dp
    private val textMarginPx = textMarginDp * density

    fun setNiveles(niveles: List<Float>) {
        puntos.clear()
        if (niveles.size > 6) {
            puntos.addAll(niveles.takeLast(6)) // Solo toma los últimos 6 puntos
        } else {
            puntos.addAll(niveles)
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = Color.YELLOW
        paint.strokeWidth = 5f
        paint.textSize = 60f // Doble del tamaño original

        val maxNivel = puntos.maxOrNull() ?: 1f
        val minNivel = puntos.minOrNull() ?: 0f
        val range = max(maxNivel - minNivel, 0.1f) // Asegura que el rango no sea cero

        var prevX = marginPx
        var prevY = height / 2f

        for ((index, nivel) in puntos.withIndex()) {
            val x = marginPx + (width - 2 * marginPx) / (puntos.size - 1) * index
            val y = marginPx + (height - 2 * marginPx) - ((nivel - minNivel) / range) * (height - 2 * marginPx) * (30f / 45f) // Ajusta la escala de los puntos y multiplica para obtener 30 grados

            if (index != 0) {
                canvas.drawLine(prevX, prevY, x, y, paint)
            }

            prevX = x
            prevY = y

            paint.color = Color.WHITE
            canvas.drawCircle(x, y, 19.5f, paint) // 30% más grande que el tamaño original (15f * 1.3)
            paint.color = Color.YELLOW

            // Dibuja el valor del punto, ajustando la posición según el nivel siguiente
            if (index < puntos.size - 1 && puntos[index + 1] > nivel) {
                // Si el siguiente punto es de nivel superior, el texto aparece abajo a la izquierda
                canvas.drawText(nivel.toString(), x - paint.measureText(nivel.toString()) / 2, y + 19.5f + textMarginPx + paint.textSize, paint)
            } else {
                // Si el siguiente punto es de nivel inferior o es el último punto, el texto aparece arriba a la derecha
                canvas.drawText(nivel.toString(), x, y - 19.5f - textMarginPx, paint)
            }
        }
    }
}
