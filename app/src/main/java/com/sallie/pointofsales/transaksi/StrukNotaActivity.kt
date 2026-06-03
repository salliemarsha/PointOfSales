package com.sallie.pointofsales.transaksi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.FirebaseDatabase
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ItemKeranjang
import com.sallie.pointofsales.model.ModelTransaksi
import com.sallie.pointofsales.printer.PrinterService

class StrukNotaActivity : AppCompatActivity() {

    private lateinit var tvIdStruk: TextView
    private lateinit var tvTanggalStruk: TextView
    private lateinit var tvTotalNota: TextView
    private lateinit var tvBayarNota: TextView
    private lateinit var tvKembaliNota: TextView
    private lateinit var btnTransaksiBaru: Button
    private lateinit var btnPrintNota: Button
    private lateinit var btnBagikanNota: Button
    private lateinit var cbAutoPrint: CheckBox
    private lateinit var toolbarStruk: MaterialToolbar
    private lateinit var llItemContainer: LinearLayout

    private lateinit var printerService: PrinterService
    private var currentReceiptData: PrinterService.ReceiptData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("StrukNotaActivity", "onCreate started")
        enableEdgeToEdge()
        setContentView(R.layout.activity_struk_nota)

        printerService = PrinterService(this)
        initComponent()

        val idTransaksi = intent.getStringExtra("ID_TRANSAKSI") ?: ""
        Log.d("StrukNotaActivity", "Loading ID: $idTransaksi")

        if (idTransaksi.isNotEmpty()) {
            ambilDataTransaksi(idTransaksi)
        } else {
            Toast.makeText(this, "ID Transaksi Kosong", Toast.LENGTH_SHORT).show()
        }

        setupListeners()
        loadAutoPrintPreference()
    }

    private fun setupListeners() {
        toolbarStruk.setNavigationOnClickListener { finish() }

        btnTransaksiBaru.setOnClickListener {
            // Return to Transaksi screen and clear history
            val intent = Intent(this, TransaksiActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        btnBagikanNota.setOnClickListener {
            currentReceiptData?.let { data ->
                // Construct a temporary model for sharing
                val mockTransaksi = ModelTransaksi(
                    idTransaksi = data.idTransaksi,
                    tanggal = data.tanggal,
                    namaCabang = data.storeName,
                    totalBayar = data.totalBayar.toIntOrNull() ?: 0,
                    uangBayar = data.uangBayar.toIntOrNull() ?: 0,
                    kembalian = data.kembalian.toIntOrNull() ?: 0,
                    itemTerjual = data.items
                )
                printerService.shareTransaction(mockTransaksi)
            } ?: Toast.makeText(this, "Data belum siap", Toast.LENGTH_SHORT).show()
        }

        btnPrintNota.setOnClickListener {
            currentReceiptData?.let {
                printerService.printReceipt(it)
            } ?: Toast.makeText(this, "Data nota belum siap", Toast.LENGTH_SHORT).show()
        }

        cbAutoPrint.setOnCheckedChangeListener { _, isChecked ->
            saveAutoPrintPreference(isChecked)
        }
    }

    private fun ambilDataTransaksi(id: String) {
        val ref = FirebaseDatabase.getInstance().getReference("transaksi").child(id)

        ref.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                Log.e("StrukNotaActivity", "Data not found for ID: $id")
                Toast.makeText(this, "Data transaksi tidak ditemukan", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            try {
                val idTx = snapshot.child("idTransaksi").value?.toString() ?: id
                val tanggal = snapshot.child("tanggal").value?.toString() ?: "-"
                val total = snapshot.child("totalBayar").value?.toString() ?: "0"
                val bayar = snapshot.child("uangBayar").value?.toString() ?: "0"
                val kembali = snapshot.child("kembalian").value?.toString() ?: "0"
                val cabang = snapshot.child("namaCabang").value?.toString() ?: "POS Store"

                tvIdStruk.text = "ID: #$idTx"
                tvTanggalStruk.text = tanggal
                tvTotalNota.text = "Rp$total"
                tvBayarNota.text = "Rp$bayar"
                tvKembaliNota.text = "Rp$kembali"

                val itemsSnapshot = snapshot.child("itemTerjual")
                val itemsList = mutableListOf<ItemKeranjang>()

                llItemContainer.removeAllViews()

                for (itemSnap in itemsSnapshot.children) {
                    val nama = itemSnap.child("namaProduk").value?.toString() ?: "Produk"
                    val qty = itemSnap.child("jumlahBeli").value?.toString()?.toIntOrNull() ?: 0
                    val harga = itemSnap.child("hargaJual").value?.toString()?.toIntOrNull() ?: 0
                    val sub = itemSnap.child("subTotal").value?.toString() ?: "0"
                    
                    itemsList.add(ItemKeranjang(namaProduk = nama, jumlahBeli = qty, hargaJual = harga))

                    val itemView = LayoutInflater.from(this)
                        .inflate(android.R.layout.simple_list_item_2, llItemContainer, false)

                    val text1 = itemView.findViewById<TextView>(android.R.id.text1)
                    val text2 = itemView.findViewById<TextView>(android.R.id.text2)

                    text1.text = "$nama x$qty"
                    text1.setTextColor(getColor(R.color.color2))
                    text2.text = "Rp$sub"
                    text2.setTextColor(getColor(R.color.color6))

                    llItemContainer.addView(itemView)
                }

                currentReceiptData = PrinterService.ReceiptData(
                    idTransaksi = idTx,
                    tanggal = tanggal,
                    storeName = cabang,
                    items = itemsList,
                    totalBayar = total,
                    uangBayar = bayar,
                    kembalian = kembali
                )

                if (cbAutoPrint.isChecked) {
                    currentReceiptData?.let { printerService.printReceipt(it) }
                }
            } catch (e: Exception) {
                Log.e("StrukNotaActivity", "Error parsing data: ${e.message}")
            }

        }.addOnFailureListener {
            Log.e("StrukNotaActivity", "Database error: ${it.message}")
            Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initComponent() {
        tvIdStruk = findViewById(R.id.tvIdStruk)
        tvTanggalStruk = findViewById(R.id.tvTanggalStruk)
        tvTotalNota = findViewById(R.id.tvTotalNota)
        tvBayarNota = findViewById(R.id.tvBayarNota)
        tvKembaliNota = findViewById(R.id.tvKembaliNota)
        btnTransaksiBaru = findViewById(R.id.btnTransaksiBaru)
        btnPrintNota = findViewById(R.id.btnPrintNota)
        btnBagikanNota = findViewById(R.id.btnBagikanNota)
        cbAutoPrint = findViewById(R.id.cbAutoPrint)
        toolbarStruk = findViewById(R.id.toolbarStruk)
        llItemContainer = findViewById(R.id.llItemContainer)
    }

    private fun saveAutoPrintPreference(autoPrint: Boolean) {
        val sharedPrefs = getSharedPreferences("printer_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("auto_print", autoPrint).apply()
    }

    private fun loadAutoPrintPreference() {
        val sharedPrefs = getSharedPreferences("printer_prefs", Context.MODE_PRIVATE)
        cbAutoPrint.isChecked = sharedPrefs.getBoolean("auto_print", false)
    }
}
