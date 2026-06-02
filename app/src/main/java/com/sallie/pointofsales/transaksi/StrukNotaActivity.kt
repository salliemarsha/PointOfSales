package com.sallie.pointofsales.transaksi

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.sallie.pointofsales.printer.PrinterService

class StrukNotaActivity : AppCompatActivity() {

    private lateinit var tvIdStruk: TextView
    private lateinit var tvTanggalStruk: TextView
    private lateinit var tvTotalNota: TextView
    private lateinit var tvBayarNota: TextView
    private lateinit var tvKembaliNota: TextView
    private lateinit var btnTransaksiBaru: Button
    private lateinit var btnPrintNota: Button
    private lateinit var cbAutoPrint: CheckBox
    private lateinit var toolbarStruk: MaterialToolbar
    private lateinit var llItemContainer: LinearLayout

    private lateinit var printerService: PrinterService
    private var currentReceiptData: PrinterService.ReceiptData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_struk_nota)

        printerService = PrinterService(this)
        initComponent()

        val idTransaksi = intent.getStringExtra("ID_TRANSAKSI") ?: ""

        if (idTransaksi.isNotEmpty()) {
            ambilDataTransaksi(idTransaksi)
        }

        setupListeners()
        loadAutoPrintPreference()
    }

    private fun setupListeners() {
        toolbarStruk.setNavigationOnClickListener { finish() }

        btnTransaksiBaru.setOnClickListener {
            val intent = Intent(this, TransaksiActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
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
            if (!snapshot.exists()) return@addOnSuccessListener

            val idTx = snapshot.child("idTransaksi").value.toString()
            val tanggal = snapshot.child("tanggal").value.toString()
            val total = snapshot.child("totalBayar").value.toString()
            val bayar = snapshot.child("uangBayar").value.toString()
            val kembali = snapshot.child("kembalian").value.toString()

            tvIdStruk.text = "ID: #$idTx"
            tvTanggalStruk.text = tanggal
            tvTotalNota.text = "Rp$total"
            tvBayarNota.text = "Rp$bayar"
            tvKembaliNota.text = "Rp$kembali"

            val itemsSnapshot = snapshot.child("itemTerjual")
            val itemsList = mutableListOf<ItemKeranjang>()

            llItemContainer.removeAllViews()

            for (itemSnap in itemsSnapshot.children) {
                val nama = itemSnap.child("namaProduk").value.toString()
                val qty = itemSnap.child("jumlahBeli").value.toString().toInt()
                val harga = itemSnap.child("harga").value.toString().toInt()
                val subTotal = itemSnap.child("subTotal").value.toString()
                val idProduk = itemSnap.child("idProduk").value.toString()

                val item = ItemKeranjang(idProduk, nama, harga, qty)
                itemsList.add(item)

                val view = LayoutInflater.from(this)
                    .inflate(android.R.layout.simple_list_item_2, llItemContainer, false)

                val text1 = view.findViewById<TextView>(android.R.id.text1)
                val text2 = view.findViewById<TextView>(android.R.id.text2)

                text1.text = "$nama x$qty"
                text2.text = "Rp$subTotal"

                llItemContainer.addView(view)
            }

            currentReceiptData = PrinterService.ReceiptData(
                idTransaksi = idTx,
                tanggal = tanggal,
                items = itemsList,
                totalBayar = total,
                uangBayar = bayar,
                kembalian = kembali
            )

            // Trigger auto print if enabled
            if (cbAutoPrint.isChecked) {
                currentReceiptData?.let { printerService.printReceipt(it) }
            }

        }.addOnFailureListener {
            Toast.makeText(this, "Gagal memuat data nota", Toast.LENGTH_SHORT).show()
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
