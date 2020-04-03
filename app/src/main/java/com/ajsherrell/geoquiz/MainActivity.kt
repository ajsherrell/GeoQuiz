package com.ajsherrell.geoquiz

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var questionTextView: TextView

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )
    private var currentIndex = 0
    private var lastIndex = 0
    private var score = 0

    private val quizViewModel: QuizViewModel by lazy {
        val factory = QuizViewModelFactory()
        ViewModelProvider(this@MainActivity, factory).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate is called!!!")
        setContentView(R.layout.activity_main)

        //depreciated
//        val provider: ViewModelProvider = ViewModelProviders.of(this)
//        val quizViewModel = provider.get(QuizViewModel::class.java)
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        previousButton = findViewById(R.id.previous_button)
        questionTextView = findViewById(R.id.question_text_view)

        lastIndex = questionBank.size - 1

        trueButton.setOnClickListener {
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            currentIndex = (currentIndex + 1) % questionBank.size
            if (currentIndex == lastIndex) {
                nextButton.isEnabled = false
            }
            isAnswered(currentIndex)
            updateQuestion()
        }

        previousButton.setOnClickListener {
            if (currentIndex != 0) {
                currentIndex -= 1
                updateQuestion()
            }
        }

        updateQuestion()

    }

    private fun isAnswered(index: Int) {
        val isQuestionAnswered = questionBank[index].answered
        trueButton.isEnabled = !isQuestionAnswered
        falseButton.isEnabled = !isQuestionAnswered
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart is called !!!")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume is called!!!")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause is called!!!")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop is called!!!")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart is called!!!")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy is called!!!")
    }

    private fun updateQuestion() {
        val questionTextResId = questionBank[currentIndex].textResId
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        // disable buttons when answered
        trueButton.isEnabled = false
        falseButton.isEnabled = false
        questionBank[currentIndex].answered = true

        val correctAnswer = questionBank[currentIndex].answer
        var messageId = ""
        if (userAnswer == correctAnswer) {
            score+=1
            messageId = getString(R.string.correct_toast)
            Log.d(TAG, "Current score is: $score")
        } else {
            messageId = getString(R.string.incorrect_toast)
        }
        if (questionBank[currentIndex] == questionBank[lastIndex]) {
            messageId = getString(R.string.final_score) + score.toString()
            previousButton.isEnabled = false
        }

        //reference: https://www.journaldev.com/96/android-toast-with-kotlin
        val toast = Toast.makeText(this, messageId, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 250)
        toast.show()
    }
}
