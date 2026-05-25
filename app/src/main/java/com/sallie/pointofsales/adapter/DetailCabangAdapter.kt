package com.sallie.pointofsales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ModelCabangActivity

class DetailCabangAdapter(private var cabangList: List<ModelCabangActivity>) :
    RecyclerView.Adapter<DetailCabangAdapter.CabangViewHolder>() {

    lateinit var appContext: Context

    interface OnItemClickListener {
        fun onItemClick(cabang: ModelCabangActivity)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun updateData(newList: List<ModelCabangActivity>) {
        cabangList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CabangViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_data_cabang, parent, false)
        appContext = parent.context
        return CabangViewHolder(view)
    }

    override fun onBindViewHolder(holder: CabangViewHolder, position: Int) {
        val cabang = cabangList[position]
        holder.bind(cabang)
    }

    override fun getItemCount(): Int {
        return cabangList.size
    }

    inner class CabangViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvNamaCabang: TextView = itemView.findViewById(R.id.tvNamaCabang)
        val tvLokasiCabang: TextView = itemView.findViewById(R.id.tvLokasiCabang)

        fun bind(cabang: ModelCabangActivity) {
            tvNamaCabang.text = cabang.namaCabang ?: "-"
            tvLokasiCabang.text = cabang.lokasiCabang ?: "-"

            itemView.setOnClickListener {
                listener?.onItemClick(cabang)
            }
        }
    }
}