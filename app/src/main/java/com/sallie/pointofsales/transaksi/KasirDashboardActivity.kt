package com.sallie.pointofsales.transaksi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.*
import com.sallie.pointofsales.R
import com.sallie.pointofsales.akun.AccountActivity
import com.sallie.pointofsales.laporan.TransactionHistoryActivity
import java.text.SimpleDateFormat
import java.util.*

class KasirDashboardActivity : AppCompatActivity() {

    private lateinit var tvGreetings: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTotalNota: TextView
    private lateinit var cvTransaksiBaru: MaterialCardView
    private lateinit var cvRiwayatSaya: MaterialCardView
    private lateinit var cvAkunSaya: MaterialCardView

    private val database = FirebaseDatabase.getInstance()
    private val transaksiRef = database.getReference("transaksi")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kasir_dashboard)

        initViews()
        setupDashboard()
        loadTodayStats()
    }

    private fun initViews() {
        tvGreetings = findViewById(R.id.tvGreetings)
        tvDate = findViewById(R.id.tvDate)
        tvTotalNota = findViewById(R.id.tvTotalNota)
        cvTransaksiBaru = findViewById(R.id.cvTransaksiBaru)
        cvRiwayatSaya = findViewById(R.id.cvRiwayatSaya)
        cvAkunSaya = findViewById(R.id.cvAkunSaya)

        cvTransaksiBaru.setOnClickListener {
            startActivity(Intent(this, TransaksiActivity::class.java))
        }

        cvRiwayatSaya.setOnClickListener {
            startActivity(Intent(this, TransactionHistoryActivity::class.java))
        }

        cvAkunSaya.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }

    private fun setupDashboard() {
        val sharedPref = getSharedPreferences("KASIR_SESSION", Context.MODE_PRIVATE)
        val namaKasir = sharedPref.getString("KASIR_NAMA", "Kasir")
        
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 0..11 -> "Selamat Pagi"
            in 12..14 -> "Selamat Siang"
            in 15..17 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
        
        tvGreetings.text = "$greeting, $namaKasir"
        
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        tvDate.text = dateFormat.format(Date())
    }

    private fun loadTodayStats() {
        val sharedPref = getSharedPreferences("KASIR_SESSION", Context.MODE_PRIVATE)
        val idKasir = sharedPref.getString("KASIR_ID", "") ?: ""
        val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        // Logic: Count only transactions for this specific cashier on this day
        transaksiRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                for (ds in snapshot.children) {
                    val tgl = ds.child("tanggal").getValue(String::class.java) ?: ""
                    val kasirIdInDb = ds.child("idKasir").getValue(String::class.java) ?: ""
                    
                    if (tgl.contains(today) && kasirIdInDb == idKasir) {
                        count++
                    }
                }
                tvTotalNota.text = "$count Nota"
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("KasirDashboard", "Error: ${error.message}")
            }
        })
    }
}
