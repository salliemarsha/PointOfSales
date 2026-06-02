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
import com.sallie.pointofsales.adapter.LaporanKasirAdapter
import com.sallie.pointofsales.model.LaporanKasirModel

class LaporanKasirActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvTopCashierName: TextView
    private lateinit var tvTopCashierStats: TextView
    private lateinit var rvKasir: RecyclerView

    private lateinit var database: DatabaseReference
    private val kasirList = ArrayList<LaporanKasirModel>()
    private lateinit var adapter: LaporanKasirAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_laporan_kasir)

        initView()

        database = FirebaseDatabase.getInstance().getReference("kasir")

        setupRecycler()
        loadKasirData()

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbarLaporanKasir)
        tvTopCashierName = findViewById(R.id.tvTopCashierName)
        tvTopCashierStats = findViewById(R.id.tvTopCashierStats)
        rvKasir = findViewById(R.id.rvLaporanKasir)
    }

    private fun setupRecycler() {
        adapter = LaporanKasirAdapter(kasirList)
        rvKasir.layoutManager = LinearLayoutManager(this)
        rvKasir.adapter = adapter
    }

    private fun loadKasirData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                kasirList.clear()

                var topName = "-"
                var topTotal = 0L
                var topTransaksi = 0

                for (data in snapshot.children) {
                    val nama = data.child("nama").value.toString()
                    val total = data.child("totalPenjualan").value.toString().toLongOrNull() ?: 0L
                    val transaksi = data.child("jumlahTransaksi").value.toString().toIntOrNull() ?: 0

                    kasirList.add(
                        LaporanKasirModel(
                            namaKasir = nama,
                            totalPenjualan = total,
                            jumlahTransaksi = transaksi
                        )
                    )

                    if (total > topTotal) {
                        topTotal = total
                        topName = nama
                        topTransaksi = transaksi
                    }
                }

                tvTopCashierName.text = topName
                tvTopCashierStats.text = "Rp$topTotal ($topTransaksi Transaksi)"

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
