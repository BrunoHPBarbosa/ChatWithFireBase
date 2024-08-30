package com.example.chatwithfirebase.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatwithfirebase.R
import com.example.chatwithfirebase.databinding.ActivitySignUpBinding
import com.example.chatwithfirebase.databinding.ActivitySuccesBinding

class SuccesActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySuccesBinding.inflate(layoutInflater)
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
        binding.btnEnter.setOnClickListener {
            startActivity(Intent(this@SuccesActivity,MainActivity::class.java))
        }
    }
}