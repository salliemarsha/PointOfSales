package com.sallie.pointofsales

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

/**
 * MainActivity serves as the primary dashboard for the POS system.
 * It manages session validation and provides navigation to all core modules.
 */
class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database = FirebaseDatabase.getInstance()
    private val balanceRef = database.getReference("store_balance")

    private lateinit var cvAccount: CardView
    private lateinit var cvProduct: CardView
    private lateinit var cvCategory: CardView
    private lateinit var cvEmployee: CardView
    private lateinit var cvBranch: CardView
    private lateinit var cvPrint: CardView

    private lateinit var llTransaksi: LinearLayout
    private lateinit var llLaporan: LinearLayout
    private lateinit var llPelanggan: LinearLayout

    private lateinit var tvDate: TextView
    private lateinit var tvGreetings: TextView
    private lateinit var tvRP: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Session Guard: Redirect to Login if no active session
        if (auth.currentUser == null) {
            redirectToLogin()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 2. Component Initialization
        initViews()
        setupListeners()
        updateDashboardInfo()
        listenToBalance()

        // 3. System UI Setup
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        cvAccount = findViewById(R.id.cvAccount)
        cvProduct = findViewById(R.id.cvProduct)
        cvCategory = findViewById(R.id.cvCategory)
        cvEmployee = findViewById(R.id.cvEmployee)
        cvBranch = findViewById(R.id.cvBranch)
        cvPrint = findViewById(R.id.cvPrint)

        llTransaksi = findViewById(R.id.llTransaksi)
        llLaporan = findViewById(R.id.llLaporan)
        llPelanggan = findViewById(R.id.llPelanggan)

        tvDate = findViewById(R.id.tvDate)
        tvGreetings = findViewById(R.id.tvGreetings)
        tvRP = findViewById(R.id.tvRP)
    }

    private fun setupListeners() {
        cvCategory.setOnClickListener { navigateTo(DataKategoriActivity::class.java) }
        cvProduct.setOnClickListener { navigateTo(DataProductActivity::class.java) }
        cvEmployee.setOnClickListener { navigateTo(DataPegawaiActivity::class.java) }
        cvBranch.setOnClickListener { navigateTo(DataCabangActivity::class.java) }
        llTransaksi.setOnClickListener { navigateTo(TransaksiActivity::class.java) }
        
        // Refactored: Report button now opens Transaction History
        llLaporan.setOnClickListener { navigateTo(TransactionHistoryActivity::class.java) }

        llPelanggan.setOnClickListener { navigateTo(PelangganActivity::class.java) }
        cvAccount.setOnClickListener { navigateTo(AccountActivity::class.java) }
        cvPrint.setOnClickListener { navigateTo(PrinterSettingsActivity::class.java) }
    }

    private fun updateDashboardInfo() {
        val localeID = Locale("id", "ID")
        tvDate.text = SimpleDateFormat("dd MMMM yyyy", localeID).format(Date())
        tvGreetings.text = getGreeting()
    }

    private fun listenToBalance() {
        balanceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val balance = snapshot.getValue(Long::class.java) ?: 1000000L // Default 1jt if empty
                if (!snapshot.exists()) {
                    balanceRef.setValue(balance)
                }
                val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                tvRP.text = format.format(balance).replace(",00", "").replace("Rp", "Rp ")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Balance listener cancelled: ${error.message}")
            }
        })
    }

    private fun getGreeting(): String {
        val user = auth.currentUser
        // Logic: Use Display Name -> Email Prefix -> Generic "User"
        val userName = user?.displayName ?: user?.email?.substringBefore("@") ?: "User"
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        
        val timeLabel = when (hour) {
            in 0..11 -> "Pagi"
            in 12..14 -> "Siang"
            in 15..17 -> "Sore"
            else -> "Malam"
        }
        return "Selamat $timeLabel, $userName"
    }

    /**
     * Standardized navigation helper with error logging for production stability.
     */
    private fun <T> navigateTo(destination: Class<T>) {
        try {
            startActivity(Intent(this, destination))
        } catch (e: Exception) {
            Log.e("MainActivity", "Navigation error to ${destination.simpleName}: ${e.message}")
            Toast.makeText(this, "Modul tidak tersedia", Toast.LENGTH_SHORT).show()
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
