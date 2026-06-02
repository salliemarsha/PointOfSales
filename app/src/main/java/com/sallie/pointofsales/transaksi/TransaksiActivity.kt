package com.sallie.pointofsales.transaksi

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.*
import com.sallie.pointofsales.R
import com.sallie.pointofsales.adapter.KeranjangAdapter
import com.sallie.pointofsales.model.ItemKeranjang
import com.sallie.pointofsales.model.ModelTransaksi
import java.text.SimpleDateFormat
import java.util.*

class TransaksiActivity : AppCompatActivity(), KeranjangAdapter.OnItemChangeListener {

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
            resetKeranjang()
        }

        btnTambahKeranjang.setOnClickListener { tambahKeKeranjang() }
        btnSelesaikanTransaksi.setOnClickListener { simpanTransaksi() }
        toolbar.setNavigationOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.transaksi_root)) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
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
        keranjangAdapter = KeranjangAdapter(keranjangBelanja, this)
        rvKeranjang.adapter = keranjangAdapter
    }

    private fun loadCabang() {
        cabangRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listCabang.clear()
                for (data in snapshot.children) {
                    data.child("namaCabang").getValue(String::class.java)?.let {
                        listCabang.add(it)
                    }
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

                for (data in snapshot.children) {
                    val cab = data.child("cabang").getValue(String::class.java)
                    if (cab == cabang) {

                        val nama = data.child("namaProduk").getValue(String::class.java)
                        val harga = data.child("hargaJual").getValue(Int::class.java) ?: 0

                        if (nama != null) {
                            listProduk.add(data)
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
        val jumlahStr = etJumlahBeli.text.toString()
        val jumlah = jumlahStr.toIntOrNull()

        if (cabangTerpilih.isEmpty() || produkInput.isEmpty() || jumlah == null || jumlah <= 0) {
            Toast.makeText(this, "Data belum lengkap atau jumlah tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val index = listNamaProduk.indexOf(produkInput)
        if (index == -1) return

        val snap = listProduk[index]

        val id = snap.child("idProduk").getValue(String::class.java) ?: ""
        val nama = snap.child("namaProduk").getValue(String::class.java) ?: ""
        val harga = snap.child("hargaJual").getValue(Int::class.java) ?: 0

        val item = ItemKeranjang(id, nama, harga, jumlah, harga * jumlah)

        keranjangBelanja.add(item)
        keranjangAdapter.notifyDataSetChanged()
        hitungTotal()

        etJumlahBeli.setText("")
        spPilihProduk.setText("", false)
    }

    private fun hitungTotal() {
        totalBelanja = keranjangBelanja.sumOf { it.subTotal }
        tvTotalBelanja.text = "Rp$totalBelanja"
    }

    private fun resetKeranjang() {
        keranjangBelanja.clear()
        keranjangAdapter.notifyDataSetChanged()
        hitungTotal()
    }

    override fun onItemDeleted(item: ItemKeranjang, position: Int) {
        keranjangBelanja.removeAt(position)
        keranjangAdapter.notifyItemRemoved(position)
        hitungTotal()
    }

    override fun onItemUpdated() {
        hitungTotal()
    }

    private fun simpanTransaksi() {
        if (keranjangBelanja.isEmpty()) {
            Toast.makeText(this, "Keranjang kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val idTx = transaksiRef.push().key ?: return

        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val tanggal = sdf.format(Date())

        val transaksi = ModelTransaksi(
            idTransaksi = idTx,
            tanggal = tanggal,
            namaCabang = cabangTerpilih,
            totalBayar = totalBelanja,
            itemTerjual = keranjangBelanja
        )

        transaksiRef.child(idTx).setValue(transaksi)
            .addOnSuccessListener {
                val intent = Intent(this, PembayaranActivity::class.java)
                intent.putExtra("TOTAL_BELANJA", totalBelanja)
                intent.putExtra("ID_TRANSAKSI", idTx)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal simpan transaksi", Toast.LENGTH_SHORT).show()
            }
    }
}
