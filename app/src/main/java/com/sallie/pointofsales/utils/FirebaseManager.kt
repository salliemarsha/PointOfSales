package com.sallie.pointofsales.utils

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.sallie.pointofsales.model.ModelProdukActivity

/**
 * FirebaseManager Singleton
 * Centralizes all Firebase operations: Auth, Database, and Storage.
 */
object FirebaseManager {
    private const val TAG = "FirebaseManager"

    // References
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance().reference }
    private val storage by lazy { FirebaseStorage.getInstance().reference }

    // Database Paths
    private const val PATH_PRODUK = "produk"
    private const val PATH_STORAGE_FOTO = "foto_produk"

    /**
     * Auth: Standard login with error handling
     */
    fun login(email: String, pass: String, onResult: (Result<Unit>) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(Result.success(Unit))
                else onResult(Result.failure(task.exception ?: Exception("Unknown Login Error")))
            }
    }

    /**
     * Storage + Database: Safe Save Pattern for Products
     * 1. Upload Image (if exists)
     * 2. Save Metadata to Database
     */
    fun saveProduct(
        product: ModelProdukActivity,
        imageUri: Uri?,
        onResult: (Result<String>) -> Unit
    ) {
        val id = product.idProduk ?: database.child(PATH_PRODUK).push().key ?: return
        product.idProduk = id

        if (imageUri != null) {
            // Step 1: Upload Image
            val fileRef = storage.child("$PATH_STORAGE_FOTO/$id.jpg")
            fileRef.putFile(imageUri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) task.exception?.let { throw it }
                    fileRef.downloadUrl
                }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        product.productImageUrl = task.result.toString()
                        // Step 2: Save to Database
                        writeProductToDb(product, onResult)
                    } else {
                        onResult(Result.failure(task.exception ?: Exception("Image Upload Failed")))
                    }
                }
        } else {
            // No image to upload, save metadata directly
            writeProductToDb(product, onResult)
        }
    }

    private fun writeProductToDb(product: ModelProdukActivity, onResult: (Result<String>) -> Unit) {
        database.child(PATH_PRODUK).child(product.idProduk!!).setValue(product)
            .addOnSuccessListener {
                onResult(Result.success("Produk berhasil disimpan"))
            }
            .addOnFailureListener {
                onResult(Result.failure(it))
            }
    }

    /**
     * Database: Generic delete operation
     */
    fun deleteProduct(product: ModelProdukActivity, onResult: (Result<Unit>) -> Unit) {
        val id = product.idProduk ?: return
        database.child(PATH_PRODUK).child(id).removeValue()
            .addOnSuccessListener {
                // Also attempt to delete image from storage if it exists
                if (!product.productImageUrl.isNullOrEmpty()) {
                    storage.child("$PATH_STORAGE_FOTO/$id.jpg").delete()
                }
                onResult(Result.success(Unit))
            }
            .addOnFailureListener {
                onResult(Result.failure(it))
            }
    }

    fun logout() {
        auth.signOut()
    }
}
