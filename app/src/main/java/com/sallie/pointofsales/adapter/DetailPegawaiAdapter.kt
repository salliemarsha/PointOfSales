package com.sallie.pointofsales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ModelPegawaiActivity

class DetailPegawaiAdapter(private var pegawaiList: List<ModelPegawaiActivity>) :
    RecyclerView.Adapter<DetailPegawaiAdapter.PegawaiViewHolder>() {

    lateinit var appContext: Context

    interface OnItemClickListener {
        fun onItemClick(pegawai: ModelPegawaiActivity)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun updateData(newList: List<ModelPegawaiActivity>) {
        pegawaiList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PegawaiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_data_pegawai, parent, false)
        appContext = parent.context
        return PegawaiViewHolder(view)
    }

    override fun onBindViewHolder(holder: PegawaiViewHolder, position: Int) {
        val pegawai = pegawaiList[position]
        holder.bind(pegawai)
    }

    override fun getItemCount(): Int {
        return pegawaiList.size
    }

    inner class PegawaiViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvNamaPegawai: TextView = itemView.findViewById(R.id.tvNamaPegawai)
        val tvPhonePegawai: TextView = itemView.findViewById(R.id.tvPhoneNumber)
        val chipRole: Chip = itemView.findViewById(R.id.chipStatus)

        fun bind(pegawai: ModelPegawaiActivity) {
            tvNamaPegawai.text = pegawai.namaPegawai ?: "-"
            tvPhonePegawai.text = pegawai.phonePegawai ?: "-"
            chipRole.text = pegawai.jabatan ?: "-"

            itemView.setOnClickListener {
                listener?.onItemClick(pegawai)
            }
        }
    }
}