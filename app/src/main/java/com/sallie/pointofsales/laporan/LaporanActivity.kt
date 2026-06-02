package com.sallie.pointofsales.laporan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.FirebaseDatabase
import com.sallie.pointofsales.R

class LaporanActivity : AppCompatActivity() {

    private lateinit var toolbarLaporan: MaterialToolbar

    private lateinit var cvLaporanHarian: MaterialCardView
    private lateinit var cvLaporanBulanan: MaterialCardView
    private lateinit var cvProdukTerlaris: MaterialCardView
    private lateinit var cvLaporanKasir: MaterialCardView

    private lateinit var tvTodaySalesVal: MaterialTextView
    private lateinit var tvMonthlySalesVal: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_laporan)

        initView()
        setupToolbar()
        setupClick()
        loadRingkasan()
    }

    private fun initView() {
        toolbarLaporan = findViewById(R.id.toolbarLaporan)

        cvLaporanHarian = findViewById(R.id.cvLaporanHarian)
        cvLaporanBulanan = findViewById(R.id.cvLaporanBulanan)
        cvProdukTerlaris = findViewById(R.id.cvProdukTerlaris)
        cvLaporanKasir = findViewById(R.id.cvLaporanKasir)

        tvTodaySalesVal = findViewById(R.id.tvTodaySalesVal)
        tvMonthlySalesVal = findViewById(R.id.tvMonthlySalesVal)
    }

    private fun setupToolbar() {
        toolbarLaporan.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupClick() {
        cvLaporanHarian.setOnClickListener {
            startActivity(Intent(this, LaporanHarianActivity::class.java))
        }

        cvLaporanBulanan.setOnClickListener {
            startActivity(Intent(this, LaporanBulananActivity::class.java))
        }

        cvProdukTerlaris.setOnClickListener {
            startActivity(Intent(this, ProdukTerlarisActivity::class.java))
        }

        cvLaporanKasir.setOnClickListener {
            startActivity(Intent(this, LaporanKasirActivity::class.java))
        }
    }

    private fun loadRingkasan() {
        val ref = FirebaseDatabase.getInstance().getReference("transaksi")

        ref.get().addOnSuccessListener { snapshot ->
            var totalHariIni = 0
            var totalBulanIni = 0

            val today = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault())
                .format(java.util.Date())

            val month = java.text.SimpleDateFormat("MM-yyyy", java.util.Locale.getDefault())
                .format(java.util.Date())

            for (data in snapshot.children) {
                val tanggal = data.child("tanggal").value.toString()
                val total = data.child("totalHarga").value.toString().toIntOrNull() ?: 0

                if (tanggal.contains(today)) {
                    totalHariIni += total
                }

                if (tanggal.contains(month)) {
                    totalBulanIni += total
                }
            }

            tvTodaySalesVal.text = "Rp$totalHariIni"
            tvMonthlySalesVal.text = "Rp$totalBulanIni"

        }.addOnFailureListener {
            Toast.makeText(this, "Gagal load laporan", Toast.LENGTH_SHORT).show()
        }
    }
}
