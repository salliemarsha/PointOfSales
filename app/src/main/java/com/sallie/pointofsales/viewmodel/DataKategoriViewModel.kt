package com.sallie.pointofsales.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import androidx.lifecycle.ViewModel
import android.util.Log
import com.sallie.pointofsales.model.ModelKategoriActivity

class DataKategoriViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("kategori")

    val kategoriList = MutableLiveData<ArrayList<ModelKategoriActivity>>()
    private var originalKategoriList = ArrayList<ModelKategoriActivity>()
    val isLoading = MutableLiveData<Boolean>()
    val isSearchEmpty = MutableLiveData<Boolean>()

    init {
        getData()
    }

    fun getData() {
        isLoading.value = true
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isLoading.value = false
                val list = ArrayList<ModelKategoriActivity>()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        try {
                            val kategori = dataSnapshot.getValue(ModelKategoriActivity::class.java)
                            if (kategori != null) {
                                // Management screen shows BOTH Active and Inactive
                                list.add(kategori)
                            }
                        } catch (e: Exception) {
                            Log.e("CategoryStatus", "Error parsing category: ${e.message}")
                        }
                    }
                }
                Log.d("CategoryStatus", "Loaded ${list.size} categories (Active + Inactive) for management.")
                
                originalKategoriList.clear()
                originalKategoriList.addAll(list)
                kategoriList.value = list
                isSearchEmpty.value = list.isEmpty()
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading.value = false
                Log.e("CategoryStatus", "Database error: ${error.message}")
            }
        })
    }

    fun filterList(query: String?) {
        if (query.isNullOrEmpty()) {
            kategoriList.value = originalKategoriList
            isSearchEmpty.value = originalKategoriList.isEmpty()
        } else {
            val filteredList = originalKategoriList.filter {
                it.namaKategori?.lowercase()?.contains(query.lowercase()) == true
            }
            kategoriList.value = ArrayList(filteredList)
            isSearchEmpty.value = filteredList.isEmpty()
        }
    }
}
