package com.sallie.pointofsales.printer

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.sallie.pointofsales.model.ItemKeranjang
import com.sallie.pointofsales.model.ModelTransaksi

class PrinterService(private val context: Context) {

    /**
     * Data structure for receipt
     */
    data class ReceiptData(
        val idTransaksi: String,
        val tanggal: String,
        val storeName: String = "NexPOS",
        val storeAddress: String = "Jl. Raya Kelontong No. 123",
        val items: List<ItemKeranjang>,
        val totalBayar: String,
        val uangBayar: String,
        val kembalian: String
    )

    fun printTransaction(transaksi: ModelTransaksi) {
        val data = mapToReceiptData(transaksi)
        printReceipt(data)
    }

    fun shareTransaction(transaksi: ModelTransaksi) {
        val data = mapToReceiptData(transaksi)
        val receiptText = buildReceiptString(data)
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Receipt #${data.idTransaksi.takeLast(8)}")
            putExtra(Intent.EXTRA_TEXT, receiptText)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Receipt Via"))
    }

    private fun mapToReceiptData(transaksi: ModelTransaksi): ReceiptData {
        return ReceiptData(
            idTransaksi = transaksi.idTransaksi,
            tanggal = transaksi.tanggal,
            storeName = transaksi.namaCabang.ifEmpty { "NexPOS Store" },
            items = transaksi.itemTerjual,
            totalBayar = transaksi.totalBayar.toString(),
            uangBayar = transaksi.uangBayar.toString(),
            kembalian = transaksi.kembalian.toString()
        )
    }

    fun printReceipt(data: ReceiptData) {
        val sharedPrefs = context.getSharedPreferences("printer_prefs", Context.MODE_PRIVATE)
        val printerAddress = sharedPrefs.getString("last_printer_address", null)

        if (printerAddress == null) {
            Toast.makeText(context, "Printer not connected. Please go to Printer Settings.", Toast.LENGTH_LONG).show()
            // Even if not connected, we show the preview/mock
        }

        val receiptText = buildReceiptString(data)
        
        Toast.makeText(context, "Printing receipt #${data.idTransaksi.takeLast(6)}...", Toast.LENGTH_SHORT).show()
        
        // Mock Bluetooth output / Logs
        println("BLUETOOTH PRINT:\n$receiptText")
    }

    fun buildReceiptString(data: ReceiptData): String {
        val sb = StringBuilder()
        sb.append("================================\n")
        sb.append("          ${data.storeName.uppercase()}          \n")
        sb.append("    ${data.storeAddress}    \n")
        sb.append("================================\n")
        sb.append("ID: ${data.idTransaksi}\n")
        sb.append("Tgl: ${data.tanggal}\n")
        sb.append("--------------------------------\n")
        
        data.items.forEach { item ->
            val subTotal = item.hargaJual * item.jumlahBeli
            sb.append("${item.namaProduk}\n")
            sb.append("${item.jumlahBeli} x ${item.hargaJual} \t\t${subTotal}\n")
        }
        
        sb.append("--------------------------------\n")
        sb.append("TOTAL: \t\t\tRp${data.totalBayar}\n")
        sb.append("BAYAR: \t\t\tRp${data.uangBayar}\n")
        sb.append("KEMBALI: \t\tRp${data.kembalian}\n")
        sb.append("================================\n")
        sb.append("   Terima Kasih Telah Berbelanja   \n")
        sb.append("================================\n")
        
        return sb.toString()
    }
}
