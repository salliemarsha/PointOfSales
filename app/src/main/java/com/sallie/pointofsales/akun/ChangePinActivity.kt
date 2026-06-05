package com.sallie.pointofsales.akun

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sallie.pointofsales.R

class ChangePinActivity : AppCompatActivity() {

    private lateinit var etOldPin: TextInputEditText
    private lateinit var etNewPin: TextInputEditText
    private lateinit var etConfirmPin: TextInputEditText
    private lateinit var btnSavePin: MaterialButton

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pin)

        initView()
        setupAction()
    }

    private fun initView() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarChangePin)
        etOldPin = findViewById(R.id.etOldPin)
        etNewPin = findViewById(R.id.etNewPin)
        etConfirmPin = findViewById(R.id.etConfirmPin)
        btnSavePin = findViewById(R.id.btnSavePin)

        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupAction() {
        btnSavePin.setOnClickListener {
            val oldPin = etOldPin.text.toString().trim()
            val newPin = etNewPin.text.toString().trim()
            val confirmPin = etConfirmPin.text.toString().trim()

            if (oldPin.isEmpty() || newPin.isEmpty() || confirmPin.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPin.length != 6) {
                Toast.makeText(this, "PIN baru harus 6 digit", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPin != confirmPin) {
                Toast.makeText(this, "Konfirmasi PIN tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            processChangePin(oldPin, newPin)
        }
    }

    private fun processChangePin(oldPin: String, newPin: String) {
        val user = auth.currentUser
        if (user != null) {
            // ROLE: ADMIN (Update di node 'users' field 'pin')
            val ref = database.getReference("users").child(user.uid)
            ref.child("pin").get().addOnSuccessListener { snapshot ->
                val savedPin = snapshot.value?.toString() ?: ""
                if (savedPin.isEmpty() || savedPin == oldPin) {
                    ref.child("pin").setValue(newPin).addOnSuccessListener {
                        showSuccessAndFinish()
                    }
                } else {
                    Toast.makeText(this, "PIN lama Admin salah", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // ROLE: KASIR (Update di node 'pegawai' field 'pinPegawai')
            val sharedPref = getSharedPreferences("KASIR_SESSION", Context.MODE_PRIVATE)
            val idKasir = sharedPref.getString("KASIR_ID", "") ?: ""

            if (idKasir.isNotEmpty()) {
                val ref = database.getReference("pegawai").child(idKasir)
                ref.child("pinPegawai").get().addOnSuccessListener { snapshot ->
                    val savedPin = snapshot.value?.toString() ?: ""
                    if (savedPin == oldPin) {
                        ref.child("pinPegawai").setValue(newPin).addOnSuccessListener {
                            showSuccessAndFinish()
                        }
                    } else {
                        Toast.makeText(this, "PIN lama Kasir salah", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Sesi tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSuccessAndFinish() {
        Toast.makeText(this, "PIN Keamanan Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
        finish()
    }
}
