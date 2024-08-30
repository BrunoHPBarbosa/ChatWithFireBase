package com.example.chatwithfirebase.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatwithfirebase.R
import com.example.chatwithfirebase.databinding.FragmentChatsBinding
import com.example.chatwithfirebase.databinding.FragmentContactsBinding
import com.example.chatwithfirebase.model.Chats
import com.example.chatwithfirebase.model.Users
import com.example.chatwithfirebase.ui.activity.MessageActivity
import com.example.chatwithfirebase.ui.adapters.ChatsAdapter
import com.example.chatwithfirebase.ui.adapters.ContactsAdapter
import com.example.chatwithfirebase.utils.Constants
import com.example.chatwithfirebase.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ChatsFragment : Fragment() {

    private lateinit var eventSnapshot: ListenerRegistration

    private lateinit var binding: FragmentChatsBinding

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val fireStore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var chatsAdapter: ChatsAdapter

    override fun onStart() {
        super.onStart()
        listenerChatUsers()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventSnapshot.remove()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChatsBinding.inflate(
            inflater, container, false
        )
        chatsAdapter = ChatsAdapter{ chat ->
            val intent = Intent(context, MessageActivity::class.java)

            val user = Users(
                id = chat.idUserDestinatario,
                name = chat.name,
                photos = chat.photo
            )
            intent.putExtra("dataDestination",user)
         //  intent.putExtra("origin",Constants.ORIGIN_CHAT)
            startActivity(intent)
        }
        binding.rvChats.adapter = chatsAdapter
        binding.rvChats.layoutManager = LinearLayoutManager(context)
        binding.rvChats.addItemDecoration(
            DividerItemDecoration(
                context, LinearLayoutManager.VERTICAL
            )
        )
        return binding.root
    }
    private fun listenerChatUsers() {
        val idUserRemetente = firebaseAuth.currentUser?.uid
        if (idUserRemetente!= null){
            eventSnapshot = fireStore
                .collection(Constants.CHATS)
                .document(idUserRemetente )
                .collection(Constants.LAST_MESSAGES)
                .orderBy("data", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, error ->
                    if (error!= null){
                        activity?.showMessage("error to get message${error.message}")
                        Log.i("erro","${error.message}")
                    }
                    val listChats = mutableListOf<Chats>()
                    val documents = querySnapshot?.documents
                    documents?.forEach {  documentSnapshot ->
                        val chat =documentSnapshot.toObject(Chats::class.java)
                        if (chat!= null){
                        listChats.add(chat)
                            Log.i("chat","${chat.name}")

                        }
                    }
                    // atualizar o adpter
                    if (listChats.isNotEmpty()){
                        chatsAdapter.addList(listChats)
                    }
                }
        }
    }



}