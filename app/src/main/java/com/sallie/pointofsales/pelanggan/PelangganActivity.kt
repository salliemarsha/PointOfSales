package com.sallie.pointofsales.pelanggan

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.sallie.pointofsales.R
import com.sallie.pointofsales.adapter.PelangganAdapter
import com.sallie.pointofsales.databinding.ActivityPelangganBinding
import com.sallie.pointofsales.model.ModelPelanggan

class PelangganActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPelangganBinding
    private lateinit var database: DatabaseReference
    private lateinit var adapter: PelangganAdapter
    private var listPelanggan = ArrayList<ModelPelanggan>()
    private var filteredList = ArrayList<ModelPelanggan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPelangganBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("pelanggan")

        setupRecyclerView()
        setupListeners()
        loadDataPelanggan()
    }

    private fun setupRecyclerView() {
        adapter = PelangganAdapter(filteredList, 
            onEditClick = { pelanggan -> showDialogEdit(pelanggan) },
            onDeleteClick = { pelanggan -> showDialogDelete(pelanggan) }
        )
        binding.rvPelanggan.layoutManager = LinearLayoutManager(this)
        binding.rvPelanggan.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.fabAddPelanggan.setOnClickListener { showDialogTambah() }

        binding.etSearchPelanggan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadDataPelanggan() {
        binding.progressBar.visibility = View.VISIBLE
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listPelanggan.clear()
                for (dataSnapshot in snapshot.children) {
                    val pelanggan = dataSnapshot.getValue(ModelPelanggan::class.java)
                    pelanggan?.let { listPelanggan.add(it) }
                }
                
                binding.progressBar.visibility = View.GONE
                filter(binding.etSearchPelanggan.text.toString())
                
                if (listPelanggan.isEmpty()) {
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.rvPelanggan.visibility = View.GONE
                } else {
                    binding.layoutEmpty.visibility = View.GONE
                    binding.rvPelanggan.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@PelangganActivity, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filter(text: String) {
        filteredList.clear()
        for (item in listPelanggan) {
            if (item.namaPelanggan?.lowercase()?.contains(text.lowercase()) == true ||
                item.nomorTelepon?.contains(text) == true ||
                item.email?.lowercase()?.contains(text.lowercase()) == true
            ) {
                filteredList.add(item)
            }
        }
        adapter.updateData(filteredList)
        
        if (filteredList.isEmpty() && listPelanggan.isNotEmpty()) {
            // Optional: show "No results found"
        }
    }

    private fun showDialogTambah() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_tambah_pelanggan, null)
        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog).setView(dialogView).create()

        val etNama = dialogView.findViewById<TextInputEditText>(R.id.etNamaPelanggan)
        val etTelepon = dialogView.findViewById<TextInputEditText>(R.id.etNomorTelepon)
        val etEmail = dialogView.findViewById<TextInputEditText>(R.id.etEmail)
        val etAlamat = dialogView.findViewById<TextInputEditText>(R.id.etAlamat)
        val btnSimpan = dialogView.findViewById<MaterialButton>(R.id.btnSimpan)
        val btnBatal = dialogView.findViewById<MaterialButton>(R.id.btnBatal)

        btnBatal.setOnClickListener { dialog.dismiss() }

        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val telepon = etTelepon.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val alamat = etAlamat.text.toString().trim()

            if (nama.isEmpty()) {
                etNama.error = "Nama wajib diisi"
                return@setOnClickListener
            }
            if (telepon.isEmpty()) {
                etTelepon.error = "Nomor telepon wajib diisi"
                return@setOnClickListener
            }

            val id = database.push().key ?: ""
            val pelanggan = ModelPelanggan(id, nama, telepon, email, alamat, System.currentTimeMillis())

            database.child(id).setValue(pelanggan).addOnSuccessListener {
                dialog.dismiss()
                Snackbar.make(binding.root, "Pelanggan berhasil ditambahkan", Snackbar.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun showDialogEdit(pelanggan: ModelPelanggan) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_pelanggan, null)
        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog).setView(dialogView).create()

        val etNama = dialogView.findViewById<TextInputEditText>(R.id.etNamaPelanggan)
        val etTelepon = dialogView.findViewById<TextInputEditText>(R.id.etNomorTelepon)
        val etEmail = dialogView.findViewById<TextInputEditText>(R.id.etEmail)
        val etAlamat = dialogView.findViewById<TextInputEditText>(R.id.etAlamat)
        val btnSimpan = dialogView.findViewById<MaterialButton>(R.id.btnSimpan)
        val btnBatal = dialogView.findViewById<MaterialButton>(R.id.btnBatal)

        etNama.setText(pelanggan.namaPelanggan)
        etTelepon.setText(pelanggan.nomorTelepon)
        etEmail.setText(pelanggan.email)
        etAlamat.setText(pelanggan.alamat)

        btnBatal.setOnClickListener { dialog.dismiss() }

        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val telepon = etTelepon.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val alamat = etAlamat.text.toString().trim()

            if (nama.isEmpty()) {
                etNama.error = "Nama wajib diisi"
                return@setOnClickListener
            }
            if (telepon.isEmpty()) {
                etTelepon.error = "Nomor telepon wajib diisi"
                return@setOnClickListener
            }

            val updatedPelanggan = pelanggan.copy(
                namaPelanggan = nama,
                nomorTelepon = telepon,
                email = email,
                alamat = alamat
            )

            pelanggan.idPelanggan?.let { id ->
                database.child(id).setValue(updatedPelanggan).addOnSuccessListener {
                    dialog.dismiss()
                    Snackbar.make(binding.root, "Data pelanggan diperbarui", Snackbar.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    private fun showDialogDelete(pelanggan: ModelPelanggan) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Pelanggan")
            .setMessage("Apakah Anda yakin ingin menghapus pelanggan '${pelanggan.namaPelanggan}'?")
            .setPositiveButton("Hapus") { _, _ ->
                pelanggan.idPelanggan?.let { id ->
                    database.child(id).removeValue().addOnSuccessListener {
                        Snackbar.make(binding.root, "Pelanggan berhasil dihapus", Snackbar.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
