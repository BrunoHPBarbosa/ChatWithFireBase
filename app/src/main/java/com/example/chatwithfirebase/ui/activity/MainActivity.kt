package com.example.chatwithfirebase.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatwithfirebase.R
import com.example.chatwithfirebase.ui.adapters.ViewPagerAdapter
import com.example.chatwithfirebase.databinding.ActivityMainBinding
import com.example.chatwithfirebase.databinding.ActivityProfileBinding
import com.example.chatwithfirebase.repository.UserRepository
import com.example.chatwithfirebase.utils.UserData
import com.example.chatwithfirebase.utils.showMessage
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val userRepository by lazy { UserRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        onClickProfile()
        initViewPager()

        userRepository.recoverDataUser(
            onDataRetrieved = {recoverDatasUser()},
            onError = {exception ->
                showMessage("Error: ${exception.message}")
            }
        )
    }

    override fun onStart() {
        super.onStart()
        recoverDatasUser()
    }

    private fun recoverDatasUser() = with(binding){
        val name = UserData.name
        val photo = UserData.photos
        if (!photo.isNullOrEmpty()) {
            Picasso.get()
                .load(photo)
                .into(imgProfileA)

        }
    }

    private fun initViewPager() {

        val tabLayout = binding.tabLayoutMain
        val viewPager = binding.viewPagerMain

        val tabs = listOf("Chats","Contacts")
        viewPager.adapter = ViewPagerAdapter(
           tabs, supportFragmentManager,lifecycle
        )
        tabLayout.isTabIndicatorFullWidth = true
        TabLayoutMediator(tabLayout,viewPager){ tab,position ->
            tab.text = tabs[position]
        }.attach()
    }

    private fun onClickProfile() = with(binding){
        imgProfileA.setOnClickListener {
            startActivity(Intent(this@MainActivity,ProfileActivity::class.java))
        }
    }
}