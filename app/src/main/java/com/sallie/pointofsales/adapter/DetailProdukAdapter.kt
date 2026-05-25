package com.sallie.pointofsales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ModelProdukActivity

class DetailProdukAdapter(private var produkList: List<ModelProdukActivity>) :
    RecyclerView.Adapter<DetailProdukAdapter.ProdukViewHolder>() {

    lateinit var appContext: Context

    interface OnItemClickListener {
        fun onItemClick(produk: ModelProdukActivity)
        fun onItemLongClick(produk: ModelProdukActivity)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun updateData(newList: List<ModelProdukActivity>) {
        produkList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdukViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_data_product, parent, false)
        appContext = parent.context
        return ProdukViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdukViewHolder, position: Int) {
        val produk = produkList[position]
        holder.bind(produk)
    }

    override fun getItemCount(): Int {
        return produkList.size
    }

    inner class ProdukViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val ivLogo: ImageView = itemView.findViewById(R.id.ivLogo)
        val tvNamaProduk: TextView = itemView.findViewById(R.id.tvNamaProduk)
        val tvHargaProduk: TextView = itemView.findViewById(R.id.tvHarga)
        val chipStatus: Chip = itemView.findViewById(R.id.chipStatus)
        val tvKategoriProduk: TextView = itemView.findViewById(R.id.tvKategoriProduk)
        val tvStokProduk: TextView = itemView.findViewById(R.id.tvStokProduk)
        val tvCabangProduk: TextView = itemView.findViewById(R.id.tvCabangProduk)

        fun bind(produk: ModelProdukActivity) {
            tvNamaProduk.text = produk.namaProduk ?: "-"
            tvHargaProduk.text = "Rp${produk.hargaJual ?: 0}"
            tvKategoriProduk.text = produk.kategori ?: "-"
            tvCabangProduk.text = produk.cabang ?: "-"

            if (produk.stok == -1) {
                tvStokProduk.text = "Tidak Terbatas"
                chipStatus.text = "Aktif"
                chipStatus.setChipIconResource(R.drawable.ic_check)
            } else {
                val currentStock = produk.stok ?: 0
                tvStokProduk.text = "Stok: $currentStock"
                if (currentStock > 0) {
                    chipStatus.text = "Aktif"
                    chipStatus.setChipIconResource(R.drawable.ic_check)
                } else {
                    chipStatus.text = "Habis"
                    chipStatus.setChipIconResource(R.drawable.stok)
                }
            }

            val urlFoto = produk.fotoUrl
            if (!urlFoto.isNullOrEmpty()) {
                Glide.with(appContext)
                    .load(urlFoto)
                    .placeholder(R.drawable.produk)
                    .error(R.drawable.produk)
                    .centerCrop()
                    .into(ivLogo)
            } else {
                ivLogo.setImageResource(R.drawable.produk)
                ivLogo.scaleType = ImageView.ScaleType.CENTER_INSIDE
            }

            itemView.setOnClickListener {
                listener?.onItemClick(produk)
            }

            itemView.setOnLongClickListener {
                listener?.onItemLongClick(produk)
                true
            }
        }
    }
}