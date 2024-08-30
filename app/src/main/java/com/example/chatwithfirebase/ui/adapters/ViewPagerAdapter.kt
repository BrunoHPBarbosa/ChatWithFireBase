package com.example.chatwithfirebase.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chatwithfirebase.ui.fragments.ChatsFragment
import com.example.chatwithfirebase.ui.fragments.ContactsFragment

class ViewPagerAdapter(
    private val tab: List<String>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager,lifecycle) {
    
    override fun getItemCount(): Int {
return tab.size
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            1 -> return  ContactsFragment()
        }
        return ChatsFragment()
    }
}