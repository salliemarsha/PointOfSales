package com.sallie.pointofsales.pegawai

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.sallie.pointofsales.R
import com.sallie.pointofsales.model.ModelPegawaiActivity

class ModPegawaiActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pegawai")

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvToolbarTitle: TextView
    private lateinit var etNameEmployee: TextInputEditText
    private lateinit var etPhoneEmployee: TextInputEditText
    private lateinit var spRoleEmployee: AutoCompleteTextView
    private lateinit var etPinEmployee: TextInputEditText
    private lateinit var btnSaveEmployee: Button
    private lateinit var btnDeleteEmployee: Button

    private var idPegawai: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mod_pegawai)

        init()

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val roleOptions = resources.getStringArray(R.array.role_pegawai)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, roleOptions)
        spRoleEmployee.setAdapter(adapter)

        cekModeIntent()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.modPegawai)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun init() {
        toolbar = findViewById(R.id.toolbar)
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle)
        etNameEmployee = findViewById(R.id.etNameEmployee)
        etPhoneEmployee = findViewById(R.id.etPhoneEmployee)
        spRoleEmployee = findViewById(R.id.spRoleEmployee)
        etPinEmployee = findViewById(R.id.etPinEmployee)
        btnSaveEmployee = findViewById(R.id.btnSaveEmployee)
        btnDeleteEmployee = findViewById(R.id.btnDeleteEmployee)

        btnSaveEmployee.setOnClickListener {
            cekValidasi()
        }

        btnDeleteEmployee.setOnClickListener {
            hapus()
        }
    }

    private fun cekModeIntent() {
        idPegawai = intent.getStringExtra("ID_PEGAWAI")
        val nama = intent.getStringExtra("NAMA_PEGAWAI")
        val phone = intent.getStringExtra("PHONE_PEGAWAI")
        val role = intent.getStringExtra("ROLE_PEGAWAI")
        val pin = intent.getStringExtra("PIN_PEGAWAI")

        if (idPegawai != null) {
            tvToolbarTitle.text = getString(R.string.edit_pegawai)
            btnSaveEmployee.text = getString(R.string.simpan)
            btnDeleteEmployee.visibility = View.VISIBLE

            etNameEmployee.setText(nama)
            etPhoneEmployee.setText(phone)
            spRoleEmployee.setText(role, false)
            etPinEmployee.setText(pin)
        } else {
            tvToolbarTitle.text = getString(R.string.tmbh_pegawai)
            btnSaveEmployee.text = getString(R.string.simpan)
            btnDeleteEmployee.visibility = View.GONE
        }
    }

    private fun cekValidasi() {
        val nama = etNameEmployee.text.toString().trim()
        val phone = etPhoneEmployee.text.toString().trim()
        val role = spRoleEmployee.text.toString().trim()
        val pin = etPinEmployee.text.toString().trim()

        if (nama.isEmpty()) {
            etNameEmployee.error = "Nama Lengkap Tidak Boleh Kosong"
            etNameEmployee.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            etPhoneEmployee.error = "Nomor Telepon Tidak Boleh Kosong"
            etPhoneEmployee.requestFocus()
            return
        }

        if (role.isEmpty()) {
            spRoleEmployee.error = "Jabatan Tidak Boleh Kosong"
            spRoleEmployee.requestFocus()
            return
        }

        if (pin.isEmpty()) {
            etPinEmployee.error = "PIN Tidak Boleh Kosong"
            etPinEmployee.requestFocus()
            return
        }

        if (pin.length < 6) {
            etPinEmployee.error = "PIN Harus 6 Digit"
            etPinEmployee.requestFocus()
            return
        }

        simpan()
    }

    private fun simpan() {
        val id = idPegawai ?: myRef.push().key

        if (id == null) {
            Toast.makeText(this, "ID gagal dibuat", Toast.LENGTH_SHORT).show()
            return
        }

        val pegawai = ModelPegawaiActivity(
            idPegawai = id,
            namaPegawai = etNameEmployee.text.toString().trim(),
            phonePegawai = etPhoneEmployee.text.toString().trim(),
            jabatan = spRoleEmployee.text.toString().trim(),
            pinPegawai = etPinEmployee.text.toString().trim()
        )

        myRef.child(id).setValue(pegawai)
            .addOnSuccessListener {
                val pesan = if (idPegawai != null) "Pegawai berhasil diperbarui" else "Pegawai berhasil ditambahkan"
                Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan data pegawai", Toast.LENGTH_SHORT).show()
            }
    }

    private fun hapus() {
        idPegawai?.let { id ->
            myRef.child(id).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Pegawai berhasil dihapus", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal menghapus pegawai", Toast.LENGTH_SHORT).show()
                }
        }
    }
}