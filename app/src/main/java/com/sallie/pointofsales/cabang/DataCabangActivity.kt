package com.sallie.pointofsales.cabang

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
import com.sallie.pointofsales.adapter.DetailCabangAdapter
import com.sallie.pointofsales.model.ModelCabangActivity
import com.sallie.pointofsales.viewmodel.DataCabangViewModel

class DataCabangActivity : AppCompatActivity() {

    private val viewModel: DataCabangViewModel by viewModels()
    private lateinit var rvDataCabang: RecyclerView
    private lateinit var fabAddBranch: FloatingActionButton
    private lateinit var etSearchBranch: TextInputEditText
    private lateinit var adapter: DetailCabangAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_cabang)

        init()

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        fabAddBranch.setOnClickListener {
            startActivity(Intent(this, ModCabangActivity::class.java))
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        rvDataCabang.layoutManager = layoutManager
        rvDataCabang.setHasFixedSize(true)

        adapter = DetailCabangAdapter(emptyList())
        rvDataCabang.adapter = adapter

        adapter.setOnItemClickListener(object : DetailCabangAdapter.OnItemClickListener {
            override fun onItemClick(cabang: ModelCabangActivity) {
                if (!cabang.idCabang.isNullOrBlank()) {
                    val intent = Intent(this@DataCabangActivity, ModCabangActivity::class.java).apply {
                        putExtra("ID_CABANG", cabang.idCabang)
                        putExtra("NAMA_CABANG", cabang.namaCabang)
                        putExtra("LOKASI_CABANG", cabang.lokasiCabang)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this@DataCabangActivity, "Data tidak valid", Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.cabangList.observe(this) { list ->
            adapter.updateData(list)
            if (list.isNotEmpty()) {
                rvDataCabang.scrollToPosition(list.size - 1)
            }
        }

        etSearchBranch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun init() {
        rvDataCabang = findViewById(R.id.rvDataCabang)
        fabAddBranch = findViewById(R.id.fabAddBranch)
        etSearchBranch = findViewById(R.id.etSearchBranch)
    }
}