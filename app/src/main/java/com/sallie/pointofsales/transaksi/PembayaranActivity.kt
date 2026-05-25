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
import com.google.firebase.database.FirebaseDatabase
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pembayaran)

        initComponent()

        toolbarPembayaran.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        totalTagihan = intent.getIntExtra("TOTAL_BELANJA", 0)
        tvTotalTagihan.text = "Rp$totalTagihan"

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
            prosesSimpanTransaksi()
        }
    }

    private fun hitungKembalian(inputUang: String) {
        if (inputUang.isNotEmpty()) {
            uangBayar = inputUang.toInt()
            uangKembalian = uangBayar - totalTagihan
            if (uangKembalian >= 0) {
                tvUangKembalian.text = "Rp$uangKembalian"
            } else {
                tvUangKembalian.text = "Uang Kurang"
            }
        } else {
            uangBayar = 0
            uangKembalian = 0
            tvUangKembalian.text = "Rp0"
        }
    }

    private fun prosesSimpanTransaksi() {
        if (uangBayar < totalTagihan) {
            Toast.makeText(this, "Nominal uang yang dibayarkan kurang!", Toast.LENGTH_SHORT).show()
            return
        }

        val databaseRef = FirebaseDatabase.getInstance().getReference("transaksi")
        val idTransaksi = databaseRef.push().key ?: return

        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val tanggalSekarang = sdf.format(Date())

        val dataTransaksi = HashMap<String, Any>()
        dataTransaksi["idTransaksi"] = idTransaksi
        dataTransaksi["tanggal"] = tanggalSekarang
        dataTransaksi["totalHarga"] = totalTagihan
        dataTransaksi["uangBayar"] = uangBayar
        dataTransaksi["kembalian"] = uangKembalian

        databaseRef.child(idTransaksi).setValue(dataTransaksi)
            .addOnSuccessListener {
                Toast.makeText(this, "Transaksi Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, StrukNotaActivity::class.java).apply {
                    putExtra("ID_TRANSAKSI", idTransaksi)
                }
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan transaksi", Toast.LENGTH_SHORT).show()
            }
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