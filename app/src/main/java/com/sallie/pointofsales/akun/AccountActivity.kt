package com.sallie.pointofsales.akun

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)

        auth = FirebaseAuth.getInstance()

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarAccount)
        tvAccountName = findViewById(R.id.tvAccountName)
        tvAvatarInitials = findViewById(R.id.tvAvatarInitials)
        tvUserRole = findViewById(R.id.tvAccountRole)

        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)
        val cvEditProfile = findViewById<MaterialCardView>(R.id.cvEditProfile)
        val cvChangePin = findViewById<MaterialCardView>(R.id.cvChangePin)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        loadUserData()

        btnLogout.setOnClickListener {
            auth.signOut()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }

        cvEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        cvChangePin.setOnClickListener {
            startActivity(Intent(this, ChangePinActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutAccount)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser ?: return

        val uid = user.uid

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(uid)
            .get()
            .addOnSuccessListener { snapshot ->

                val nama =
                    snapshot.child("nama").getValue(String::class.java)
                        ?: user.displayName
                        ?: user.email
                        ?: "User"

                val jabatan =
                    snapshot.child("jabatan").getValue(String::class.java)
                        ?: "Staff"

                tvAccountName.text = nama
                tvUserRole.text = jabatan

                val initials = nama
                    .split(" ")
                    .mapNotNull { it.firstOrNull()?.toString() }
                    .take(2)
                    .joinToString("")
                    .uppercase()

                tvAvatarInitials.text = initials
            }
    }
}