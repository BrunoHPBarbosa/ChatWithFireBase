package com.example.chatwithfirebase.ui.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatwithfirebase.R
import com.example.chatwithfirebase.databinding.ActivityMessageBinding
import com.example.chatwithfirebase.model.Chats
import com.example.chatwithfirebase.model.Message
import com.example.chatwithfirebase.model.Users
import com.example.chatwithfirebase.ui.adapters.MessageAdapter
import com.example.chatwithfirebase.utils.Constants
import com.example.chatwithfirebase.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

class MessageActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityMessageBinding.inflate(layoutInflater)
    }
    private var dadosDestinatario : Users? = null
    private var dadosUsuarioRemetente : Users? = null

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val fireStore by lazy {
        FirebaseFirestore.getInstance()
    }
    private lateinit var conversasAdapter: MessageAdapter

    private lateinit var listenerRegistration: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        recoverDataUsers()
        initTollBar()
        initEventClick()
        initListeners()
        initRecyclerView()
    }

    private fun initRecyclerView() = with(binding) {
        conversasAdapter = MessageAdapter()
        rvChat.adapter = conversasAdapter
        rvChat.layoutManager = LinearLayoutManager(applicationContext)

    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    private fun initListeners() {

        val idUserRemetente = firebaseAuth.currentUser?.uid
        val idUserDestinatario = dadosDestinatario?.id
        if (idUserRemetente != null && idUserDestinatario != null) {

         listenerRegistration =  fireStore
              .collection(Constants.COLECTION_MESSAGES)
              .document(idUserRemetente)
              .collection(idUserDestinatario)
              .orderBy("data", Query.Direction.ASCENDING)
              .addSnapshotListener { querySnapshot, error ->
                  if (error!= null){
                      showMessage("error")
                  }
                  val listMessages = mutableListOf<Message>()
                  val document = querySnapshot?.documents
                  document?.forEach {  documentSnapshot ->
                      val message = documentSnapshot.toObject(Message::class.java)
                      if (message != null){
                          listMessages.add(message)

                      }

                  }
                  if (listMessages.isNotEmpty()){

                      conversasAdapter.addList(listMessages)
                  }
              }
        }
    }

    private fun initEventClick() = with(binding) {
        floatingActionButton.setOnClickListener {
            val message = edtSendMessage.text.toString()
            saveMessage(message)
        }
    }

    private fun saveMessage(textMessage: String) {
        if (textMessage.isNotEmpty()) {
            val idUserRemetente = firebaseAuth.currentUser?.uid
            val idUserDestinatario = dadosDestinatario?.id
            if (idUserRemetente != null && idUserDestinatario != null) {
                val message = Message(
                    idUserRemetente, textMessage
                )

                //salvar para o remetente
                saveMessageFirestore( idUserRemetente,idUserDestinatario,message)
                
               // salvar foto e nome remetente
                val chatRemetente  = Chats(
                    idUserRemetente,idUserDestinatario,
                    dadosDestinatario!!.photos,
                    dadosDestinatario!!.name,textMessage

                )
                salvarChatFirestore(chatRemetente)

                //salvar para o destinatario
                saveMessageFirestore(
                    idUserDestinatario,idUserRemetente,message
                )
                
                //salva message para o destinatario
                val chatDestinatario  = Chats(
                    idUserDestinatario, idUserRemetente,
                    dadosUsuarioRemetente!!.photos,dadosUsuarioRemetente!!.name,
                    textMessage

                )
                salvarChatFirestore(chatDestinatario)
                binding.edtSendMessage.setText("")
            }

        }
    }

    private fun salvarChatFirestore(chat: Chats) {

        fireStore
            .collection(Constants.CHATS)
            .document(chat.idUserRemetente)
            .collection(Constants.LAST_MESSAGES)
            .document(chat.idUserDestinatario)
            .set(chat)
            .addOnFailureListener {
                showMessage("error to save message")
            }
    }

    private fun saveMessageFirestore(
        idUserRemetente:String,
        idUserDestinatario:String,
        message: Message
    ) {
        fireStore.collection(Constants.COLECTION_MESSAGES)
            .document(idUserRemetente)
            .collection(idUserDestinatario)
            .add(message)
            .addOnFailureListener {
                showMessage("faild to send a message")
            }
    }

    private fun initTollBar() {
        val toolbar = binding.materialToolbar
        setSupportActionBar( toolbar)
        supportActionBar?.apply {
            title = ""
            if (dadosDestinatario!= null){
                binding.txtNameUser.text = dadosDestinatario!!.name
                Picasso.get()
                    .load(dadosDestinatario!!.photos)
                    .into(binding.imgUser)
            }
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.back_arrow)
        }
    }

    private fun recoverDataUsers() {
// recuperando dados do usuario logado
        val idUserLogged = firebaseAuth.currentUser?.uid
        if(idUserLogged!= null) {
            fireStore.collection(Constants.USERS)
                .document(idUserLogged)
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    val user = documentSnapshot.toObject(Users::class.java)
                    if(user!= null){
                        dadosUsuarioRemetente = user
                    }

                }
        }
        //recuperar dados do destinatario
        val extras = intent.extras
        if (extras!= null){
            dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable("dataDestination",Users::class.java)
            }else{
                extras.getParcelable("dataDestination")
            }
           // val origin = extras.getString("origin")

        }
    }
}