package com.sallie.pointofsales.laporan

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.FirebaseDatabase
import com.sallie.pointofsales.R
import com.sallie.pointofsales.adapter.SimpleLaporanAdapter
import java.text.SimpleDateFormat
import java.util.*

class LaporanHarianActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvLaporanHarian: RecyclerView
    private lateinit var tvTotalPendapatanHarian: com.google.android.material.textview.MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_laporan_harian)

        initView()
        setupRecycler()
        setupToolbar()
        loadLaporanHarian()
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbarLaporanHarian)
        rvLaporanHarian = findViewById(R.id.rvLaporanHarian)
        tvTotalPendapatanHarian = findViewById(R.id.tvTotalPendapatanHarian)
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecycler() {
        rvLaporanHarian.layoutManager = LinearLayoutManager(this)
    }

    private fun loadLaporanHarian() {

        val ref = FirebaseDatabase.getInstance().getReference("transaksi")

        ref.get().addOnSuccessListener { snapshot ->

            val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                .format(Date())

            var totalPendapatan = 0

            val listTransaksi = mutableListOf<Map<String, Any>>()

            for (data in snapshot.children) {

                val tanggal = data.child("tanggal").value.toString()
                val totalHarga = data.child("totalHarga").value.toString().toIntOrNull() ?: 0

                if (tanggal.contains(today)) {
                    totalPendapatan += totalHarga

                    val item = mapOf(
                        "id" to (data.child("idTransaksi").value ?: ""),
                        "tanggal" to tanggal,
                        "total" to totalHarga
                    )

                    listTransaksi.add(item)
                }
            }

            tvTotalPendapatanHarian.text = "Rp$totalPendapatan"

            rvLaporanHarian.adapter = SimpleLaporanAdapter(listTransaksi)

        }.addOnFailureListener {
            Toast.makeText(this, "Gagal load laporan harian", Toast.LENGTH_SHORT).show()
        }
    }
}
