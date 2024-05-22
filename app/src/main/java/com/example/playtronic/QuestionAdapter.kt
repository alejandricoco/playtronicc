package com.example.playtronic

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playtronic.R

class QuestionAdapter(private val items: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val answers = MutableList(items.filterIsInstance<Question>().size) { "" }
    private val answerIndices = MutableList(items.filterIsInstance<Question>().size) { -1 }
    private var questionIndex = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.information_banner_item, parent, false)
            InformationBannerViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.question_item, parent, false)
            QuestionViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is Question -> {(holder as QuestionViewHolder).bind(item, position)

        }
        is InformationBanner -> (holder as InformationBannerViewHolder).bind(item)
    }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is InformationBanner) 0 else 1
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getAnswers(): List<String> {
        return answers
    }

    fun allQuestionsAnswered(): Boolean {
        return answers.all { it != "" }
    }

    inner class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val questionText: TextView = view.findViewById(R.id.question_text)
        private val optionsGroup: RadioGroup = view.findViewById(R.id.options_group)

        fun bind(question: Question, position: Int) {
            questionText.text = question.text
            optionsGroup.setOnCheckedChangeListener(null) // Eliminamos el listener para evitar que se llame varias veces
            optionsGroup.removeAllViews()
            val radioButtons = mutableListOf<RadioButton>()
            for (option in question.options) {
                val radioButton = RadioButton(optionsGroup.context).apply{

                    id = View.generateViewId()
                    text = option
                    isChecked = option == question.selectedOption
                }
                radioButton.id = View.generateViewId() // Genera un ID único
                radioButton.text = option
                radioButton.setTextColor(Color.WHITE)

                // Define los márgenes
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 10, 0, 10) // Establecemos los márgenes (izquierda, arriba, derecha, abajo)

                // Color del RadioButton cuando no está seleccionado
                val colorStateList = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_checked), // Estado cuando no está seleccionado
                        intArrayOf(android.R.attr.state_checked)  // Estado cuando está seleccionado
                    ),
                    intArrayOf(
                        Color.YELLOW, // Color cuando no está seleccionado
                        Color.YELLOW   // Color cuando está seleccionado
                    )
                )

                radioButton.buttonTintList = colorStateList

                radioButtons.add(radioButton)
                optionsGroup.addView(radioButton)
            }

            // Restauramos el estado de la selección de las respuestas
            val radioButton = optionsGroup.children.find { (it as RadioButton).text == question.selectedOption }
            radioButton?.let { optionsGroup.check(it.id) }

            // Establecemos el nuevo listener después de restaurar el estado
            optionsGroup.setOnCheckedChangeListener { _, checkedId ->
                val selectedButton = optionsGroup.findViewById<RadioButton>(checkedId)
                if (selectedButton != null) {
                    question.selectedOption = selectedButton.text.toString()
                    if (questionIndex < answers.size) {
                        answers[questionIndex] = selectedButton.text.toString()
                        answerIndices[questionIndex] = optionsGroup.indexOfChild(selectedButton)
                        questionIndex++
                    }
                }
            }
        }
    }

    inner class InformationBannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val bannerText: TextView = view.findViewById(R.id.banner_text)

        fun bind(banner: InformationBanner) {
            bannerText.text = banner.text
        }
    }

    fun getAnswersIndices(): List<Int> {
        return answerIndices
    }
}
