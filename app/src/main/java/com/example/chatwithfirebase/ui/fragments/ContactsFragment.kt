package com.example.chatwithfirebase.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatwithfirebase.R
import com.example.chatwithfirebase.databinding.FragmentContactsBinding
import com.example.chatwithfirebase.model.Users
import com.example.chatwithfirebase.ui.activity.MessageActivity
import com.example.chatwithfirebase.ui.adapters.ContactsAdapter
import com.example.chatwithfirebase.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage


class ContactsFragment : Fragment() {

    private lateinit var eventSnapshot: ListenerRegistration

    private lateinit var binding: FragmentContactsBinding

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val fireStore by lazy {
        FirebaseFirestore.getInstance()
    }
    private lateinit var contactAdapter: ContactsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentContactsBinding.inflate(
           inflater,container,false
       )

        contactAdapter = ContactsAdapter{ user ->
            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra("dataDestination",user)
          //  intent.putExtra("origin",Constants.ORIGIN_CONTACT)
           startActivity(intent)
        }
        binding.rvContacts.adapter = contactAdapter
        binding.rvContacts.layoutManager = LinearLayoutManager(context)
        binding.rvContacts.addItemDecoration(
            DividerItemDecoration(
                context,LinearLayoutManager.VERTICAL
            )
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        listenerContactUsers()
    }

    private fun listenerContactUsers() {

        eventSnapshot =  fireStore
            .collection("users")
            .addSnapshotListener{ querySnapshot,error ->

                val contactList = mutableListOf<Users>()
                val documents = querySnapshot?.documents

                documents?.forEach { documentSnapshot ->

                    val idLoggedUser = firebaseAuth.currentUser?.uid
                    val user = documentSnapshot.toObject(Users::class.java)

                    if (user!= null && idLoggedUser != null) {
                        if (idLoggedUser != user.id) {
                            contactList.add(user)

                        }
                    }
                }
                if (contactList.isNotEmpty()) {
                    contactAdapter.addList(contactList)
                }
            }

    }

    override fun onDestroy() {
        super.onDestroy()
        eventSnapshot.remove()
    }

}