package com.sallie.pointofsales.printer

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sallie.pointofsales.R

class PrintPreviewActivity : AppCompatActivity() {

    private lateinit var tvId: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvCash: TextView
    private lateinit var tvChange: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_print_preview)

        init()

        val id = intent.getStringExtra("id") ?: "-"
        val tanggal = intent.getStringExtra("tanggal") ?: "-"
        val total = intent.getIntExtra("total", 0)
        val bayar = intent.getIntExtra("bayar", 0)
        val kembalian = intent.getIntExtra("kembalian", 0)

        tvId.text = "ID: #$id"
        tvDate.text = tanggal
        tvTotal.text = "Rp$total"
        tvCash.text = "Rp$bayar"
        tvChange.text = "Rp$kembalian"

        findViewById<android.widget.Button>(R.id.btnCancel).setOnClickListener {
            finish()
        }

        findViewById<android.widget.Button>(R.id.btnPrint).setOnClickListener {
            // nanti kita sambungkan ke bluetooth printer
        }
    }

    private fun init() {
        tvId = findViewById(R.id.tvPreviewInvoiceId)
        tvDate = findViewById(R.id.tvPreviewInvoiceDate)
        tvTotal = findViewById(R.id.tvPreviewTotal)
        tvCash = findViewById(R.id.tvPreviewCash)
        tvChange = findViewById(R.id.tvPreviewChange)
    }
}