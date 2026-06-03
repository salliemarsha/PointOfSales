package com.sallie.pointofsales.laporan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sallie.pointofsales.R
import com.sallie.pointofsales.adapter.TransactionAdapter
import com.sallie.pointofsales.model.ItemKeranjang
import com.sallie.pointofsales.model.ModelTransaksi
import com.sallie.pointofsales.transaksi.StrukNotaActivity

class TransactionHistoryActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvHistory: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var adapter: TransactionAdapter
    
    private val transactions = mutableListOf<ModelTransaksi>()
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
        adapter = TransactionAdapter(transactions) { transaction ->
            // Click Behavior: Navigate to Printer Screen (StrukNotaActivity)
            Log.d("TransactionHistory", "Opening Printer Screen for: ${transaction.idTransaksi}")
            val intent = Intent(this, StrukNotaActivity::class.java)
            intent.putExtra("ID_TRANSAKSI", transaction.idTransaksi)
            startActivity(intent)
        }
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = adapter
    }

    private fun loadTransactions() {
        progressBar.visibility = View.VISIBLE
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                transactions.clear()
                for (data in snapshot.children) {
                    try {
                        val trx = parseTransaction(data)
                        transactions.add(trx)
                    } catch (e: Exception) {
                        Log.e("TransactionHistory", "Error parsing transaction: ${e.message}")
                    }
                }
                
                // Sort by ID or Date descending (Newest first)
                transactions.sortByDescending { it.idTransaksi }
                
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
        val total = data.child("totalBayar").value?.toString()?.toIntOrNull() 
                    ?: data.child("totalHarga").value?.toString()?.toIntOrNull() ?: 0
        
        // We only need ID and Total for the simple history list
        return ModelTransaksi(idTransaksi = id, totalBayar = total)
    }
}
