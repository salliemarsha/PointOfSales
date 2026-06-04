package com.sallie.pointofsales.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ModelPegawaiActivity
import com.sallie.pointofsales.transaksi.KasirDashboardActivity

class LoginKasirActivity : AppCompatActivity() {

    private lateinit var spPilihKasir: AutoCompleteTextView
    private lateinit var layoutDots: LinearLayout
    private val database = FirebaseDatabase.getInstance()
    private val pegawaiRef = database.getReference("pegawai")

    private val listPegawai = ArrayList<ModelPegawaiActivity>()
    private val listNamaPegawai = ArrayList<String>()
    private var selectedPegawai: ModelPegawaiActivity? = null
    private var inputPin = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_kasir)

        initViews()
        loadPegawai()
        setupNumpad()
    }

    private fun initViews() {
        spPilihKasir = findViewById(R.id.spPilihKasir)
        layoutDots = findViewById(R.id.layoutDots)

        spPilihKasir.setOnItemClickListener { _, _, position, _ ->
            if (position < listPegawai.size) {
                selectedPegawai = listPegawai[position]
                inputPin = ""
                updateDots()
            }
        }
    }

    private fun loadPegawai() {
        // FILTER: Hanya ambil data pegawai dengan role 'Kasir'
        pegawaiRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) return
                
                listPegawai.clear()
                listNamaPegawai.clear()
                for (ds in snapshot.children) {
                    try {
                        val p = ds.getValue(ModelPegawaiActivity::class.java)
                        p?.let {
                            val role = it.jabatan ?: ""
                            // FILTER KETAT: Admin tidak boleh muncul di list login kasir
                            if (role.equals("Kasir", true)) {
                                listPegawai.add(it)
                                listNamaPegawai.add(it.namaPegawai ?: "Tanpa Nama")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                val adapter = ArrayAdapter(this@LoginKasirActivity, android.R.layout.simple_dropdown_item_1line, listNamaPegawai)
                spPilihKasir.setAdapter(adapter)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginKasirActivity, "Error Database: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupNumpad() {
        val buttons = arrayOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        for (id in buttons) {
            findViewById<Button>(id)?.setOnClickListener {
                if (selectedPegawai == null) {
                    Toast.makeText(this, "Pilih nama pegawai terlebih dahulu", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (inputPin.length < 6) {
                    inputPin += (it as Button).text.toString()
                    updateDots()
                    if (inputPin.length == 6) {
                        checkPin()
                    }
                }
            }
        }

        findViewById<ImageButton>(R.id.btnDelete)?.setOnClickListener {
            if (inputPin.isNotEmpty()) {
                inputPin = inputPin.dropLast(1)
                updateDots()
            }
        }
    }

    private fun updateDots() {
        val dotCount = layoutDots.childCount
        for (i in 0 until dotCount) {
            val dot = layoutDots.getChildAt(i)
            if (i < inputPin.length) {
                dot?.setBackgroundResource(R.drawable.bg_dot_on)
            } else {
                dot?.setBackgroundResource(R.drawable.bg_dot_off)
            }
        }
    }

    private fun checkPin() {
        val pegawai = selectedPegawai ?: return
        
        // VALIDASI ROLE: Proteksi jika data admin masuk ke tabel pegawai
        val role = pegawai.jabatan ?: ""
        if (!role.equals("Kasir", true)) {
            Toast.makeText(this, "Akses Ditolak: Admin tidak bisa login sebagai Kasir!", Toast.LENGTH_LONG).show()
            inputPin = ""
            updateDots()
            return
        }

        val correctPin = pegawai.pinPegawai ?: ""
        if (correctPin.isNotEmpty() && correctPin == inputPin) {
            saveSessionAndNavigate(pegawai)
        } else {
            Toast.makeText(this, "PIN Salah!", Toast.LENGTH_SHORT).show()
            inputPin = ""
            updateDots()
        }
    }

    private fun saveSessionAndNavigate(pegawai: ModelPegawaiActivity) {
        try {
            val sharedPref = getSharedPreferences("KASIR_SESSION", Context.MODE_PRIVATE)
            sharedPref.edit().apply {
                putString("KASIR_ID", pegawai.idPegawai ?: "unknown_id")
                putString("KASIR_NAMA", pegawai.namaPegawai ?: "Kasir")
                putString("KASIR_ROLE", "Kasir")
                apply()
            }

            val intent = Intent(this, KasirDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal memproses login: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}
