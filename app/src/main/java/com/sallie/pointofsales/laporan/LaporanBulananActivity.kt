package com.sallie.pointofsales.laporan

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.database.FirebaseDatabase
import com.sallie.pointofsales.R
import java.text.SimpleDateFormat
import java.util.*

class LaporanBulananActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var spFilterBulan: MaterialAutoCompleteTextView
    private lateinit var tvTotalPendapatanBulanan: com.google.android.material.textview.MaterialTextView

    private val listBulan = arrayListOf(
        "01-2026", "02-2026", "03-2026", "04-2026", "05-2026", "06-2026",
        "07-2026", "08-2026", "09-2026", "10-2026", "11-2026", "12-2026"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_laporan_bulanan)

        initView()
        setupToolbar()
        setupDropdown()
        loadBulanan(getCurrentMonth())
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbarLaporanBulanan)
        spFilterBulan = findViewById(R.id.spFilterBulan)
        tvTotalPendapatanBulanan = findViewById(R.id.tvTotalPendapatanBulanan)
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listBulan)
        spFilterBulan.setAdapter(adapter)

        spFilterBulan.setText(getCurrentMonth(), false)

        spFilterBulan.setOnItemClickListener { _, _, position, _ ->
            val selected = listBulan[position]
            loadBulanan(selected)
        }
    }

    private fun loadBulanan(monthYear: String) {
        val ref = FirebaseDatabase.getInstance().getReference("transaksi")

        ref.get().addOnSuccessListener { snapshot ->

            var total = 0

            for (data in snapshot.children) {

                val tanggal = data.child("tanggal").value.toString()
                val totalHarga = data.child("totalHarga").value.toString().toIntOrNull() ?: 0

                if (tanggal.contains(monthYear)) {
                    total += totalHarga
                }
            }

            tvTotalPendapatanBulanan.text = "Rp$total"

        }.addOnFailureListener {
            Toast.makeText(this, "Gagal memuat laporan bulanan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentMonth(): String {
        val sdf = SimpleDateFormat("MM-yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}