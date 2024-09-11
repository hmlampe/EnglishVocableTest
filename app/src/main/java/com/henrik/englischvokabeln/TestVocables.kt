package com.henrik.englischvokabeln

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.henrik.englischvokabeln.databinding.ActivityTestVocablesBinding

class TestVocables : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityTestVocablesBinding
    private lateinit var askVocable: TextView
    private lateinit var answerVocable: TextView
    private lateinit var testVocable: Button
    private lateinit var stopTest: FloatingActionButton
    private lateinit var test: VocableTestData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestVocablesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askVocable = findViewById(R.id.textAsk)
        answerVocable = findViewById(R.id.textAnswer)
        testVocable = findViewById(R.id.button2)
        stopTest = findViewById(R.id.stopTestView)

        answerVocable.requestFocus()

        test = VocableListData.getTest()

        askVocable.text = test.getNextVocable()

        testVocable.setOnClickListener {
            if (testVocable.text == "Nächste") {
                if (!test.testFinished(this)) {
                    askVocable.text = test.getNextVocable()
                    answerVocable.text = ""
                    answerVocable.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                    testVocable.text = "Prüfen"
                } else {
                    showResultDialog()
                }
            } else if (testVocable.text == "Prüfen") {
                if (test.addResult(answerVocable.text.toString().trim())) {
                    answerVocable.setTextColor(
                        ContextCompat.getColor(
                            this,
                            android.R.color.holo_green_dark
                        )
                    )
                    testVocable.text = "Nächste"
                } else {
                    answerVocable.setTextColor(
                        ContextCompat.getColor(
                            this,
                            android.R.color.holo_red_dark
                        )
                    )
                    answerVocable.text = answerVocable.text.toString() + " --> richtig: " + test.getCorrectVocable()
                    testVocable.text = "Nächste"
                }
            }
        }

        stopTest.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }

    private fun showResultDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Testergebnis")
        val view = layoutInflater.inflate(R.layout.show_result_layout, null)
        builder.setView(view)

        val test = VocableListData.getTest()
        val resultPositive = view.findViewById<EditText>(R.id.resultPositivView)
        val resultNegative = view.findViewById<EditText>(R.id.resultNegativeView)

        resultPositive.setText(test.getPositive().toString())
        resultNegative.setText(test.getNegative().toString())

        builder.setPositiveButton("Test wiederholen") { dialog, _ ->
            VocableListData.startTest(0)
            val intent = Intent(this, TestVocables::class.java)
            startActivity(intent)
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}