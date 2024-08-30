package com.example.chatwithfirebase.repository

import com.example.chatwithfirebase.utils.UserData
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class UserRepository {

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val fireStore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun recoverDataUser(onDataRetrieved: () -> Unit, onError: (Exception) -> Unit) {

        val idUser = firebaseAuth.currentUser?.uid
        if (idUser != null) {
            fireStore
                .collection("users")
                .document(idUser)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val dataUsers = documentSnapshot.data
                    if (dataUsers != null) {
                            UserData.name = dataUsers["name"] as? String ?: ""
                            UserData.photos = dataUsers["photos"] as? String ?: ""
                            onDataRetrieved()
                        }
                    }

                .addOnFailureListener { exception ->
                    onError(exception)

                }

        } else {
            onError(Exception("user id is null"))
        }
    }
}
