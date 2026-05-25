package com.sallie.pointofsales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ItemKeranjang

class KeranjangAdapter(private var keranjangList: List<ItemKeranjang>) :
    RecyclerView.Adapter<KeranjangAdapter.KeranjangViewHolder>() {

    lateinit var appContext: Context

    interface OnItemClickListener {
        fun onItemHapusClick(item: ItemKeranjang, position: Int)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeranjangViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_keranjang, parent, false)
        appContext = parent.context
        return KeranjangViewHolder(view)
    }

    override fun onBindViewHolder(holder: KeranjangViewHolder, position: Int) {
        val item = keranjangList[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = keranjangList.size

    inner class KeranjangViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaProdukKeranjang: TextView = itemView.findViewById(R.id.tvNamaProdukKeranjang)
        val tvSubTotalKeranjang: TextView = itemView.findViewById(R.id.tvSubTotalKeranjang)
        val tvQtyKeranjang: TextView = itemView.findViewById(R.id.tvQtyKeranjang)
        val btnHapusItem: ImageButton = itemView.findViewById(R.id.btnHapusItem)

        fun bind(item: ItemKeranjang, position: Int) {
            tvNamaProdukKeranjang.text = item.namaProduk
            tvSubTotalKeranjang.text = "Rp${item.subTotal}"
            tvQtyKeranjang.text = "Jumlah Beli: ${item.jumlahBeli}"

            btnHapusItem.setOnClickListener {
                listener?.onItemHapusClick(item, position)
            }
        }
    }
}