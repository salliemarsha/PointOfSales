package com.sallie.pointofsales.laporan

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sallie.pointofsales.R
import com.sallie.pointofsales.adapter.DetailItemsAdapter
import com.sallie.pointofsales.adapter.TransactionAdapter
import com.sallie.pointofsales.model.ItemKeranjang
import com.sallie.pointofsales.model.ModelTransaksi
import com.sallie.pointofsales.printer.PrinterService

class TransactionHistoryActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvHistory: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var adapter: TransactionAdapter
    
    private val transactions = mutableListOf<ModelTransaksi>()
    private val printerService by lazy { PrinterService(this) }
    private val database = FirebaseDatabase.getInstance().getReference("transaksi")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)

        initViews()
        setupRecyclerView()
        loadTransactions()
        
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbarHistory)
        rvHistory = findViewById(R.id.rvTransactionHistory)
        progressBar = findViewById(R.id.progressBar)
        layoutEmpty = findViewById(R.id.layoutEmpty)
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter(
            transactions,
            onItemClick = { transaction, isLatest -> 
                showDetail(transaction, isLatest) 
            },
            onPrintClick = { printerService.printTransaction(it) }
        )
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = adapter
    }

    private fun loadTransactions() {
        progressBar.visibility = View.VISIBLE
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                transactions.clear()
                for (data in snapshot.children) {
                    val trx = parseTransaction(data)
                    transactions.add(trx)
                }
                
                // Sort by date - Newest first (Latest transaction will be at index 0)
                transactions.sortByDescending { it.tanggal }
                
                adapter.updateData(transactions)
                progressBar.visibility = View.GONE
                layoutEmpty.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@TransactionHistoryActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun parseTransaction(data: DataSnapshot): ModelTransaksi {
        val id = data.child("idTransaksi").value?.toString() ?: ""
        val tanggal = data.child("tanggal").value?.toString() ?: ""
        val cabang = data.child("namaCabang").value?.toString() ?: ""
        val total = data.child("totalBayar").value?.toString()?.toIntOrNull() ?: 0
        val bayar = data.child("uangBayar").value?.toString()?.toIntOrNull() ?: 0
        val kembali = data.child("kembalian").value?.toString()?.toIntOrNull() ?: 0
        
        val items = mutableListOf<ItemKeranjang>()
        data.child("itemTerjual").children.forEach { itemSnap ->
            val item = ItemKeranjang(
                idProduk = itemSnap.child("idProduk").value?.toString() ?: "",
                namaProduk = itemSnap.child("namaProduk").value?.toString() ?: "",
                hargaJual = itemSnap.child("hargaJual").value?.toString()?.toIntOrNull() ?: 0,
                jumlahBeli = itemSnap.child("jumlahBeli").value?.toString()?.toIntOrNull() ?: 0,
                subTotal = itemSnap.child("subTotal").value?.toString()?.toIntOrNull() ?: 0
            )
            items.add(item)
        }
        
        return ModelTransaksi(id, tanggal, cabang, total, bayar, kembali, items)
    }

    private fun showDetail(transaction: ModelTransaksi, isLatest: Boolean) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_transaction_detail, null)
        
        view.findViewById<TextView>(R.id.tvDetailId).text = "#${transaction.idTransaksi}"
        view.findViewById<TextView>(R.id.tvDetailTanggal).text = transaction.tanggal
        view.findViewById<TextView>(R.id.tvDetailTotal).text = "Rp${transaction.totalBayar}"
        view.findViewById<TextView>(R.id.tvDetailBayar).text = "Rp${transaction.uangBayar}"
        view.findViewById<TextView>(R.id.tvDetailKembalian).text = "Rp${transaction.kembalian}"
        
        // Handle Latest Transaction Badge
        val tvLatest = view.findViewById<TextView>(R.id.tvLatestLabel)
        tvLatest.visibility = if (isLatest) View.VISIBLE else View.GONE

        val rvItems = view.findViewById<RecyclerView>(R.id.rvDetailItems)
        rvItems.layoutManager = LinearLayoutManager(this)
        rvItems.adapter = DetailItemsAdapter(transaction.itemTerjual)
        
        // Share Feature
        view.findViewById<MaterialButton>(R.id.btnShareDetail).setOnClickListener {
            printerService.shareTransaction(transaction)
            dialog.dismiss()
        }

        // Print Feature
        view.findViewById<MaterialButton>(R.id.btnPrintDetail).setOnClickListener {
            printerService.printTransaction(transaction)
            dialog.dismiss()
        }
        
        dialog.setContentView(view)
        dialog.show()
    }
}
