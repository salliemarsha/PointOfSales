package com.sallie.pointofsales.akun

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sallie.pointofsales.R
import com.sallie.pointofsales.auth.LoginActivity

class AccountActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvAccountName: TextView
    private lateinit var tvAvatarInitials: TextView
    private lateinit var tvUserRole: TextView
    private lateinit var cvChangePin: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        auth = FirebaseAuth.getInstance()
        initViews()
    }

    private fun initViews() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarAccount)
        tvAccountName = findViewById(R.id.tvAccountName)
        tvAvatarInitials = findViewById(R.id.tvAvatarInitials)
        tvUserRole = findViewById(R.id.tvAccountRole)
        cvChangePin = findViewById(R.id.cvChangePin)

        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener { logout() }
        
        findViewById<MaterialCardView>(R.id.cvEditProfile).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        // Enable Change PIN for Admin
        cvChangePin.setOnClickListener {
            startActivity(Intent(this, ChangePinActivity::class.java))
        }

        toolbar.setNavigationOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        loadProfileData() // Selalu refresh data terbaru saat kembali ke layar ini
    }

    private fun loadProfileData() {
        val user = auth.currentUser
        
        if (user != null) {
            // LOGIKA ADMIN (Firebase Auth)
            // Show Change PIN button for Admin
            cvChangePin.visibility = View.VISIBLE
            
            val uid = user.uid
            FirebaseDatabase.getInstance().getReference("users").child(uid).get()
                .addOnSuccessListener { snapshot ->
                    val name = snapshot.child("name").value?.toString() ?: user.displayName ?: "Admin"
                    val role = snapshot.child("role").value?.toString() ?: "Admin"
                    updateUI(name, role)
                }
        } else {
            // LOGIKA KASIR (SharedPreferences + Realtime DB)
            // Hide Change PIN button for Cashier
            cvChangePin.visibility = View.GONE

            val sharedPref = getSharedPreferences("KASIR_SESSION", Context.MODE_PRIVATE)
            val idKasir = sharedPref.getString("KASIR_ID", "") ?: ""
            val localName = sharedPref.getString("KASIR_NAMA", "Kasir") ?: "Kasir"
            
            // Set data lokal dulu agar cepat
            updateUI(localName, "Kasir")

            // Ambil data terbaru dari DB jika ID tersedia (sinkronisasi real-time)
            if (idKasir.isNotEmpty()) {
                FirebaseDatabase.getInstance().getReference("pegawai").child(idKasir).get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            val dbName = snapshot.child("namaPegawai").value?.toString() ?: localName
                            updateUI(dbName, "Kasir")
                        }
                    }
            }
        }
    }

    private fun updateUI(name: String, role: String) {
        tvAccountName.text = name
        tvUserRole.text = role
        tvAvatarInitials.text = generateInitials(name)
    }

    private fun generateInitials(name: String): String {
        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
            parts.isNotEmpty() -> "${parts[0].first()}".uppercase()
            else -> "?"
        }
    }

    private fun logout() {
        auth.signOut()
        getSharedPreferences("KASIR_SESSION", Context.MODE_PRIVATE).edit().clear().apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
