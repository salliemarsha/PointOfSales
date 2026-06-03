package com.sallie.pointofsales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.chip.Chip
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ModelProdukActivity

/**
 * Standardized Product Adapter
 * - Uses ListAdapter with DiffUtil for optimized updates.
 * - Centralized Glide logic for consistent image loading.
 * - Lambda-based click handling for clean separation of concerns.
 */
class DetailProdukAdapter(
    private val onItemClicked: (ModelProdukActivity) -> Unit,
    private val onItemLongClicked: (ModelProdukActivity) -> Unit
) : ListAdapter<ModelProdukActivity, DetailProdukAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_data_product, parent, false)
        return ViewHolder(view, onItemClicked, onItemLongClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        view: View,
        private val onClick: (ModelProdukActivity) -> Unit,
        private val onLongClick: (ModelProdukActivity) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val ivLogo: ImageView = view.findViewById(R.id.ivLogo)
        private val tvNamaProduk: TextView = view.findViewById(R.id.tvNamaProduk)
        private val tvHargaProduk: TextView = view.findViewById(R.id.tvHarga)
        private val chipStatus: Chip = view.findViewById(R.id.chipStatus)
        private val tvKategoriProduk: TextView = view.findViewById(R.id.tvKategoriProduk)
        private val tvStokProduk: TextView = view.findViewById(R.id.tvStokProduk)
        private val tvCabangProduk: TextView = view.findViewById(R.id.tvCabangProduk)

        fun bind(produk: ModelProdukActivity) {
            tvNamaProduk.text = produk.namaProduk ?: "-"
            tvHargaProduk.text = "Rp${produk.hargaJual ?: 0}"
            tvKategoriProduk.text = produk.kategori ?: "-"
            tvCabangProduk.text = produk.cabang ?: "-"

            // Logic: Status and Stock Management
            val status = produk.statusProduk ?: "ACTIVE"
            val currentStock = produk.stok ?: 0
            val isActive = status.equals("ACTIVE", ignoreCase = true) || 
                           status.equals("Aktif", ignoreCase = true)

            if (!isActive) {
                chipStatus.text = "Nonaktif"
                chipStatus.setChipIconResource(R.drawable.hapus)
                chipStatus.setChipIconTintResource(R.color.button2)
                chipStatus.setChipBackgroundColorResource(R.color.color5)
                chipStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.color6))
            } else if (currentStock == 0) {
                chipStatus.text = "Habis"
                chipStatus.setChipIconResource(R.drawable.stok)
                chipStatus.setChipIconTintResource(R.color.color6)
                chipStatus.setChipBackgroundColorResource(R.color.color5)
                chipStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.color6))
            } else {
                chipStatus.text = "Aktif"
                chipStatus.setChipIconResource(R.drawable.ic_check)
                chipStatus.setChipIconTintResource(R.color.color4)
                chipStatus.setChipBackgroundColorResource(R.color.layanan)
                chipStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.color2))
            }

            if (currentStock == -1) {
                tvStokProduk.text = "Stok: Tidak Terbatas"
            } else {
                tvStokProduk.text = "Stok: $currentStock"
            }

            // Standardized Glide Implementation
            Glide.with(itemView.context)
                .load(produk.productImageUrl)
                .placeholder(R.drawable.produk)
                .error(R.drawable.produk)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(ivLogo)

            itemView.setOnClickListener { onClick(produk) }
            itemView.setOnLongClickListener {
                onLongClick(produk)
                true
            }
        }
    }

    /**
     * DiffCallback ensures we only update the items that actually changed,
     * significantly improving RecyclerView performance.
     */
    object DiffCallback : DiffUtil.ItemCallback<ModelProdukActivity>() {
        override fun areItemsTheSame(oldItem: ModelProdukActivity, newItem: ModelProdukActivity): Boolean {
            return oldItem.idProduk == newItem.idProduk
        }

        override fun areContentsTheSame(oldItem: ModelProdukActivity, newItem: ModelProdukActivity): Boolean {
            return oldItem == newItem
        }
    }
}
