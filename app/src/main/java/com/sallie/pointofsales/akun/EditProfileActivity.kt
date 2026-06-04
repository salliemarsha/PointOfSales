package com.sallie.pointofsales.akun

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sallie.pointofsales.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

class EditProfileActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etName: TextInputEditText
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var spRole: MaterialAutoCompleteTextView
    private lateinit var btnSave: MaterialButton

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        initView()
        setupToolbar()
        setupDropdownRole()
        loadUserData()
        setupAction()
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbarEditProfile)
        etName = findViewById(R.id.etEditName)
        etUsername = findViewById(R.id.etEditUsername)
        etPhone = findViewById(R.id.etEditPhone)
        spRole = findViewById(R.id.spEditRole)
        btnSave = findViewById(R.id.btnSaveProfile)
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupDropdownRole() {
        val roles = listOf("Admin", "Kasir")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        spRole.setAdapter(adapter)
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            // Mode Admin (Logged in via Auth)
            etName.setText(user.displayName)
            etUsername.setText(user.email?.substringBefore("@"))
            
            database.getReference("users").child(user.uid).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    etPhone.setText(snapshot.child("phone").value?.toString())
                    val role = snapshot.child("role").value?.toString()
                    if (role != null) {
                        spRole.setText(role, false)
                    }
                }
            }
        } else {
            // Mode Kasir (Logged in via PIN/Session)
            val sharedPref = getSharedPreferences("KASIR_SESSION", Context.MODE_PRIVATE)
            val idKasir = sharedPref.getString("KASIR_ID", null)
            
            if (idKasir != null) {
                database.getReference("pegawai").child(idKasir).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val nama = snapshot.child("namaPegawai").value?.toString() ?: ""
                        val phone = snapshot.child("phonePegawai").value?.toString() ?: ""
                        val jabatan = snapshot.child("jabatan").value?.toString() ?: "Kasir"
                        
                        etName.setText(nama)
                        etPhone.setText(phone)
                        spRole.setText(jabatan, false)
                        etUsername.setText(nama.lowercase().replace(" ", ""))
                    }
                }
            }
        }
    }

    private fun setupAction() {
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val role = spRole.text.toString().trim()

            if (name.isEmpty() || username.isEmpty() || phone.isEmpty() || role.isEmpty()) {
                Toast.makeText(this, "Nama, No. Telp, dan Jabatan wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user != null) {
                // Update Admin Profile
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

                user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userData = mapOf(
                            "name" to name,
                            "username" to username,
                            "phone" to phone,
                            "role" to role
                        )
                        database.getReference("users").child(user.uid).updateChildren(userData).addOnSuccessListener {
                            Toast.makeText(this, "Profil Admin berhasil diperbarui", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            } else {
                // Update Kasir Profile
                val sharedPref = getSharedPreferences("KASIR_SESSION", Context.MODE_PRIVATE)
                val idKasir = sharedPref.getString("KASIR_ID", null)

                if (idKasir != null) {
                    val updateData = mapOf(
                        "namaPegawai" to name,
                        "phonePegawai" to phone,
                        "jabatan" to role
                    )
                    database.getReference("pegawai").child(idKasir).updateChildren(updateData).addOnSuccessListener {
                        // SINKRONISASI SESSION: Update Shared Preferences instan
                        sharedPref.edit().apply {
                            putString("KASIR_NAMA", name)
                            putString("KASIR_ROLE", role)
                            apply()
                        }
                        Toast.makeText(this, "Profil Kasir berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Gagal sinkronisasi data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
