package com.sallie.pointofsales.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sallie.pointofsales.R
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.sallie.pointofsales.model.ModelKategoriActivity

class DetailKategoriAdapter(private var kategoriList: List<ModelKategoriActivity>) :
    RecyclerView.Adapter<DetailKategoriAdapter.KategoriViewHolder>() {
    lateinit var appContext: Context

    interface OnItemClickListener {
        fun onItemClick(kategori: ModelKategoriActivity)
        fun onItemLongClick(kategori: ModelKategoriActivity)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): KategoriViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_data_kategori, parent, false)
        appContext = parent.context
        return KategoriViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: DetailKategoriAdapter.KategoriViewHolder,
        position: Int
    ) {
        val kategori = kategoriList[position]
        holder.bind(kategori)
    }

    override fun getItemCount(): Int {
        return kategoriList.size
    }

    fun updateData(newList: List<ModelKategoriActivity>) {
        kategoriList = newList
        notifyDataSetChanged()
    }

    inner class KategoriViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvNamaKategori: TextView = itemView.findViewById(R.id.tvNamaKategori)
        val chipStatus: Chip = itemView.findViewById(R.id.chipStatus)

        fun bind(kategori: ModelKategoriActivity) {
            tvNamaKategori.text = kategori.namaKategori ?: "-"
            
            val status = kategori.statusKategori ?: "ACTIVE"
            val isActive = status.equals("ACTIVE", ignoreCase = true) || 
                           status.equals("Aktif", ignoreCase = true)
            
            if (isActive) {
                chipStatus.text = "Aktif"
                chipStatus.setChipIconResource(R.drawable.ic_check)
                chipStatus.setChipIconTintResource(R.color.color4)
                chipStatus.setChipBackgroundColorResource(R.color.layanan) // Soft Green tint
                chipStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.color2))
            } else {
                chipStatus.text = "Nonaktif"
                chipStatus.setChipIconResource(R.drawable.hapus)
                chipStatus.setChipIconTintResource(R.color.button2)
                chipStatus.setChipBackgroundColorResource(R.color.color5) // Light Gray
                chipStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.color6))
            }

            itemView.setOnClickListener {
                listener?.onItemClick(kategori)
            }

            itemView.setOnLongClickListener {
                listener?.onItemLongClick(kategori)
                true
            }
        }
    }
}
