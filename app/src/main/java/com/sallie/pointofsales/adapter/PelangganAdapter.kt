package com.sallie.pointofsales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ModelPelanggan

class PelangganAdapter(
    private var listPelanggan: List<ModelPelanggan>,
    private val onEditClick: (ModelPelanggan) -> Unit,
    private val onDeleteClick: (ModelPelanggan) -> Unit
) : RecyclerView.Adapter<PelangganAdapter.PelangganViewHolder>() {

    fun updateData(newList: List<ModelPelanggan>) {
        listPelanggan = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PelangganViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pelanggan, parent, false)
        return PelangganViewHolder(view)
    }

    override fun onBindViewHolder(holder: PelangganViewHolder, position: Int) {
        val pelanggan = listPelanggan[position]
        holder.bind(pelanggan)
    }

    override fun getItemCount(): Int = listPelanggan.size

    inner class PelangganViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvInitials: TextView = itemView.findViewById(R.id.tvAvatarInitials)
        private val tvNama: TextView = itemView.findViewById(R.id.tvNamaPelanggan)
        private val tvTelepon: TextView = itemView.findViewById(R.id.tvNomorTelepon)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        private val tvAlamat: TextView = itemView.findViewById(R.id.tvAlamat)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(pelanggan: ModelPelanggan) {
            tvNama.text = pelanggan.namaPelanggan ?: "-"
            tvTelepon.text = pelanggan.nomorTelepon ?: "-"
            tvEmail.text = if (pelanggan.email.isNullOrBlank()) "Tidak ada email" else pelanggan.email
            tvAlamat.text = if (pelanggan.alamat.isNullOrBlank()) "Tidak ada alamat" else pelanggan.alamat

            // Set Initials
            val name = pelanggan.namaPelanggan ?: "P"
            val initials = name.split(" ")
                .filter { it.isNotEmpty() }
                .map { it[0] }
                .take(2)
                .joinToString("")
                .uppercase()
            tvInitials.text = initials

            btnEdit.setOnClickListener { onEditClick(pelanggan) }
            btnDelete.setOnClickListener { onDeleteClick(pelanggan) }
        }
    }
}
