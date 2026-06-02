package com.sallie.pointofsales.produk

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ModelProdukActivity
import java.io.ByteArrayOutputStream

class ModProdukActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("produk")
    private val kategoriRef = database.getReference("kategori")
    private val cabangRef = database.getReference("cabang")
    private val storageRef = FirebaseStorage.getInstance().getReference("foto_produk")

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etNamaProduk: TextInputEditText
    private lateinit var etProductImageUrl: TextInputEditText
    private lateinit var spKategori: AutoCompleteTextView
    private lateinit var spCabang: AutoCompleteTextView
    private lateinit var etHargaBeli: TextInputEditText
    private lateinit var etNilaiProfit: TextInputEditText
    private lateinit var etHargaJual: TextInputEditText
    private lateinit var etStok: TextInputEditText
    private lateinit var cbUnlimited: CheckBox
    private lateinit var btnSimpan: Button
    private lateinit var imageView: ImageView
    private lateinit var tvPlaceholderFoto: TextView

    private var idProduk: String? = null
    private var imageUri: Uri? = null
    private var imageBitmap: Bitmap? = null
    private val listKategori = ArrayList<String>()
    private val listCabang = ArrayList<String>()
    private lateinit var kategoriAdapter: ArrayAdapter<String>
    private lateinit var cabangAdapter: ArrayAdapter<String>

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                imageBitmap = null
                imageView.setImageURI(uri)
                tvPlaceholderFoto.visibility = View.GONE
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                imageBitmap = bitmap
                imageUri = null
                imageView.setImageBitmap(bitmap)
                tvPlaceholderFoto.visibility = View.GONE
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

        etHargaBeli.addTextChangedListener { hitungHarga() }
        etNilaiProfit.addTextChangedListener { hitungHarga() }

        kategoriAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listKategori)
        cabangAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listCabang)

        spKategori.setAdapter(kategoriAdapter)
        spCabang.setAdapter(cabangAdapter)

        loadKategori()
        loadCabang()

        idProduk = intent.getStringExtra("ID_PRODUK")
        val namaProduk = intent.getStringExtra("NAMA_PRODUK")
        val productImageUrl = intent.getStringExtra("PRODUCT_IMAGE_URL")
        val kategoriProduk = intent.getStringExtra("KATEGORI_PRODUK")
        val cabangProduk = intent.getStringExtra("CABANG_PRODUK")
        val hargaBeli = intent.getIntExtra("HARGA_BELI", 0)
        val profitProduk = intent.getIntExtra("PROFIT_PRODUK", 0)
        val hargaJual = intent.getIntExtra("HARGA_JUAL", 0)
        val stokProduk = intent.getIntExtra("STOK_PRODUK", 0)

        if (idProduk != null) {
            val titleToolbar = toolbar.findViewById<TextView>(R.id.tvToolbarTitle)
            if (titleToolbar != null) titleToolbar.text = "Edit Produk"
            etNamaProduk.setText(namaProduk)
            etProductImageUrl.setText(productImageUrl)
            spKategori.setText(kategoriProduk, false)
            spCabang.setText(cabangProduk, false)
            etHargaBeli.setText(hargaBeli.toString())
            etNilaiProfit.setText(profitProduk.toString())
            etHargaJual.setText(hargaJual.toString())
            if (stokProduk == -1) {
                cbUnlimited.isChecked = true
                etStok.setText("")
            } else {
                cbUnlimited.isChecked = false
                etStok.setText(stokProduk.toString())
            }
            btnSimpan.text = "Simpan Perubahan"
            tvPlaceholderFoto.visibility = View.GONE
        }

        val btnGaleri = findViewById<Button>(R.id.btnGaleri)
        val btnKamera = findViewById<Button>(R.id.btnKamera)

        btnGaleri.setOnClickListener { galleryLauncher.launch("image/*") }
        btnKamera.setOnClickListener { cameraLauncher.launch(null) }
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.modproduk)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadKategori() {
        kategoriRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listKategori.clear()
                for (dataSnapshot in snapshot.children) {
                    val nameKategori = dataSnapshot.child("namaKategori").getValue(String::class.java)
                        ?: dataSnapshot.child("nameKategori").getValue(String::class.java)
                    if (nameKategori != null) {
                        listKategori.add(nameKategori)
                    }
                }
                kategoriAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadCabang() {
        cabangRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listCabang.clear()
                for (dataSnapshot in snapshot.children) {
                    val namaCabang = dataSnapshot.child("namaCabang").getValue(String::class.java)
                    if (namaCabang != null) {
                        listCabang.add(namaCabang)
                    }
                }
                cabangAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun init() {
        toolbar = findViewById(R.id.toolbar)
        etNamaProduk = findViewById(R.id.etNamaProduk)
        etProductImageUrl = findViewById(R.id.etProductImageUrl)
        spKategori = findViewById(R.id.spKategori)
        spCabang = findViewById(R.id.spCabang)
        etHargaBeli = findViewById(R.id.etHargaBeli)
        etNilaiProfit = findViewById(R.id.etNilaiProfit)
        etHargaJual = findViewById(R.id.etHargaJual)
        etStok = findViewById(R.id.etStok)
        cbUnlimited = findViewById(R.id.cbUnlimited)
        btnSimpan = findViewById(R.id.btnSimpan)
        imageView = findViewById(R.id.ivPreview)
        tvPlaceholderFoto = findViewById(R.id.tvPlaceholderFoto)

        btnSimpan.setOnClickListener { cekValidasi() }
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

        uploadFotoDanSimpan()
    }

    private fun uploadFotoDanSimpan() {
        val id = idProduk ?: myRef.push().key
        if (id == null) {
            Toast.makeText(this, "ID gagal dibuat", Toast.LENGTH_SHORT).show()
            return
        }

        val fileRef = storageRef.child("$id.jpg")

        if (imageUri != null) {
            fileRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { uri: Uri ->
                        simpanKeDatabase(id, uri.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal mengunggah foto produk", Toast.LENGTH_SHORT).show()
                }
        } else if (imageBitmap != null) {
            val baos = ByteArrayOutputStream()
            imageBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            fileRef.putBytes(data)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { uri: Uri ->
                        simpanKeDatabase(id, uri.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal mengunggah foto kamera", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Use URL from EditText if provided, otherwise empty
            val manualUrl = etProductImageUrl.text.toString().trim()
            simpanKeDatabase(id, manualUrl)
        }
    }

    private fun simpanKeDatabase(id: String, fotoUrl: String) {
        val produk = ModelProdukActivity(
            idProduk = id,
            namaProduk = etNamaProduk.text.toString().trim(),
            kategori = spKategori.text.toString().trim(),
            cabang = spCabang.text.toString().trim(),
            hargaBeli = etHargaBeli.text.toString().toIntOrNull() ?: 0,
            profit = etNilaiProfit.text.toString().toIntOrNull() ?: 0,
            hargaJual = etHargaJual.text.toString().toIntOrNull() ?: 0,
            stok = if (cbUnlimited.isChecked) -1 else (etStok.text.toString().toIntOrNull() ?: 0),
            productImageUrl = if (fotoUrl.isNotEmpty()) fotoUrl else etProductImageUrl.text.toString().trim()
        )

        myRef.child(id).setValue(produk)
            .addOnSuccessListener {
                val pesan = if (idProduk != null) "Data produk diubah" else "Produk berhasil ditambahkan"
                Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menambahkan produk", Toast.LENGTH_SHORT).show()
            }
    }
}
