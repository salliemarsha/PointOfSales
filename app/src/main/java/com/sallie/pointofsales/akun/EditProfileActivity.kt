package com.sallie.pointofsales.akun

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
    private val database = FirebaseDatabase.getInstance().getReference("users")

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
        val roles = listOf(
            "Admin",
            "Kasir"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        spRole.setAdapter(adapter)
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            etName.setText(user.displayName)
            etUsername.setText(user.email?.substringBefore("@"))
            
            // Load additional data from Realtime Database
            database.child(user.uid).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    etPhone.setText(snapshot.child("phone").value?.toString())
                    val role = snapshot.child("role").value?.toString()
                    if (role != null) {
                        spRole.setText(role, false)
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
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (phone.length < 10) {
                Toast.makeText(this, "Nomor telepon tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user != null) {
                // Update Firebase Auth Profile
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

                user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Update Realtime Database
                        val userData = mapOf(
                            "name" to name,
                            "username" to username,
                            "phone" to phone,
                            "role" to role
                        )
                        database.child(user.uid).setValue(userData).addOnSuccessListener {
                            Toast.makeText(this, "Profil berhasil disimpan", Toast.LENGTH_SHORT).show()
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Gagal menyimpan ke database", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Gagal memperbarui profil auth", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
