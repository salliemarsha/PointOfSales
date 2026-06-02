package com.sallie.pointofsales.produk

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.sallie.pointofsales.R
import com.sallie.pointofsales.adapter.DetailProdukAdapter
import com.sallie.pointofsales.model.ModelProdukActivity
import com.sallie.pointofsales.viewmodel.DataProdukViewModel

class DataProductActivity : AppCompatActivity() {

    private val viewModel: DataProdukViewModel by viewModels()
    private lateinit var rvDataProduk: RecyclerView
    private lateinit var fabAddProduct: FloatingActionButton
    private lateinit var etSearchProduct: TextInputEditText
    private lateinit var adapter: DetailProdukAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_product)

        init()

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        fabAddProduct.setOnClickListener {
            val intent = Intent(this, ModProdukActivity::class.java)
            startActivity(intent)
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        rvDataProduk.layoutManager = layoutManager
        rvDataProduk.setHasFixedSize(true)

        // Initialize DetailProdukAdapter with the required click and long click lambdas
        adapter = DetailProdukAdapter(
            onItemClicked = { produk ->
                if (!produk.idProduk.isNullOrBlank()) {
                    bukaHalamanEdit(produk)
                } else {
                    Toast.makeText(
                        this@DataProductActivity,
                        "Data tidak valid",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onItemLongClicked = { produk ->
                if (!produk.idProduk.isNullOrBlank()) {
                    tampilkanDialogHapus(produk)
                }
            }
        )
        rvDataProduk.adapter = adapter

        viewModel.produkList.observe(this) { list ->
            // Use submitList for ListAdapter
            adapter.submitList(list)
            if (list != null && list.isNotEmpty()) {
                rvDataProduk.scrollToPosition(list.size - 1)
            }
        }

        etSearchProduct.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun bukaHalamanEdit(produk: ModelProdukActivity) {
        val intent = Intent(this, ModProdukActivity::class.java).apply {
            putExtra("ID_PRODUK", produk.idProduk)
            putExtra("NAMA_PRODUK", produk.namaProduk)
            putExtra("PRODUCT_IMAGE_URL", produk.productImageUrl)
            putExtra("KATEGORI_PRODUK", produk.kategori)
            putExtra("CABANG_PRODUK", produk.cabang)
            putExtra("HARGA_BELI", produk.hargaBeli)
            putExtra("PROFIT_PRODUK", produk.profit)
            putExtra("HARGA_JUAL", produk.hargaJual)
            putExtra("STOK_PRODUK", produk.stok)
        }
        startActivity(intent)
    }

    private fun tampilkanDialogHapus(produk: ModelProdukActivity) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Produk")
            .setMessage("Apakah Anda yakin ingin menghapus ${produk.namaProduk}?")
            .setPositiveButton("Hapus") { _, _ ->
                hapusProdukDariFirebase(produk)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun hapusProdukDariFirebase(produk: ModelProdukActivity) {
        val id = produk.idProduk ?: return
        val databaseRef = FirebaseDatabase.getInstance().getReference("produk")
        val storageRef = FirebaseStorage.getInstance().getReference("foto_produk")

        databaseRef.child(id).removeValue().addOnSuccessListener {
            if (!produk.productImageUrl.isNullOrEmpty()) {
                val fileRef = storageRef.child("$id.jpg")
                fileRef.delete().addOnCompleteListener {
                    Toast.makeText(this, "Produk dan gambar berhasil dihapus", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal menghapus produk", Toast.LENGTH_SHORT).show()
        }
    }

    private fun init() {
        rvDataProduk = findViewById(R.id.rvDataProduk)
        fabAddProduct = findViewById(R.id.fabAddProduct)
        etSearchProduct = findViewById(R.id.etSearchProduct)
    }
}
