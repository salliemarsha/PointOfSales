package com.sallie.pointofsales.cabang

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ModelCabangActivity

class ModCabangActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("cabang")

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvToolbarTitle: TextView
    private lateinit var etNamaCabang: TextInputEditText
    private lateinit var etLokasiCabang: TextInputEditText
    private lateinit var btnSimpan: Button

    private var idCabang: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mod_cabang)

        init()

        idCabang = intent.getStringExtra("ID_CABANG")
        val namaCabang = intent.getStringExtra("NAMA_CABANG")
        val lokasiCabang = intent.getStringExtra("LOKASI_CABANG")

        if (idCabang != null) {
            tvToolbarTitle.text = "Edit Cabang"
            etNamaCabang.setText(namaCabang)
            etLokasiCabang.setText(lokasiCabang)
            btnSimpan.text = "Simpan Perubahan"
        }

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mod)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun init() {
        toolbar = findViewById(R.id.toolbar)
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle)
        etNamaCabang = findViewById(R.id.etNamaCabang)
        etLokasiCabang = findViewById(R.id.etLokasiCabang)
        btnSimpan = findViewById(R.id.btnSimpan)

        btnSimpan.setOnClickListener { cekValidasi() }
    }

    private fun cekValidasi() {
        val nama = etNamaCabang.text.toString().trim()
        val lokasi = etLokasiCabang.text.toString().trim()

        if (nama.isEmpty()) {
            etNamaCabang.error = "Nama cabang tidak boleh kosong"
            etNamaCabang.requestFocus()
            return
        }
        if (lokasi.isEmpty()) {
            etLokasiCabang.error = "Lokasi cabang tidak boleh kosong"
            etLokasiCabang.requestFocus()
            return
        }

        simpan(nama, lokasi)
    }

    private fun simpan(nama: String, lokasi: String) {
        val id = idCabang ?: myRef.push().key
        if (id == null) {
            Toast.makeText(this, "ID gagal dibuat", Toast.LENGTH_SHORT).show()
            return
        }

        val cabang = ModelCabangActivity(
            idCabang = id,
            namaCabang = nama,
            lokasiCabang = lokasi
        )

        myRef.child(id).setValue(cabang)
            .addOnSuccessListener {
                val pesan = if (idCabang != null) "Data cabang diubah" else "Cabang ditambahkan"
                Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
            }
    }
}