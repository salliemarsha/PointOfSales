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

    private val searchQuery = MutableLiveData<String?>()

    val isLoading = MutableLiveData<Boolean>()

    val isSearchEmpty = MutableLiveData<Boolean>()

    init {
        getData()
    }

    fun getData (){
        isLoading.value = true
        val query = myRef.orderByChild("idProduk").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isLoading.value = false
                if (snapshot.exists()) {
                    val list = ArrayList<ModelProdukActivity>()
                    for (dataSnapshot in snapshot.children) {
                        val produk = dataSnapshot.getValue(ModelProdukActivity::class.java)
                        if (produk == null) {
                            Log.e("DataProdukViewModel", "Failed to purpose produk")
                        } else {
                            list.add(produk)
                        }
                    }
                    originalProdukList.clear()
                    originalProdukList.addAll(list)
                    produkList.value = list
                    isSearchEmpty.value = false
                    Log.d("DataProdukViewModel", "Loaded ${list.size} produk items.")
                } else {
                    originalProdukList.clear()
                    produkList.value = ArrayList()
                    isSearchEmpty.value = true
                    Log.d("DataProdukViewModel", "No produk data found.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading.value = false
            }
        })
    }

    fun filterList(query: String?) {
        searchQuery.value = query
        if (query.isNullOrEmpty()) {
            produkList.value = originalProdukList
            isSearchEmpty.value = false
        } else {
            val filteredList = originalProdukList.filter {
                it.namaProduk?.lowercase()?.contains(query.lowercase()) == true
            }
            produkList.value = ArrayList(filteredList)
            isSearchEmpty.value = filteredList.isEmpty()
        }
    }
}
