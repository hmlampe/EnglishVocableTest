package com.henrik.englischvokabeln

import android.content.Context
import android.content.Context.MODE_APPEND
import android.content.Context.MODE_PRIVATE
import java.io.OutputStreamWriter
import java.io.PrintWriter

class VocablesData(private val german: String, private val english: String) {

    var asked : Int = 0
    var correct : Int = 0
    var wrong : Int = 0

    override fun toString(): String {
        return "$german;$english;$asked;$correct;$wrong"
    }

    fun getGerman(): String {
        return german
    }

    fun getEnglish(): String {
        return english
    }

    fun tested(correct: Boolean) {
        asked++
        if (correct) {
            this.correct++
        } else {
            wrong++
        }
    }

    fun getScore(): Int {
        return correct - wrong
    }

}

class VocableTestData(private val vocableList: ArrayList<VocablesData>) {

    private var negative = 0
    private var positive = 0
    private lateinit var actual_vocable: VocablesData

    fun getNextVocable(): String {
        val index = (0 until vocableList.size).random()
        actual_vocable = vocableList[index]
        vocableList.removeAt(index)
        return actual_vocable.getGerman()
    }

    fun getCorrectVocable(): String {
        return actual_vocable.getEnglish()
    }

    fun testFinished(context: Context): Boolean {
        if (vocableList.isEmpty()) {
            VocableListData.writeFile(context)
            return true
        }
        return false
    }

    fun addResult(answer: String): Boolean {
        if (answer == actual_vocable.getEnglish()) {
            actual_vocable.correct++
            positive++
            return true
        } else {
            actual_vocable.wrong++
            negative++
            return false
        }
    }

    fun getPositive(): Int {
        return positive
    }

    fun getNegative(): Int {
        return negative
    }

}

object VocableListData {

    private val vocableList = ArrayList<VocablesData>()
    private val vocableTest = ArrayList<VocablesData>()
    private lateinit var test: VocableTestData
    private var testSize = 10
    private const val vocablesFileName = "vocabels.csv"

    fun initializeVocables(context: Context) {
        try {
//            val outputFile = context.openFileOutput(vocablesFileName, MODE_PRIVATE)
//            val output = "eins;one;0;0;0\n" +
//                    "zwei;two;0;0;0\n" +
//                    "drei;three;0;0;0\n" +
//                    "vier;four;0;0;0\n" +
//                    "fünf;five;0;0;0\n" +
//                    "sechs;six;0;0;0\n" +
//                    "sieben;seven;0;0;0\n" +
//                    "acht;eight;0;0;0\n" +
//                    "neun;nine;0;0;0\n"
//            outputFile.write(output.toByteArray())
//            outputFile.close()
            vocableList.clear()
            context.openFileInput(vocablesFileName).bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    println("--- $line read ---")
                    val parts = line.split(";")
                    for (vocable in vocableList) {
                        if (vocable.getGerman() == parts[0] && vocable.getEnglish() == parts[1]) {
                            continue
                        }
                    }
                    val vocable = VocablesData(parts[0], parts[1])
                    vocable.asked = parts[2].toInt()
                    vocable.correct = parts[3].toInt()
                    vocable.wrong = parts[4].toInt()
                    vocableList.add(vocable)
                }
            }
        } catch (e: Exception) {
            println("--- Error read FILE: " + e.message + " ---")
        }
    }

    fun addVocable(context: Context, vocable: VocablesData) {
        vocableList.add(vocable)
        writeFile(context)
    }

    fun removeVocable(context: Context, vocable: VocablesData) {
        vocableList.remove(vocable)
        writeFile(context)
    }

    private fun getWorstVocable(): VocablesData {
        val worstList = ArrayList<VocablesData>()
        worstList.add(vocableList[0])
        for (vocable in vocableList) {
            if (vocable in vocableTest) {
                continue
            }
            if (vocable.getScore() < worstList[0].getScore()) {
                worstList.clear()
                worstList.add(vocable)
            } else if (vocable.getScore() == worstList[0].getScore()) {
                worstList.add(vocable)
            }
        }
        val random = (0 until worstList.size).random()
        return worstList[random]
    }
    private fun getRandomVocable(): VocablesData {
        for (i in 1..10) {
            val random = (0 until vocableList.size).random()
            val vocable = vocableList[random]
            if (vocable !in vocableTest) {
                return vocable
            }
        }
        return vocableList[0]
    }

    fun getWorstEnglishVocable(): String {
        return getWorstVocable().getEnglish()
    }

    fun getWorstGermanVocable(): String {
        return getWorstVocable().getGerman()
    }

    private fun findVocable(text: String): VocablesData? {
        for (vocable in vocableList) {
            if (vocable.getGerman() == text || vocable.getEnglish() == text) {
                return vocable
            }
        }
        return null
    }

    fun getVocableList(): ArrayList<VocablesData> {
        return vocableList
    }

    fun getVocableCount(): Int {
        return vocableList.size
    }

    fun startTest(count: Int) {
        if (count != 0) {
            if(count > vocableList.size) {
                testSize = vocableList.size
            }
            else {
                testSize = count
            }
        }
        val easy = (testSize / 3)
        for (i in 0 until testSize-easy) {
            vocableTest.add(getWorstVocable())
        }
        for (i in 1..easy) {
            vocableTest.add(getRandomVocable())
        }
        test = VocableTestData(vocableTest)
    }

    fun getTest(): VocableTestData {
        return test
    }

    fun writeFile(context: Context) {
        var text = ""
        for (vocable in vocableList) {
            text += vocable.toString() + "\n"
        }
        try {
            val outputFile = context.openFileOutput(vocablesFileName, MODE_PRIVATE)
            outputFile.write(text.toByteArray())
            outputFile.close()
        } catch (e: Exception) {
            println("--- Error write FILE: " + e.message + " ---")
        }
    }

}