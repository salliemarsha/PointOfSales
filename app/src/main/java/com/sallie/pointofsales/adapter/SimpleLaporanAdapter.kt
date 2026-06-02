package com.sallie.pointofsales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sallie.pointofsales.R

class SimpleLaporanAdapter(
    private val list: List<Map<String, Any>>
) : RecyclerView.Adapter<SimpleLaporanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId = view.findViewById<TextView>(R.id.tvItemIdTransaksi)
        val tvTanggal = view.findViewById<TextView>(R.id.tvItemTanggal)
        val tvTotal = view.findViewById<TextView>(R.id.tvItemTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_simple_laporan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvId.text = item["id"].toString()
        holder.tvTanggal.text = item["tanggal"].toString()
        holder.tvTotal.text = "Rp${item["total"]}"
    }

    override fun getItemCount() = list.size
}
