package com.henrik.englischvokabeln

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EditVocables : AppCompatActivity() {

    private lateinit var stopEdit: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_vocables)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView: RecyclerView = findViewById(R.id.editRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MyAdapter(this, VocableListData.getVocableList())
        recyclerView.adapter = adapter

        stopEdit = findViewById(R.id.stopEditView)

        stopEdit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}

class MyAdapter(private val context: Context, private val data: List<VocablesData>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView1: TextView = itemView.findViewById(R.id.textView1)
        val textView2: TextView = itemView.findViewById(R.id.textView2)
        val button1: AppCompatImageButton = itemView.findViewById(R.id.imageButton1)
        val button2: AppCompatImageButton = itemView.findViewById(R.id.imageButton2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.edit_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.textView1.text = item.getGerman()
        holder.textView2.text = item.getEnglish()

        holder.button1.setOnClickListener {
            showEditVocableDialog(context, item, position)
            holder.textView1.text = item.getGerman()
            holder.textView2.text = item.getEnglish()
        }
        holder.button2.setOnClickListener {
            VocableListData.removeVocable(context, item)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun showEditVocableDialog(context: Context, vocable: VocablesData, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Vokabel ändern")
        val view = LayoutInflater.from(context).inflate(R.layout.edit_vocable_layout, null)
        builder.setView(view)
        val textGerman = view.findViewById<TextView>(R.id.textViewGerman)
        val textEnglish = view.findViewById<TextView>(R.id.textViewEnglish)
        textGerman.requestFocus()
        textGerman.text = vocable.getGerman()
        textEnglish.text = vocable.getEnglish()
        builder.setPositiveButton("Speichern") { dialog, _ ->
            vocable.setGerman(textGerman.text.toString())
            vocable.setEnglish(textEnglish.text.toString())
            VocableListData.writeFile(context)
            notifyItemChanged(position)
            dialog.dismiss()
        }
        builder.setNegativeButton("Abbrechen") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}