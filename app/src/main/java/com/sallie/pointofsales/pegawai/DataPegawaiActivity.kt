package com.sallie.pointofsales.pegawai

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.sallie.pointofsales.R
import com.sallie.pointofsales.adapter.DetailPegawaiAdapter
import com.sallie.pointofsales.model.ModelPegawaiActivity
import com.sallie.pointofsales.viewmodel.DataPegawaiViewModel

class DataPegawaiActivity : AppCompatActivity() {

    private val viewModel: DataPegawaiViewModel by viewModels()
    private lateinit var rvDataPegawai: RecyclerView
    private lateinit var fabAddEmployee: FloatingActionButton
    private lateinit var etSearchEmployee: TextInputEditText
    private lateinit var adapter: DetailPegawaiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_pegawai)

        init()

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        fabAddEmployee.setOnClickListener {
            val intent = Intent(this, ModPegawaiActivity::class.java)
            startActivity(intent)
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        rvDataPegawai.layoutManager = layoutManager
        rvDataPegawai.setHasFixedSize(true)

        adapter = DetailPegawaiAdapter(emptyList())
        rvDataPegawai.adapter = adapter

        adapter.setOnItemClickListener(object : DetailPegawaiAdapter.OnItemClickListener {
            override fun onItemClick(pegawai: ModelPegawaiActivity) {
                if (!pegawai.idPegawai.isNullOrBlank()) {
                    showPegawaiDetail(pegawai)
                } else {
                    Toast.makeText(
                        this@DataPegawaiActivity,
                        "Data tidak valid",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        viewModel.pegawaiList.observe(this) { list ->
            adapter.updateData(list)
            if (list.isNotEmpty()) {
                rvDataPegawai.scrollToPosition(list.size - 1)
            }
        }

        etSearchEmployee.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showPegawaiDetail(pegawai: ModelPegawaiActivity) {
        val intent = Intent(this, ModPegawaiActivity::class.java).apply {
            putExtra("ID_PEGAWAI", pegawai.idPegawai)
            putExtra("NAMA_PEGAWAI", pegawai.namaPegawai)
            putExtra("PHONE_PEGAWAI", pegawai.phonePegawai)
            putExtra("ROLE_PEGAWAI", pegawai.rolePegawai)
            putExtra("PIN_PEGAWAI", pegawai.pinPegawai)
        }
        startActivity(intent)
    }

    private fun init() {
        rvDataPegawai = findViewById(R.id.rvDataPegawai)
        fabAddEmployee = findViewById(R.id.fabAddEmployee)
        etSearchEmployee = findViewById(R.id.etSearchEmployee)
    }
}