package com.sallie.pointofsales.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import androidx.lifecycle.ViewModel
import android.util.Log
import com.sallie.pointofsales.model.ModelProdukActivity

class DataProdukViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("produk")

    val produkList = MutableLiveData<ArrayList<ModelProdukActivity>>()
    private var originalProdukList = ArrayList<ModelProdukActivity>()
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
                val list = ArrayList<ModelProdukActivity>()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        try {
                            val produk = dataSnapshot.getValue(ModelProdukActivity::class.java)
                            if (produk != null) {
                                // Management screen shows BOTH Active and Inactive
                                list.add(produk)
                            }
                        } catch (e: Exception) {
                            Log.e("ProductStatus", "Error parsing product: ${e.message}")
                        }
                    }
                }
                Log.d("ProductStatus", "Loaded ${list.size} products (Active + Inactive) for management.")
                
                originalProdukList.clear()
                originalProdukList.addAll(list)
                produkList.value = list
                isSearchEmpty.value = list.isEmpty()
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading.value = false
                Log.e("ProductStatus", "Database error: ${error.message}")
            }
        })
    }

    fun filterList(query: String?) {
        if (query.isNullOrEmpty()) {
            produkList.value = originalProdukList
            isSearchEmpty.value = originalProdukList.isEmpty()
        } else {
            val filteredList = originalProdukList.filter {
                it.namaProduk?.lowercase()?.contains(query.lowercase()) == true
            }
            produkList.value = ArrayList(filteredList)
            isSearchEmpty.value = filteredList.isEmpty()
        }
    }
}
