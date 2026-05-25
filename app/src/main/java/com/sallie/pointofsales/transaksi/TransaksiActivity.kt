package com.sallie.pointofsales.transaksi

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sallie.pointofsales.R
import com.sallie.pointofsales.adapter.KeranjangAdapter
import com.sallie.pointofsales.model.ItemKeranjang
import com.sallie.pointofsales.model.ModelTransaksi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransaksiActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val transaksiRef = database.getReference("transaksi")
    private val cabangRef = database.getReference("cabang")
    private val produkRef = database.getReference("produk")

    private lateinit var toolbar: MaterialToolbar
    private lateinit var spCabangTransaksi: AutoCompleteTextView
    private lateinit var spPilihProduk: AutoCompleteTextView
    private lateinit var etJumlahBeli: EditText
    private lateinit var btnTambahKeranjang: Button
    private lateinit var tvTotalBelanja: TextView
    private lateinit var btnSelesaikanTransaksi: Button
    private lateinit var rvKeranjang: RecyclerView

    private val listCabang = ArrayList<String>()
    private val listProduk = ArrayList<DataSnapshot>()
    private val listNamaProduk = ArrayList<String>()
    private val keranjangBelanja = ArrayList<ItemKeranjang>()

    private lateinit var cabangAdapter: ArrayAdapter<String>
    private lateinit var produkAdapter: ArrayAdapter<String>
    private lateinit var keranjangAdapter: KeranjangAdapter

    private var totalBelanja = 0
    private var cabangTerpilih = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaksi)

        init()
        setupAdapters()
        loadCabang()

        spCabangTransaksi.setOnItemClickListener { _, _, position, _ ->
            cabangTerpilih = listCabang[position]
            loadProdukPerCabang(cabangTerpilih)
            keranjangBelanja.clear()
            rvKeranjang.adapter?.notifyDataSetChanged()
            hitungTotal()
        }

        btnTambahKeranjang.setOnClickListener { tambahKeKeranjang() }
        btnSelesaikanTransaksi.setOnClickListener { simpanTransaksi() }
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.transaksi_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun init() {
        toolbar = findViewById(R.id.toolbarTransaksi)
        spCabangTransaksi = findViewById(R.id.spCabangTransaksi)
        spPilihProduk = findViewById(R.id.spPilihProduk)
        etJumlahBeli = findViewById(R.id.etJumlahBeli)
        btnTambahKeranjang = findViewById(R.id.btnTambahKeranjang)
        tvTotalBelanja = findViewById(R.id.tvTotalBelanja)
        btnSelesaikanTransaksi = findViewById(R.id.btnSelesaikanTransaksi)
        rvKeranjang = findViewById(R.id.rvKeranjang)
    }

    private fun setupAdapters() {
        cabangAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listCabang)
        spCabangTransaksi.setAdapter(cabangAdapter)

        produkAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listNamaProduk)
        spPilihProduk.setAdapter(produkAdapter)

        rvKeranjang.layoutManager = LinearLayoutManager(this)
        keranjangAdapter = KeranjangAdapter(keranjangBelanja)
        rvKeranjang.adapter = keranjangAdapter
    }

    private fun loadCabang() {
        cabangRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listCabang.clear()
                for (dataSnapshot in snapshot.children) {
                    val nama = dataSnapshot.child("namaCabang").getValue(String::class.java)
                    if (nama != null) listCabang.add(nama)
                }
                cabangAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadProdukPerCabang(cabang: String) {
        produkRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listProduk.clear()
                listNamaProduk.clear()
                for (dataSnapshot in snapshot.children) {
                    val cabangProduk = dataSnapshot.child("cabang").getValue(String::class.java)
                    if (cabangProduk == cabang) {
                        val nama = dataSnapshot.child("namaProduk").getValue(String::class.java)
                        val harga = dataSnapshot.child("hargaJual").getValue(Int::class.java) ?: 0
                        if (nama != null) {
                            listProduk.add(dataSnapshot)
                            listNamaProduk.add("$nama - Rp$harga")
                        }
                    }
                }
                produkAdapter.notifyDataSetChanged()
                spPilihProduk.setText("", false)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun tambahKeKeranjang() {
        val produkInput = spPilihProduk.text.toString()
        val jumlahInput = etJumlahBeli.text.toString()

        if (cabangTerpilih.isEmpty() || produkInput.isEmpty() || jumlahInput.isEmpty()) {
            Toast.makeText(this, "Lengkapi data pilihan terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        val index = listNamaProduk.indexOf(produkInput)
        if (index == -1) return

        val snapshot = listProduk[index]
        val id = snapshot.child("idProduk").getValue(String::class.java) ?: ""
        val nama = snapshot.child("namaProduk").getValue(String::class.java) ?: ""
        val harga = snapshot.child("hargaJual").getValue(Int::class.java) ?: 0
        val jumlah = jumlahInput.toInt()
        val sub = harga * jumlah

        val item = ItemKeranjang(id, nama, harga, jumlah, sub)
        keranjangBelanja.add(item)

        rvKeranjang.adapter?.notifyDataSetChanged()
        hitungTotal()

        etJumlahBeli.setText("")
        spPilihProduk.setText("", false)
    }

    private fun hitungTotal() {
        totalBelanja = 0
        for (item in keranjangBelanja) {
            totalBelanja += item.subTotal
        }
        tvTotalBelanja.text = "Total: Rp$totalBelanja"
    }

    private fun simpanTransaksi() {
        if (keranjangBelanja.isEmpty()) {
            Toast.makeText(this, "Keranjang belanja masih kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val idTx = transaksiRef.push().key ?: return
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val tanggalSekarang = sdf.format(Date())

        val transaksi = ModelTransaksi(
            idTransaksi = idTx,
            tanggal = tanggalSekarang,
            namaCabang = cabangTerpilih,
            totalBayar = totalBelanja,
            itemTerjual = keranjangBelanja
        )

        transaksiRef.child(idTx).setValue(transaksi)
            .addOnSuccessListener {
                Toast.makeText(this, "Transaksi Berhasil", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, PembayaranActivity::class.java)
                intent.putExtra("TOTAL_BELANJA", totalBelanja)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Transaksi Gagal", Toast.LENGTH_SHORT).show()
            }
    }
}