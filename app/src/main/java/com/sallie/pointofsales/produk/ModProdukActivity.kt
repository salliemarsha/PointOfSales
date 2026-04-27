package com.sallie.pointofsales.produk

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ModelProdukActivity

class ModProdukActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("produk")

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etNamaProduk: TextInputEditText
    private lateinit var spKategori: AutoCompleteTextView
    private lateinit var spCabang: AutoCompleteTextView
    private lateinit var etHargaBeli: TextInputEditText
    private lateinit var etNilaiProfit: TextInputEditText
    private lateinit var etHargaJual: TextInputEditText
    private lateinit var etStok: TextInputEditText
    private lateinit var cbUnlimited: CheckBox
    private lateinit var btnSimpan: Button
    private lateinit var imageView: ImageView

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageView.setImageURI(uri)
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mod_produk)

        init()

        fun hitungHarga() {
            val beli = etHargaBeli.text.toString().toIntOrNull() ?: 0
            val profit = etNilaiProfit.text.toString().toIntOrNull() ?: 0

            val jual = beli + profit
            etHargaJual.setText(jual.toString())
        }

        etHargaBeli.addTextChangedListener {
            hitungHarga()
        }

        etNilaiProfit.addTextChangedListener {
            hitungHarga()
        }

        val kategoriList = arrayOf("Makanan", "Minuman", "Snack")
        val cabangList = arrayOf("Cabang 1", "Cabang 2", "Cabang 3")

        val kategoriAdapter = ArrayAdapter(this, R.layout.dropdown_item, kategoriList)
        val cabangAdapter = ArrayAdapter(this, R.layout.dropdown_item, cabangList)

        val btnGaleri = findViewById<Button>(R.id.btnGaleri)
        val btnKamera = findViewById<Button>(R.id.btnKamera)

        toolbar = findViewById(R.id.toolbar)

        btnGaleri.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        btnKamera.setOnClickListener {
            cameraLauncher.launch(null)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        spKategori.setAdapter(kategoriAdapter)
        spCabang.setAdapter(cabangAdapter)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.modproduk)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun init() {
        etNamaProduk = findViewById(R.id.etNamaProduk)
        spKategori = findViewById(R.id.spKategori)
        spCabang = findViewById(R.id.spCabang)
        etHargaBeli = findViewById(R.id.etHargaBeli)
        etNilaiProfit = findViewById(R.id.etNilaiProfit)
        etHargaJual = findViewById(R.id.etHargaJual)
        etStok = findViewById(R.id.etStok)
        cbUnlimited = findViewById(R.id.cbUnlimited)
        btnSimpan = findViewById(R.id.btnSimpan)
        imageView = findViewById(R.id.ivPreview)

        btnSimpan.setOnClickListener {
            cekValidasi()
        }
    }

    private fun cekValidasi() {
        val nama = etNamaProduk.text.toString().trim()
        val kategori = spKategori.text.toString().trim()
        val cabang = spCabang.text.toString().trim()
        val hargaBeli = etHargaBeli.text.toString().trim()
        val profit = etNilaiProfit.text.toString().trim()
        val hargaJual = etHargaJual.text.toString().trim()
        val stok = etStok.text.toString().trim()

        if (nama.isEmpty()) {
            etNamaProduk.error = "Nama produk tidak boleh kosong"
            etNamaProduk.requestFocus()
            return
        }

        if (kategori.isEmpty()) {
            spKategori.error = "Kategori tidak boleh kosong"
            spKategori.requestFocus()
            return
        }

        if (cabang.isEmpty()) {
            spCabang.error = "Cabang tidak boleh kosong"
            spCabang.requestFocus()
            return
        }

        if (hargaBeli.isEmpty()) {
            etHargaBeli.error = "Harga beli tidak boleh kosong"
            etHargaBeli.requestFocus()
            return
        }

        if (profit.isEmpty()) {
            etNilaiProfit.error = "Profit tidak boleh kosong"
            etNilaiProfit.requestFocus()
            return
        }

        if (hargaJual.isEmpty()) {
            etHargaJual.error = "Harga jual tidak boleh kosong"
            etHargaJual.requestFocus()
            return
        }

        if (!cbUnlimited.isChecked && stok.isEmpty()) {
            etStok.error = "Stok tidak boleh kosong"
            etStok.requestFocus()
            return
        }

        simpan()
    }

    private fun simpan() {
        val id = myRef.push().key

        if (id == null) {
            Toast.makeText(this, "ID gagal dibuat", Toast.LENGTH_SHORT).show()
            return
        }

        val produk = ModelProdukActivity(
            idProduk = id,
            namaProduk = etNamaProduk.text.toString().trim(),
            kategori = spKategori.text.toString().trim(),
            cabang = spCabang.text.toString().trim(),
            hargaBeli = etHargaBeli.text.toString().toInt(),
            profit = etNilaiProfit.text.toString().toInt(),
            hargaJual = etHargaJual.text.toString().toInt(),
            stok = if (cbUnlimited.isChecked) -1 else etStok.text.toString().toInt()
        )

        myRef.child(id).setValue(produk)
            .addOnSuccessListener {
                Toast.makeText(this, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menambahkan produk", Toast.LENGTH_SHORT).show()
            }
    }
}