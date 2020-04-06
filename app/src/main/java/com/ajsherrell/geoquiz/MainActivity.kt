package com.ajsherrell.geoquiz

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
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
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0
private const val IS_CHEATER = "isCheater"

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var cheatButton: Button

    private val model: QuizViewModel by lazy {
        val factory = QuizViewModelFactory()
        ViewModelProvider(this@MainActivity, factory).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate is called!!!")
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        model.currentIndex = currentIndex
        model.isCheater = savedInstanceState?.getBoolean(IS_CHEATER, false) ?: false

        //depreciated
//        val provider: ViewModelProvider = ViewModelProviders.of(this)
//        val quizViewModel = provider.get(QuizViewModel::class.java)
        Log.d(TAG, "Got a QuizViewModel: $model")

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        previousButton = findViewById(R.id.previous_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheatButton = findViewById(R.id.cheat_button)

        trueButton.setOnClickListener {
            model.currentQuestionAnswered = true
            isAnswered()
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            model.currentQuestionAnswered = true
            isAnswered()
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            model.moveToNext()
            model.isCheater = false
            enableButtons()
            if (model.currentIndex == model.lastIndex) {
                nextButton.isEnabled = false
            }
            updateQuestion()
        }

        previousButton.setOnClickListener {
            if (model.currentIndex != 0) {
                model.currentIndex -= 1
                updateQuestion()
            }
        }

        cheatButton.setOnClickListener {
            //start cheat activity
            val answerIsTrue = model.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            val options = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val options = ActivityOptions.makeClipRevealAnimation(it, 0,0, it.width, it.height)
                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
            } else {
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
        }

        updateQuestion()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            model.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    private fun isAnswered() {
        if (model.currentQuestionAnswered) {
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        }
    }

    private fun enableButtons() {
        trueButton.isEnabled = true
        falseButton.isEnabled = true
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState is called!!!")
        outState.putInt(KEY_INDEX, model.currentIndex)
        outState.putBoolean(IS_CHEATER, model.isCheater)
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
//        Log.d(TAG, "Updating question text", Exception())
        val questionTextResId = model.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        model.currentQuestionAnswered = true
        val correctAnswer = model.currentQuestionAnswer
        if (userAnswer == correctAnswer) {
            model.score+=1
            Log.d(TAG, "Current score is: ${model.score}")
        }
        if (model.currentIndex == model.lastIndex) {
            previousButton.isEnabled = false
            cheatButton.isEnabled = false
            Toast.makeText(this, this.resources.getString(R.string.final_score, model.score), Toast.LENGTH_LONG).show()
        }
        val messageId = when {
            model.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
         }

        //reference: https://www.journaldev.com/96/android-toast-with-kotlin
        val toast = Toast.makeText(this, messageId, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 250)
        toast.show()
    }
}
