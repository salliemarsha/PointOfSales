package com.sallie.pointofsales.transaksi

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView
import com.google.firebase.database.*
import com.sallie.pointofsales.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PembayaranActivity : AppCompatActivity() {

    private lateinit var tvTotalTagihan: TextView
    private lateinit var tvUangKembalian: TextView
    private lateinit var etUangBayar: TextInputEditText
    private lateinit var btnKonfirmasiPembayaran: Button
    private lateinit var toolbarPembayaran: MaterialToolbar

    private lateinit var btnUangPas: Button
    private lateinit var btnNominal10k: Button
    private lateinit var btnNominal20k: Button
    private lateinit var btnNominal50k: Button
    private lateinit var btnNominal100k: Button
    private lateinit var btnResetNominal: Button

    private var totalTagihan = 0
    private var uangBayar = 0
    private var uangKembalian = 0
    private var idTransaksi = ""

    private val database = FirebaseDatabase.getInstance()
    private val balanceRef = database.getReference("store_balance")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pembayaran)

        initComponent()

        idTransaksi = intent.getStringExtra("ID_TRANSAKSI") ?: ""
        totalTagihan = intent.getIntExtra("TOTAL_BELANJA", 0)

        tvTotalTagihan.text = "Rp$totalTagihan"

        toolbarPembayaran.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        etUangBayar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                hitungKembalian(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnUangPas.setOnClickListener { etUangBayar.setText(totalTagihan.toString()) }
        btnNominal10k.setOnClickListener { etUangBayar.setText("10000") }
        btnNominal20k.setOnClickListener { etUangBayar.setText("20000") }
        btnNominal50k.setOnClickListener { etUangBayar.setText("50000") }
        btnNominal100k.setOnClickListener { etUangBayar.setText("100000") }
        btnResetNominal.setOnClickListener { etUangBayar.setText("") }

        btnKonfirmasiPembayaran.setOnClickListener {
            cekSaldoDanProses()
        }
    }

    private fun hitungKembalian(inputUang: String) {
        if (inputUang.isNotEmpty()) {
            uangBayar = try { inputUang.toInt() } catch (e: Exception) { 0 }
            uangKembalian = uangBayar - totalTagihan
            tvUangKembalian.text = if (uangKembalian >= 0) {
                "Rp$uangKembalian"
            } else {
                "Uang Kurang"
            }
        } else {
            uangBayar = 0
            uangKembalian = 0
            tvUangKembalian.text = "Rp0"
        }
    }

    private fun cekSaldoDanProses() {
        if (idTransaksi.isEmpty()) {
            Toast.makeText(this, "ID transaksi tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        if (uangBayar < totalTagihan) {
            Toast.makeText(this, "Nominal uang yang dibayarkan kurang!", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate balance before proceeding
        balanceRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentBalance = currentData.getValue(Long::class.java) ?: 0L
                if (currentBalance < totalTagihan) {
                    return Transaction.abort()
                }
                // We don't deduct here yet, just check. 
                // Or we can deduct here and if transaction save fails, we'd have to rollback.
                // Requirement says: "Subtract it from the current balance ... ONLY AFTER transaction is successfully saved."
                return Transaction.success(currentData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                if (committed) {
                    prosesSimpanTransaksi()
                } else {
                    Toast.makeText(this@PembayaranActivity, "Insufficient store balance.", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun prosesSimpanTransaksi() {
        val ref = database.getReference("transaksi")

        val updates = mapOf(
            "uangBayar" to uangBayar,
            "kembalian" to uangKembalian
        )

        ref.child(idTransaksi).updateChildren(updates)
            .addOnSuccessListener {
                deductBalance()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan transaksi", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deductBalance() {
        balanceRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentBalance = currentData.getValue(Long::class.java) ?: 0L
                currentData.value = currentBalance - totalTagihan
                return Transaction.success(currentData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                if (committed) {
                    Toast.makeText(this@PembayaranActivity, "Transaksi Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@PembayaranActivity, StrukNotaActivity::class.java)
                    intent.putExtra("ID_TRANSAKSI", idTransaksi)
                    startActivity(intent)
                    finish()
                } else {
                    // This is unlikely if check succeeded, but handle it
                    Toast.makeText(this@PembayaranActivity, "Gagal memperbarui saldo", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun initComponent() {
        toolbarPembayaran = findViewById(R.id.toolbarPembayaran)
        tvTotalTagihan = findViewById(R.id.tvTotalTagihan)
        tvUangKembalian = findViewById(R.id.tvUangKembalian)
        etUangBayar = findViewById(R.id.etUangBayar)
        btnKonfirmasiPembayaran = findViewById(R.id.btnKonfirmasiPembayaran)

        btnUangPas = findViewById(R.id.btnUangPas)
        btnNominal10k = findViewById(R.id.btnNominal10k)
        btnNominal20k = findViewById(R.id.btnNominal20k)
        btnNominal50k = findViewById(R.id.btnNominal50k)
        btnNominal100k = findViewById(R.id.btnNominal100k)
        btnResetNominal = findViewById(R.id.btnResetNominal)
    }
}