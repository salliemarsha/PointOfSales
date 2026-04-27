package com.sallie.pointofsales

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import com.sallie.pointofsales.kategori.DataKategoriActivity
import com.sallie.pointofsales.produk.DataProductActivity

class MainActivity : AppCompatActivity() {

    private lateinit var cvAccount: CardView
    private lateinit var cvProduct: CardView
    private lateinit var cvCategory: CardView
    private lateinit var cvEmployee: CardView
    private lateinit var cvBranch: CardView
    private lateinit var cvPrint: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        init()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvDate = findViewById<TextView>(R.id.tvDate)

        val currentDate = java.text.SimpleDateFormat(
            "dd MMMM yyyy",
            java.util.Locale("id", "ID")
        ).format(java.util.Date())

        tvDate.text = currentDate

    }

    fun getGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 0..11 -> "Selamat Pagi"
            in 12..14 -> "Selamat Siang"
            in 15..17 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    private fun init() {
        cvAccount = findViewById(R.id.cvAccount)
        cvProduct = findViewById(R.id.cvProduct)
        cvCategory = findViewById(R.id.cvCategory)
        cvEmployee = findViewById(R.id.cvEmployee)
        cvBranch = findViewById(R.id.cvBranch)
        cvPrint = findViewById(R.id.cvPrint)

        cvCategory.setOnClickListener {
            startActivity(Intent(this, DataKategoriActivity::class.java))
        }

        cvAccount.setOnClickListener {

        }

        cvProduct.setOnClickListener {
            startActivity(Intent(this, DataProductActivity::class.java))
        }

        cvEmployee.setOnClickListener {

        }

        cvBranch.setOnClickListener {

        }

        cvPrint.setOnClickListener {

        }
    }
}