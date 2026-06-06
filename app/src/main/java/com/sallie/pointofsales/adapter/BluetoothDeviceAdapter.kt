package com.sallie.pointofsales.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.PrinterDevice

class BluetoothDeviceAdapter(
    private val list: ArrayList<PrinterDevice>,
    private val onClick: (PrinterDevice) -> Unit
) : RecyclerView.Adapter<BluetoothDeviceAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvDeviceName)
        val address: TextView = itemView.findViewById(R.id.tvDeviceAddress)
        val status: TextView = itemView.findViewById(R.id.tvDeviceStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bluetooth_device, parent, false)
        return VH(view)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val printer = list[position]
        val device = printer.device

        holder.name.text = device.name ?: "Unknown Device"
        holder.address.text = device.address
        holder.status.text = printer.status

        // Change status color based on status text
        when (printer.status) {
            "Connected" -> holder.status.setTextColor(holder.itemView.context.getColor(R.color.color4))
            "Connecting..." -> holder.status.setTextColor(holder.itemView.context.getColor(R.color.color6))
            "Failed" -> holder.status.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
            else -> holder.status.setTextColor(holder.itemView.context.getColor(R.color.color6))
        }

        holder.itemView.setOnClickListener {
            onClick(printer)
        }
    }

    override fun getItemCount(): Int = list.size
}
