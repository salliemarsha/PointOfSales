package com.sallie.pointofsales.kategori

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_kategori)

        init()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDataKategori.layoutManager = layoutManager
        rvDataKategori.setHasFixedSize(true)

        viewModel.kategoriList.observe(this) { list ->
            adapter = DetailKategoriAdapter(list)
            rvDataKategori.adapter = adapter

            if (list.isNotEmpty()) {
                rvDataKategori.scrollToPosition(list.size - 1)
            }

            adapter.setOnItemClickListener(object : DetailKategoriAdapter.OnItemClickListener {
                override fun onItemClick(kategori: ModelKategoriActivity) {
                    if (!kategori.idKategori.isNullOrBlank()) {
                        showKategoriDetailFragment(kategori)
                    } else {
                        Toast.makeText(
                            this@DataKategoriActivity,
                            "Galat: {getString(R.string.data_kategori_tidak_valid)}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.datakategori)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fabAddCategory.setOnClickListener {
        }
    }

    private fun showKategoriDetailFragment(kategori: ModelKategoriActivity) {
        Toast.makeText(this, "Klik: ${kategori.namaKategori}", Toast.LENGTH_SHORT).show()
    }

    fun init() {
        rvDataKategori = findViewById(R.id.rvDataKategori)
        fabAddCategory = findViewById(R.id.fabAddCategory)
    }
}