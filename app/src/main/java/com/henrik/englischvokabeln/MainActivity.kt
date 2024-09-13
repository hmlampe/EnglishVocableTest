package com.henrik.englischvokabeln

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var addVocableView: FloatingActionButton
    private lateinit var testVocableView: FloatingActionButton
    private lateinit var editVocableView: FloatingActionButton
    private lateinit var vocableListViewGer: ListView
    private lateinit var vocableListViewEng: ListView
    private lateinit var vocableListGer: ArrayList<String>
    private lateinit var vocableListEng: ArrayList<String>
    private lateinit var itemAdapterGer: ArrayAdapter<String>
    private lateinit var itemAdapterEng: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        addVocableView = findViewById(R.id.addVocablesView)
        testVocableView = findViewById(R.id.testVocablesView)
        editVocableView = findViewById(R.id.editVocablesView)
        vocableListViewGer = findViewById(R.id.viewVocableListGer)
        vocableListViewEng = findViewById(R.id.viewVocableListEng)

        vocableListGer = ArrayList()
        vocableListEng = ArrayList()

        itemAdapterGer = ArrayAdapter(this, android.R.layout.simple_list_item_1, vocableListGer)
        itemAdapterEng = ArrayAdapter(this, android.R.layout.simple_list_item_1, vocableListEng)

        vocableListViewGer.adapter = itemAdapterGer
        vocableListViewEng.adapter = itemAdapterEng

        VocableListData.initializeVocables(this)
        vocableListGer.clear()
        vocableListEng.clear()
        for (vocable in VocableListData.getVocableList()) {
            vocableListGer.add(vocable.getGerman())
            vocableListEng.add(vocable.getEnglish())
        }

        addVocableView.setOnClickListener {
            showAddVocableDialog()
        }

        testVocableView.setOnClickListener {
            showStartTestDialog()
        }

        editVocableView.setOnClickListener {
            showEditVocablesActivity()
        }

    }

    private fun showAddVocableDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Neue Vokabel")
        val view = layoutInflater.inflate(R.layout.add_vocable_layout, null)
        builder.setView(view)
        val inputGer = view.findViewById<EditText>(R.id.resultPositivView)
        val inputEng = view.findViewById<EditText>(R.id.resultNegativeView)
        inputGer.requestFocus()
        builder.setPositiveButton("OK") { dialog, _ ->
            addVocable(inputGer.text.toString(), inputEng.text.toString())
            dialog.dismiss()
        }
        builder.setNegativeButton("Abbrechen") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showStartTestDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Vokabeltest")
        val view = layoutInflater.inflate(R.layout.start_test_layout, null)
        builder.setView(view)
        val testSize = view.findViewById<EditText>(R.id.resultPositivView)
        testSize.requestFocus()
        builder.setPositiveButton("Test starten") { dialog, _ ->
            var size = 10
            if (testSize.text.toString() != "") {
                size = testSize.text.toString().toInt()
            }
            VocableListData.startTest(size)
            val intent = Intent(this, TestVocables::class.java)
            startActivity(intent)
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showEditVocablesActivity() {
        val intent = Intent(this, EditVocables::class.java)
        startActivity(intent)
    }

    private fun addVocable(vocableGer: String, vocableEng: String) {
        vocableListGer.add(vocableGer)
        vocableListEng.add(vocableEng)
        itemAdapterGer.notifyDataSetChanged()
        itemAdapterEng.notifyDataSetChanged()
        val vocable = VocablesData(vocableGer, vocableEng)
        VocableListData.addVocable(this, vocable)
    }
}