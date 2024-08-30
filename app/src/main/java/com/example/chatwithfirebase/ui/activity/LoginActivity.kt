package com.example.chatwithfirebase.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatwithfirebase.R
import com.example.chatwithfirebase.databinding.ActivityLoginBinding
import com.example.chatwithfirebase.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class LoginActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy{
        FirebaseAuth.getInstance()
    }
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

        initEventsClick()
       //firebaseAuth.signOut()
    }

    override fun onStart() {
        super.onStart()
        verifyLoggedUser()
    }

    private fun verifyLoggedUser() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser!=null){
            startActivity(
                Intent(this,MainActivity::class.java))
        }
    }

    private fun initEventsClick() = with(binding) {
        txtSignUp.setOnClickListener {
           val intent = Intent(this@LoginActivity,SignUpActivity::class.java)
            startActivity(intent)

        }
        binding.btnLogin.setOnClickListener {
            if (validFields()){
                loginUser()

            }
        }
    }

    private fun loginUser() {
        firebaseAuth.signInWithEmailAndPassword(
            email,password
        ).addOnSuccessListener {
            showMessage("login Succesfuly")
            startActivity(
                Intent(this,MainActivity::class.java)
            )
        }.addOnFailureListener { exception ->
            try {
                throw exception
            } catch (exceptionInvalidUser: FirebaseAuthInvalidUserException) {
                exceptionInvalidUser.printStackTrace()
                showMessage("Email not registered ")

            }catch (exceptionCredentialsInvalid:FirebaseAuthInvalidCredentialsException){
                exceptionCredentialsInvalid.printStackTrace()
                showMessage("Email or password is invalid or incorrect")
            }
        }
    }

    private fun validFields(): Boolean {
        email = binding.edtEmail.text.toString()
        password = binding.edtPassw.text.toString()

        if (email.isNotEmpty()){
            binding.textInputLayout.error = null
            if(password.isNotEmpty()){
                binding.textInputLayout2.error = null
                return true
            }else{
                binding.textInputLayout2.error = "Required Password"
                return false
            }
        }else{
            binding.textInputLayout.error = "Required Email"
            return false

        }
    }
}