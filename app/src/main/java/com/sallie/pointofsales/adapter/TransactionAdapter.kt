package com.sallie.pointofsales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ModelTransaksi

class TransactionAdapter(
    private var transactions: List<ModelTransaksi>,
    private val onItemClick: (ModelTransaksi) -> Unit
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
        holder.bind(transaction)
    }

    override fun getItemCount(): Int = transactions.size

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val card = view.findViewById<MaterialCardView>(R.id.cardTransaction)
        private val tvId = view.findViewById<TextView>(R.id.tvIdTransaksi)
        private val tvTotal = view.findViewById<TextView>(R.id.tvTotal)

        fun bind(transaction: ModelTransaksi) {
            tvId.text = transaction.idTransaksi
            tvTotal.text = "Rp${transaction.totalBayar}"

            card.setOnClickListener { onItemClick(transaction) }
        }
    }
}
