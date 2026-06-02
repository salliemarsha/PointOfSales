package com.sallie.pointofsales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ItemKeranjang

class DetailItemsAdapter(private val items: List<ItemKeranjang>) :
    RecyclerView.Adapter<DetailItemsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama = view.findViewById<TextView>(R.id.tvNamaProdukKeranjang)
        val tvSubTotal = view.findViewById<TextView>(R.id.tvSubTotalKeranjang)
        val tvQty = view.findViewById<TextView>(R.id.tvQtyKeranjang)
        // Hide buttons as this is detail view only
        init {
            view.findViewById<View>(R.id.btnMinusItem)?.visibility = View.GONE
            view.findViewById<View>(R.id.btnPlusItem)?.visibility = View.GONE
            view.findViewById<View>(R.id.btnHapusItem)?.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_keranjang, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvNama.text = item.namaProduk
        holder.tvSubTotal.text = "Rp${item.subTotal}"
        holder.tvQty.text = "x${item.jumlahBeli}"
    }

    override fun getItemCount() = items.size
}
