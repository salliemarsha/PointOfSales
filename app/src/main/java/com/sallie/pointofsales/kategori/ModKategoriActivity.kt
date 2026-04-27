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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mod_kategori)

        init()

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val statusOptions = resources.getStringArray(R.array.status)

        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_item,
            statusOptions
        )

        spstatus.setAdapter(adapter)
        spstatus.setText(statusOptions[0], false)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mod)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun init(){
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle)
        etNameKategori = findViewById(R.id.etNameKategori)
        statuskategori = findViewById(R.id.statusKategori)
        spstatus = findViewById(R.id.spstatus)
        btAdd = findViewById(R.id.btAdd)

        btAdd.setOnClickListener {
            cekValidasi()
        }
    }

    private fun cekValidasi(){

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

        if (btAdd.text.toString() == getString(R.string.tambah)) {
            simpan()
        }
    }

    private fun simpan() {
        val id = myRef.push().key

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
                Toast.makeText(this, "Kategori berhasil ditambahkan", Toast.LENGTH_SHORT).show()

                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menambahkan kategori", Toast.LENGTH_SHORT).show()
            }
    }
    }