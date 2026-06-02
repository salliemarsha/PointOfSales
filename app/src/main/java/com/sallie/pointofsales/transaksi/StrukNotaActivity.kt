package com.sallie.pointofsales.transaksi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.FirebaseDatabase
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ItemKeranjang

class StrukNotaActivity : AppCompatActivity() {

    private lateinit var tvIdStruk: TextView
    private lateinit var tvTanggalStruk: TextView
    private lateinit var tvTotalNota: TextView
    private lateinit var tvBayarNota: TextView
    private lateinit var tvKembaliNota: TextView
    private lateinit var btnTransaksiBaru: Button
    private lateinit var toolbarStruk: MaterialToolbar
    private lateinit var llItemContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_struk_nota)

        initComponent()

        val idTransaksi = intent.getStringExtra("ID_TRANSAKSI") ?: ""

        if (idTransaksi.isNotEmpty()) {
            ambilDataTransaksi(idTransaksi)
        }

        toolbarStruk.setNavigationOnClickListener { finish() }

        btnTransaksiBaru.setOnClickListener {
            val intent = Intent(this, TransaksiActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun ambilDataTransaksi(id: String) {
        val ref = FirebaseDatabase.getInstance().getReference("transaksi").child(id)

        ref.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) return@addOnSuccessListener

            tvIdStruk.text = "ID: #${snapshot.child("idTransaksi").value}"
            tvTanggalStruk.text = snapshot.child("tanggal").value.toString()
            tvTotalNota.text = "Rp${snapshot.child("totalBayar").value}"
            tvBayarNota.text = "Rp${snapshot.child("uangBayar").value}"
            tvKembaliNota.text = "Rp${snapshot.child("kembalian").value}"

            val itemsSnapshot = snapshot.child("itemTerjual")

            llItemContainer.removeAllViews()

            for (itemSnap in itemsSnapshot.children) {
                val nama = itemSnap.child("namaProduk").value.toString()
                val qty = itemSnap.child("jumlahBeli").value.toString()
                val harga = itemSnap.child("harga").value.toString()
                val subTotal = itemSnap.child("subTotal").value.toString()

                val view = LayoutInflater.from(this)
                    .inflate(android.R.layout.simple_list_item_2, llItemContainer, false)

                val text1 = view.findViewById<TextView>(android.R.id.text1)
                val text2 = view.findViewById<TextView>(android.R.id.text2)

                text1.text = "$nama x$qty"
                text2.text = "Rp$subTotal"

                llItemContainer.addView(view)
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
        toolbarStruk = findViewById(R.id.toolbarStruk)
        llItemContainer = findViewById(R.id.llItemContainer)
    }
}