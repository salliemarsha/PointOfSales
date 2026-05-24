package com.sallie.pointofsales.kategori

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.sallie.pointofsales.R
import com.sallie.pointofsales.adapter.DetailKategoriAdapter
import com.sallie.pointofsales.model.ModelKategoriActivity
import com.sallie.pointofsales.viewmodel.DataKategoriViewModel

class DataKategoriActivity : AppCompatActivity() {

    private val viewModel: DataKategoriViewModel by viewModels()
    private lateinit var rvDataKategori: RecyclerView
    private lateinit var fabAddCategory: FloatingActionButton
    private lateinit var etSearchCategory: TextInputEditText
    private lateinit var adapter: DetailKategoriAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_kategori)

        init()

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        rvDataKategori.layoutManager = layoutManager
        rvDataKategori.setHasFixedSize(true)

        adapter = DetailKategoriAdapter(emptyList())
        rvDataKategori.adapter = adapter

        adapter.setOnItemClickListener(object : DetailKategoriAdapter.OnItemClickListener {
            override fun onItemClick(kategori: ModelKategoriActivity) {
                if (!kategori.idKategori.isNullOrBlank()) {
                    val intent = Intent(this@DataKategoriActivity, ModKategoriActivity::class.java).apply {
                        putExtra("ID_KATEGORI", kategori.idKategori)
                        putExtra("NAMA_KATEGORI", kategori.namaKategori)
                        putExtra("STATUS_KATEGORI", kategori.statusKategori)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this@DataKategoriActivity, "Data tidak valid", Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.kategoriList.observe(this) { list ->
            adapter.updateData(list)
            if (list.isNotEmpty()) {
                rvDataKategori.scrollToPosition(list.size - 1)
            }
        }

        fabAddCategory.setOnClickListener {
            startActivity(Intent(this, ModKategoriActivity::class.java))
        }

        etSearchCategory.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun init() {
        rvDataKategori = findViewById(R.id.rvDataKategori)
        fabAddCategory = findViewById(R.id.fabAddCategory)
        etSearchCategory = findViewById(R.id.etSearchCategory)
    }
}