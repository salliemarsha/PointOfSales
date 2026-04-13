package com.sallie.pointofsales.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import com.sallie.pointofsales.R
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.sallie.pointofsales.model.ModelKategoriActivity

class DetailKategoriAdapter (private val kategoriList: List<ModelKategoriActivity>):
    RecyclerView.Adapter<DetailKategoriAdapter.KategoriViewHolder>() {
        lateinit var appContext: Context

    interface OnItemClickListener {
        fun onItemClick(kategori: ModelKategoriActivity)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): KategoriViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_data_kategori, parent, false)
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

    inner class KategoriViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {

                val tvNamaKategori: TextView = itemView.findViewById(R.id.tvNamaKategori)
                val chipStatus: Chip = itemView.findViewById(R.id.chipStatus)

        fun bind(kategori: ModelKategoriActivity) {
            tvNamaKategori.text = kategori.namaKategori ?: "-"
            chipStatus.text = kategori.statusKategori ?: "-"

            itemView.setOnClickListener {
                listener?.onItemClick(kategori)
            }
        }
            }

}