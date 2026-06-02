package com.sallie.pointofsales.laporan

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.*
import com.sallie.pointofsales.R
import com.sallie.pointofsales.adapter.ProdukTerlarisAdapter
import com.sallie.pointofsales.model.ProdukTerlarisModel

class ProdukTerlarisActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvTopProductName: TextView
    private lateinit var tvTopProductQty: TextView
    private lateinit var rvProduk: RecyclerView

    private lateinit var database: DatabaseReference

    private val produkList = ArrayList<ProdukTerlarisModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_produk_terlaris)

        initView()

        database = FirebaseDatabase.getInstance().getReference("detail_transaksi")

        setupRecycler()
        loadProdukTerlaris()

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbarProdukTerlaris)
        tvTopProductName = findViewById(R.id.tvTopProductName)
        tvTopProductQty = findViewById(R.id.tvTopProductQty)
        rvProduk = findViewById(R.id.rvProdukTerlaris)
    }

    private fun setupRecycler() {
        rvProduk.layoutManager = LinearLayoutManager(this)
        rvProduk.adapter = ProdukTerlarisAdapter(produkList)
    }

    private fun loadProdukTerlaris() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val tempMap = HashMap<String, Int>()

                for (data in snapshot.children) {

                    val namaProduk = data.child("namaProduk").value.toString()
                    val qty = data.child("jumlah").value.toString().toIntOrNull() ?: 0

                    tempMap[namaProduk] = (tempMap[namaProduk] ?: 0) + qty
                }

                produkList.clear()

                var topName = "-"
                var topQty = 0

                for ((name, qty) in tempMap) {

                    produkList.add(
                        ProdukTerlarisModel(
                            namaProduk = name,
                            totalTerjual = qty
                        )
                    )

                    if (qty > topQty) {
                        topQty = qty
                        topName = name
                    }
                }

                tvTopProductName.text = topName
                tvTopProductQty.text = "Terjual $topQty Unit"

                produkList.sortByDescending { it.totalTerjual }

                rvProduk.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
