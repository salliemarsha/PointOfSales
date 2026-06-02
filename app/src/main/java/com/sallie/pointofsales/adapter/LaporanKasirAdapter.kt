package com.sallie.pointofsales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.LaporanKasirModel

class LaporanKasirAdapter(
    private val list: List<LaporanKasirModel>
) : RecyclerView.Adapter<LaporanKasirAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaKasir = view.findViewById<TextView>(R.id.tvNamaKasir)
        val tvJumlahTransaksi = view.findViewById<TextView>(R.id.tvJumlahTransaksi)
        val tvTotalPenjualan = view.findViewById<TextView>(R.id.tvTotalPenjualan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_laporan_kasir, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvNamaKasir.text = item.namaKasir
        holder.tvJumlahTransaksi.text = "Jumlah Transaksi: ${item.jumlahTransaksi}"
        holder.tvTotalPenjualan.text = "Total Penjualan: Rp${item.totalPenjualan}"
    }

    override fun getItemCount() = list.size
}
