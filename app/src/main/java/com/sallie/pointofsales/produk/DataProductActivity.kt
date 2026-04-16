package com.sallie.pointofsales.produk

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ModelProdukActivity
import com.sallie.pointofsales.viewmodel.DataProdukViewModel
import com.sallie.pointofsales.adapter.DetailProdukAdapter

class DataProductActivity : AppCompatActivity() {

    private val viewModel: DataProdukViewModel by viewModels()
    private lateinit var rvDataProduk: RecyclerView
    private lateinit var fabAddProduct: FloatingActionButton
    private lateinit var adapter: DetailProdukAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_product)

        init()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        rvDataProduk.layoutManager = layoutManager
        rvDataProduk.setHasFixedSize(true)

        adapter = DetailProdukAdapter(emptyList())
        rvDataProduk.adapter = adapter

        adapter.setOnItemClickListener(object : DetailProdukAdapter.OnItemClickListener {
            override fun onItemClick(produk: ModelProdukActivity) {
                if (!produk.idProduk.isNullOrBlank()) {
                    showProdukDetail(produk)
                } else {
                    Toast.makeText(
                        this@DataProductActivity,
                        "Data tidak valid",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        viewModel.produkList.observe(this) { list ->
            adapter.updateData(list)

            if (list.isNotEmpty()) {
                rvDataProduk.scrollToPosition(list.size - 1)
            }
        }
    }

    private fun showProdukDetail(produk: ModelProdukActivity) {
        Toast.makeText(this, "Klik: ${produk.namaProduk}", Toast.LENGTH_SHORT).show()
    }

    private fun init() {
        rvDataProduk = findViewById(R.id.rvDataProduk)
        fabAddProduct = findViewById(R.id.fabAddProduct)
    }

}