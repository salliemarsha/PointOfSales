package com.sallie.pointofsales

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sallie.pointofsales.akun.AccountActivity
import com.sallie.pointofsales.auth.LoginActivity
import com.sallie.pointofsales.cabang.DataCabangActivity
import com.sallie.pointofsales.kategori.DataKategoriActivity
import com.sallie.pointofsales.laporan.TransactionHistoryActivity
import com.sallie.pointofsales.pegawai.DataPegawaiActivity
import com.sallie.pointofsales.pelanggan.PelangganActivity
import com.sallie.pointofsales.printer.PrinterSettingsActivity
import com.sallie.pointofsales.produk.DataProductActivity
import com.sallie.pointofsales.transaksi.TransaksiActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database = FirebaseDatabase.getInstance()
    private val transaksiRef = database.getReference("transaksi")

    private lateinit var tvRP: TextView
    private lateinit var tvGreetings: TextView
    private lateinit var tvDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()
        updateDashboardInfo()
        listenToDailyIncome() // AKTIFKAN SUM REAL-TIME
    }

    private fun initViews() {
        tvRP = findViewById(R.id.tvRP)
        tvGreetings = findViewById(R.id.tvGreetings)
        tvDate = findViewById(R.id.tvDate)
    }

    private fun setupListeners() {
        findViewById<LinearLayout>(R.id.llTransaksi).setOnClickListener { navigateTo(TransaksiActivity::class.java) }
        findViewById<LinearLayout>(R.id.llLaporan).setOnClickListener { navigateTo(TransactionHistoryActivity::class.java) }
        findViewById<LinearLayout>(R.id.llPelanggan).setOnClickListener { navigateTo(PelangganActivity::class.java) }
        findViewById<CardView>(R.id.cvAccount).setOnClickListener { navigateTo(AccountActivity::class.java) }
        findViewById<CardView>(R.id.cvProduct).setOnClickListener { navigateTo(DataProductActivity::class.java) }
        findViewById<CardView>(R.id.cvCategory).setOnClickListener { navigateTo(DataKategoriActivity::class.java) }
        findViewById<CardView>(R.id.cvEmployee).setOnClickListener { navigateTo(DataPegawaiActivity::class.java) }
        findViewById<CardView>(R.id.cvBranch).setOnClickListener { navigateTo(DataCabangActivity::class.java) }
        findViewById<CardView>(R.id.cvPrint).setOnClickListener { navigateTo(PrinterSettingsActivity::class.java) }
    }

    private fun listenToDailyIncome() {
        val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        
        transaksiRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalAccumulated = 0L
                for (ds in snapshot.children) {
                    val tanggalTrx = ds.child("tanggal").getValue(String::class.java) ?: ""
                    if (tanggalTrx.contains(today)) {
                        val amount = ds.child("totalBayar").value?.toString()?.toLongOrNull() 
                                    ?: ds.child("totalHarga").value?.toString()?.toLongOrNull() ?: 0L
                        totalAccumulated += amount
                    }
                }
                tvRP.text = formatRupiah(totalAccumulated)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Database error: ${error.message}")
            }
        })
    }

    private fun formatRupiah(amount: Long): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount).replace(",00", "").replace("Rp", "Rp ")
    }

    private fun updateDashboardInfo() {
        val localeID = Locale("id", "ID")
        tvDate.text = SimpleDateFormat("dd MMMM yyyy", localeID).format(Date())
        
        val user = auth.currentUser
        val userName = user?.displayName ?: "Admin"
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val timeLabel = when (hour) {
            in 0..11 -> "Pagi"
            in 12..14 -> "Siang"
            in 15..17 -> "Sore"
            else -> "Malam"
        }
        tvGreetings.text = "Selamat $timeLabel, $userName"
    }

    private fun <T> navigateTo(destination: Class<T>) {
        startActivity(Intent(this, destination))
    }

    override fun onResume() {
        super.onResume()
        updateDashboardInfo() // Sync nama jika Admin baru edit profil
    }
}
