package com.sallie.pointofsales.akun

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sallie.pointofsales.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChangePinActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etOldPin: TextInputEditText
    private lateinit var etNewPin: TextInputEditText
    private lateinit var etConfirmPin: TextInputEditText
    private lateinit var btnSavePin: MaterialButton

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pin)

        initView()
        setupToolbar()
        setupAction()
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbarChangePin)
        etOldPin = findViewById(R.id.etOldPin)
        etNewPin = findViewById(R.id.etNewPin)
        etConfirmPin = findViewById(R.id.etConfirmPin)
        btnSavePin = findViewById(R.id.btnSavePin)
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupAction() {
        btnSavePin.setOnClickListener {
            val oldPin = etOldPin.text.toString().trim()
            val newPin = etNewPin.text.toString().trim()
            val confirmPin = etConfirmPin.text.toString().trim()

            if (oldPin.isEmpty() || newPin.isEmpty() || confirmPin.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPin.length != 6) {
                Toast.makeText(this, "PIN baru harus 6 digit", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPin != confirmPin) {
                Toast.makeText(this, "PIN baru tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user != null) {
                // Verify old PIN first
                database.child(user.uid).child("pin").get().addOnSuccessListener { snapshot ->
                    val savedPin = snapshot.value?.toString()
                    
                    // If no PIN is set yet, we allow setting a new one without matching old one, 
                    // or if it matches the input.
                    if (savedPin == null || savedPin == oldPin) {
                        database.child(user.uid).child("pin").setValue(newPin)
                            .addOnSuccessListener {
                                Toast.makeText(this, "PIN berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Gagal memperbarui PIN", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "PIN lama salah", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Gagal memvalidasi PIN", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
