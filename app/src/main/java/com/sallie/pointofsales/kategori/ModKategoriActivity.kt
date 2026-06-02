package com.sallie.pointofsales.kategori

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ModelKategoriActivity

class ModKategoriActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("kategori")

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvToolbarTitle: TextView
    private lateinit var etNameKategori: TextInputEditText
    private lateinit var statuskategori: TextInputLayout
    private lateinit var spstatus: AutoCompleteTextView
    private lateinit var btAdd: Button

    private var idKategori: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mod_kategori)

        init()

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val statusOptions = resources.getStringArray(R.array.status)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, statusOptions)
        spstatus.setAdapter(adapter)

        // Check for Edit Mode
        idKategori = intent.getStringExtra("ID_KATEGORI")
        val namaKategori = intent.getStringExtra("NAMA_KATEGORI")
        val statusKategori = intent.getStringExtra("STATUS_KATEGORI")

        if (idKategori != null) {
            tvToolbarTitle.text = "Edit Kategori"
            etNameKategori.setText(namaKategori)
            spstatus.setText(statusKategori, false)
            btAdd.text = getString(R.string.simpan)
        } else {
            spstatus.setText(statusOptions[0], false)
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
        etNameKategori = findViewById(R.id.etNameKategori)
        statuskategori = findViewById(R.id.statusKategori)
        spstatus = findViewById(R.id.spstatus)
        btAdd = findViewById(R.id.btAdd)

        btAdd.setOnClickListener {
            cekValidasi()
        }
    }

    private fun cekValidasi() {
        val nama = etNameKategori.text.toString().trim()
        val status = spstatus.text.toString().trim()

        if (nama.isEmpty()) {
            etNameKategori.error = "Nama Kategori Tidak Boleh Kosong"
            etNameKategori.requestFocus()
            return
        }

        if (status.isEmpty()) {
            spstatus.error = "Status Tidak Boleh Kosong"
            spstatus.requestFocus()
            return
        }

        simpan()
    }

    private fun simpan() {
        val id = idKategori ?: myRef.push().key

        if (id == null) {
            Toast.makeText(this, "ID gagal dibuat", Toast.LENGTH_SHORT).show()
            return
        }

        val kategori = ModelKategoriActivity(
            idKategori = id,
            namaKategori = etNameKategori.text.toString().trim(),
            statusKategori = spstatus.text.toString().trim()
        )

        myRef.child(id).setValue(kategori)
            .addOnSuccessListener {
                val message = if (idKategori != null) "Kategori berhasil diperbarui" else "Kategori berhasil ditambahkan"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan kategori", Toast.LENGTH_SHORT).show()
            }
    }
}
