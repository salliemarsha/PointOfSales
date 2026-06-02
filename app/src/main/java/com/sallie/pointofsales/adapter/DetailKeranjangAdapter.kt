package com.sallie.pointofsales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ItemKeranjang

class KeranjangAdapter(
    private val keranjangList: MutableList<ItemKeranjang>,
    private val listener: OnItemChangeListener? = null
) : RecyclerView.Adapter<KeranjangAdapter.KeranjangViewHolder>() {

    interface OnItemChangeListener {
        fun onItemDeleted(item: ItemKeranjang, position: Int)
        fun onItemUpdated()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeranjangViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_keranjang, parent, false)
        return KeranjangViewHolder(view)
    }

    override fun onBindViewHolder(holder: KeranjangViewHolder, position: Int) {
        holder.bind(keranjangList[position])
    }

    override fun getItemCount(): Int = keranjangList.size

    inner class KeranjangViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvNamaProdukKeranjang: TextView =
            itemView.findViewById(R.id.tvNamaProdukKeranjang)

        private val tvSubTotalKeranjang: TextView =
            itemView.findViewById(R.id.tvSubTotalKeranjang)

        private val tvQtyKeranjang: TextView =
            itemView.findViewById(R.id.tvQtyKeranjang)

        private val btnHapusItem: ImageButton =
            itemView.findViewById(R.id.btnHapusItem)

        private val btnPlusItem: ImageButton =
            itemView.findViewById(R.id.btnPlusItem)

        private val btnMinusItem: ImageButton =
            itemView.findViewById(R.id.btnMinusItem)

        fun bind(item: ItemKeranjang) {

            tvNamaProdukKeranjang.text = item.namaProduk
            tvQtyKeranjang.text = "Qty: ${item.jumlahBeli}"
            tvSubTotalKeranjang.text = "Rp${item.subTotal}"

            btnPlusItem.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    item.jumlahBeli += 1
                    item.subTotal = item.hargaJual * item.jumlahBeli
                    notifyItemChanged(position)
                    listener?.onItemUpdated()
                }
            }

            btnMinusItem.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    if (item.jumlahBeli > 1) {
                        item.jumlahBeli -= 1
                        item.subTotal = item.hargaJual * item.jumlahBeli
                        notifyItemChanged(position)
                        listener?.onItemUpdated()
                    }
                }
            }

            btnHapusItem.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemDeleted(item, position)
                }
            }
        }
    }

    fun removeItem(position: Int) {
        if (position in keranjangList.indices) {
            keranjangList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
