package com.sallie.pointofsales

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sallie.pointofsales.cabang.DataCabangActivity
import com.sallie.pointofsales.kategori.DataKategoriActivity
import com.sallie.pointofsales.pegawai.DataPegawaiActivity
import com.sallie.pointofsales.produk.DataProductActivity
import com.sallie.pointofsales.transaksi.TransaksiActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var cvAccount: CardView
    private lateinit var cvProduct: CardView
    private lateinit var cvCategory: CardView
    private lateinit var cvEmployee: CardView
    private lateinit var cvBranch: CardView
    private lateinit var cvPrint: CardView
    private lateinit var llTransaksi: LinearLayout
    private lateinit var llLaporan: LinearLayout
    private lateinit var tvDate: TextView
    private lateinit var tvGreetings: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        init()

        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
        tvDate.text = currentDate

        tvGreetings.text = getGreeting()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..11 -> "Selamat Pagi, Sallie"
            in 12..14 -> "Selamat Siang, Sallie"
            in 15..17 -> "Selamat Sore, Sallie"
            else -> "Selamat Malam, Sallie"
        }
    }

    private fun init() {
        cvAccount = findViewById(R.id.cvAccount)
        cvProduct = findViewById(R.id.cvProduct)
        cvCategory = findViewById(R.id.cvCategory)
        cvEmployee = findViewById(R.id.cvEmployee)
        cvBranch = findViewById(R.id.cvBranch)
        cvPrint = findViewById(R.id.cvPrint)
        llTransaksi = findViewById(R.id.llTransaksi)
        llLaporan = findViewById(R.id.llLaporan)
        tvDate = findViewById(R.id.tvDate)
        tvGreetings = findViewById(R.id.tvGreetings)

        cvCategory.setOnClickListener {
            startActivity(Intent(this, DataKategoriActivity::class.java))
        }

        cvProduct.setOnClickListener {
            startActivity(Intent(this, DataProductActivity::class.java))
        }

        cvEmployee.setOnClickListener {
            startActivity(Intent(this, DataPegawaiActivity::class.java))
        }

        cvBranch.setOnClickListener {
            startActivity(Intent(this, DataCabangActivity::class.java))
        }

        llTransaksi.setOnClickListener {
            startActivity(Intent(this, TransaksiActivity::class.java))
        }

        llLaporan.setOnClickListener {
        }

        cvAccount.setOnClickListener {
        }

        cvPrint.setOnClickListener {
        }
    }
}