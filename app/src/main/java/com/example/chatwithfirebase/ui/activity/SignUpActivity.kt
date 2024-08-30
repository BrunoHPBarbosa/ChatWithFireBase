package com.example.chatwithfirebase.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatwithfirebase.R
import com.example.chatwithfirebase.databinding.ActivitySignUpBinding
import com.example.chatwithfirebase.model.Users
import com.example.chatwithfirebase.utils.showMessage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy{
        FirebaseAuth.getInstance()
    }
    private val fireStore by lazy{
        FirebaseFirestore.getInstance()
    }
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        eventClick()
        initEventCliqueBtn()
    }

    private fun initEventCliqueBtn() = with(binding) {
        btnSignUp.setOnClickListener {
            if (validarCampos()) {

                cadastrarUsuario(name,email,password)
            }
        }
    }

    private fun cadastrarUsuario(name: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(
            email,password
        ).addOnCompleteListener { result ->

            if (result.isSuccessful){
                val idUser = result.result.user?.uid
                if (idUser!= null){
                    val user = Users(
                        idUser,name,email
                    )
                    saveUserFireSore(user)
                }


            }

        }.addOnFailureListener { exception ->
            try {
                throw exception
            } catch (exceptionWeakPassword: FirebaseAuthWeakPasswordException) {
                exceptionWeakPassword.printStackTrace()
                showMessage("The password is weak")

            } catch (exceptionCredentialsColision: FirebaseAuthUserCollisionException) {
                exceptionCredentialsColision.printStackTrace()
                showMessage("The email already exist")

            } catch (exceptionCredentialsInvalid: FirebaseAuthInvalidCredentialsException) {
                exceptionCredentialsInvalid.printStackTrace()
                showMessage("Invalid Email, enter a valid Email")
            }
        }
    }

    private fun saveUserFireSore(user: Users) {
        fireStore
            .collection("users")
            .document(user.id)
            .set(user)
            .addOnSuccessListener {
                showMessage("Succes to create Account")
                startActivity(Intent
                    (applicationContext,SuccesActivity::class.java))
            }.addOnFailureListener {
                showMessage("Faild to create Account")
            }

    }

    private fun validarCampos(): Boolean {

        name = binding.edtName.text.toString()
        email = binding.edtEmail.text.toString()
        password = binding.edtPassword.text.toString()

        if (name.isNotEmpty()) {
            binding.edtName.error = null

            if (email.isNotEmpty()) {
                binding.edtEmail.error = null

                if (password.isNotEmpty()) {
                    binding.edtPassword.error = null
                    return true

                } else {
                    binding.textInputLayoutPass.error = "Password is Required!"
                    return false
                }
            } else {
                binding.textInputLayoutEmail.error = "Email is Required!"
                return false
            }
        } else {
            binding.textInputLayoutName.error = "Name is Required!"
            return false
        }


    }


    private fun eventClick() = with(binding) {
        txtLogin.setOnClickListener {
            startActivity(Intent(this@SignUpActivity,LoginActivity::class.java))
        }
    }
}