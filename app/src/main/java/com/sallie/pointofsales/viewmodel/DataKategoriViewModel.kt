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

    private val searchQuery = MutableLiveData<String?>()

    val isLoading = MutableLiveData<Boolean>()

    val isSearchEmpty = MutableLiveData<Boolean>()

    init {
        getData()
    }

    fun getData (){
        isLoading.value = true
        val query = myRef.orderByChild("idKategori").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isLoading.value = false
                if (snapshot.exists()) {
                    val list = ArrayList<ModelKategoriActivity>()
                    for (dataSnapshot in snapshot.children) {
                        val kategori = dataSnapshot.getValue(ModelKategoriActivity::class.java)
                        if (kategori == null) {
                            Log.e("DataKategoriViewModel", "Failed to purpose kategori")
                        } else {
                            list.add(kategori)
                        }
                    }
                    originalKategoriList.clear()
                    originalKategoriList.addAll(list)
                    kategoriList.value = list
                    isSearchEmpty.value = false
                    Log.d("DataKategoriViewModel", "Loaded ${list.size} kategori items.")
                } else {
                    originalKategoriList.clear()
                    kategoriList.value = ArrayList()
                    isSearchEmpty.value = true
                    Log.d("DataKategoriViewModel", "No kategori data found.")
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
            kategoriList.value = originalKategoriList
            isSearchEmpty.value = false
        } else {
            val filteredList = originalKategoriList.filter {
                it.namaKategori?.lowercase()?.contains(query.lowercase()) == true
            }
            kategoriList.value = ArrayList(filteredList)
            isSearchEmpty.value = filteredList.isEmpty()
        }
    }
}
