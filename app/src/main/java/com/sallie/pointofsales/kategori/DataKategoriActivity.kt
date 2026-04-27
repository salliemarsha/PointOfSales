package com.sallie.pointofsales.kategori

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sallie.pointofsales.R
import com.sallie.pointofsales.adapter.DetailKategoriAdapter
import com.sallie.pointofsales.model.ModelKategoriActivity
import com.sallie.pointofsales.viewmodel.DataKategoriViewModel

class DataKategoriActivity : AppCompatActivity() {

    private val viewModel: DataKategoriViewModel by viewModels()
    private lateinit var rvDataKategori: RecyclerView
    private lateinit var fabAddCategory: FloatingActionButton
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
            override fun onItemClick(kategori: ModelKategoriActivity) {}
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
    }

    private fun init() {
        rvDataKategori = findViewById(R.id.rvDataKategori)
        fabAddCategory = findViewById(R.id.fabAddCategory)
    }
}