package com.sallie.pointofsales.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ModelTransaksi

class TransactionAdapter(
    private var transactions: List<ModelTransaksi>,
    private val onItemClick: (ModelTransaksi, Boolean) -> Unit,
    private val onPrintClick: (ModelTransaksi) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    fun updateData(newList: List<ModelTransaksi>) {
        transactions = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        val isLatest = position == 0
        holder.bind(transaction, isLatest)
    }

    override fun getItemCount(): Int = transactions.size

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val card = view.findViewById<MaterialCardView>(R.id.cardTransaction)
        private val tvId = view.findViewById<TextView>(R.id.tvIdTransaksi)
        private val tvDate = view.findViewById<TextView>(R.id.tvTanggal)
        private val tvStore = view.findViewById<TextView>(R.id.tvStoreName)
        private val tvTotal = view.findViewById<TextView>(R.id.tvTotal)
        private val btnPrint = view.findViewById<ImageButton>(R.id.btnQuickPrint)

        fun bind(transaction: ModelTransaksi, isLatest: Boolean) {
            tvId.text = "#${transaction.idTransaksi.takeLast(8).uppercase()}"
            tvDate.text = transaction.tanggal
            tvStore.text = transaction.namaCabang.ifEmpty { "POS Store" }
            tvTotal.text = "Rp${transaction.totalBayar}"

            if (isLatest) {
                card.strokeColor = Color.parseColor("#4CAF50") // Green for latest
                card.strokeWidth = 4
            } else {
                card.strokeColor = Color.parseColor("#E0E0E0")
                card.strokeWidth = 2
            }

            card.setOnClickListener { onItemClick(transaction, isLatest) }
            btnPrint.setOnClickListener { onPrintClick(transaction) }
        }
    }
}
