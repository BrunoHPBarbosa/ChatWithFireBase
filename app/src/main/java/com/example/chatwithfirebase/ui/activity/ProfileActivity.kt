package com.example.chatwithfirebase.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatwithfirebase.R
import com.example.chatwithfirebase.databinding.ActivityProfileBinding
import com.example.chatwithfirebase.repository.UserRepository
import com.example.chatwithfirebase.utils.UserData
import com.example.chatwithfirebase.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }
    private val fireStore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val userRepository by lazy { UserRepository() }


    private var alredyHavePermissionsGallery = false
    private var alredyHavePermissionsCamera = false

    //adiciona imagem
    private val managerGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){ uri ->
        if (uri != null){
            binding.imgProfile.setImageURI(uri)
            uploadImageStorage(uri)
        }else{
            showMessage("No image selected")
        }

    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        logoutUser()
        requestPermission()
        initiEventClick()

        userRepository.recoverDataUser(
            onDataRetrieved = { updateProfileUI() },
            onError = { exception ->
                showMessage("Error: ${exception.message}")
            }
                )

    }


    private fun updateProfileUI() {
        binding.edtNewName.setText(UserData.name)
        if (!UserData.photos.isNullOrEmpty()) {
            Picasso.get()
                .load(UserData.photos)
                .into(binding.imgProfile)
        }
    }

    override fun onStart() {
        super.onStart()

    }
    private fun uploadImageStorage(uri: Uri) {

        val idUser = firebaseAuth.currentUser?.uid
        if (idUser!= null){
            storage
                .getReference("photos")
                .child("users")
                .child(idUser)
                .child("profile.jpg")
                .putFile(uri)
                .addOnSuccessListener { task ->
                    showMessage("Success to upload Image")
                    task.metadata
                    ?.reference
                    ?.downloadUrl
                    ?.addOnSuccessListener { uri ->
                        val data = mapOf(
                            "photos" to uri.toString()
                        )
                        updateDataProfile(idUser,data)
                        }

                }.addOnFailureListener {
                  showMessage("Error to upload Image")
                }

        }

    }

    private fun updateDataProfile(iduser: String, data: Map<String, String>) {

        fireStore.collection("users")
            .document(iduser)
            .update(data)
            .addOnSuccessListener {
                showMessage("Success to update profile")
            }.addOnFailureListener { exception->
                showMessage("Faild to update profile ${exception.message}")
            }
    }

    private fun initiEventClick() = with(binding) {

        fabAddImg.setOnClickListener {
            if (alredyHavePermissionsGallery){

                managerGallery.launch("image/*")
                
            }else{
                showMessage("You dont have permission to acces Gallery")
                requestPermission()
            }
        }
        binding.btnSaveNewName.setOnClickListener {

            val nameUser = binding.edtNewName.text.toString()
            if (nameUser.isNotEmpty()){
               val iduser = firebaseAuth.currentUser?.uid
                if (iduser!= null){
                    val data = mapOf(
                        "name" to nameUser.toString()
                    )
                    updateDataProfile(iduser,data)
                }
            }else{

                showMessage("Fill in the name to update")

            }
        }
    }

    private fun requestPermission() {

        alredyHavePermissionsCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        alredyHavePermissionsGallery = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        val listPermissionsDenied = mutableListOf<String>()
        if (!alredyHavePermissionsCamera)
            listPermissionsDenied.add(Manifest.permission.CAMERA)
        if (!alredyHavePermissionsGallery)
            listPermissionsDenied.add(Manifest.permission.READ_MEDIA_IMAGES)

        if (listPermissionsDenied.isNotEmpty()) {

            //solicitar multiplas permissoes
            val managerPermissions = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permission ->

                alredyHavePermissionsCamera = permission[Manifest.permission.CAMERA]
                    ?: alredyHavePermissionsCamera

                alredyHavePermissionsGallery = permission[Manifest.permission.READ_MEDIA_IMAGES]
                    ?: alredyHavePermissionsGallery
            }
            managerPermissions.launch(listPermissionsDenied.toTypedArray())
        }
    }
    private fun logoutUser() = with(binding) {
        btnLogout.setOnClickListener {
            dielogLogout()

        }
    }

    private fun dielogLogout() {

        AlertDialog.Builder(this@ProfileActivity)
            .setTitle("Log out")
            .setMessage("Are you sure want to log out?")
            .setNegativeButton("Cancel") { dielog, position -> }
            .setPositiveButton("Log out") { dielog, position ->
                firebaseAuth.signOut()
                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
            }
            .create()
            .show()
    }
}


