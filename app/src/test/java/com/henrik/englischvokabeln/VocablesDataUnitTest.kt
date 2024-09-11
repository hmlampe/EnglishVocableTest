package com.henrik.englischvokabeln

import org.junit.Test
import org.junit.Assert.*
import com.henrik.englischvokabeln.VocablesData
import org.junit.After

class VocablesDataUnitTest {

    @After
    fun tearDown() {
        VocableListData.clearAll()
    }

    @Test
    fun testVocablesData() {
        val vocable = VocablesData("Hallo", "Hello")
        assertEquals("Hallo", vocable.getGerman())
        assertEquals("Hello", vocable.getEnglish())
    }

    @Test
    fun testVocableListData() {
        VocableListData.addVocable("Hallo", "Hello")
        VocableListData.addVocable("Welt", "World")
        assertEquals(2, VocableListData.getVocableCount())
    }

    @Test
    fun testVocableListDataTested() {
        VocableListData.addVocable("Hallo", "Hello")
        VocableListData.addVocable("Welt", "World")
        VocableListData.vocableTested("Hallo", false)
        VocableListData.testFinished()
        assertEquals(2, VocableListData.getVocableCount())
        assertEquals("Hallo", VocableListData.getWorstGermanVocable())
    }
}