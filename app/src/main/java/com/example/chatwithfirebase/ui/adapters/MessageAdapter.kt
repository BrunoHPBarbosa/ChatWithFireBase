package com.example.chatwithfirebase.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chatwithfirebase.databinding.ItemLayoutDestinatarioBinding
import com.example.chatwithfirebase.databinding.ItemMessagensRemetenteBinding
import com.example.chatwithfirebase.model.Message
import com.example.chatwithfirebase.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter : Adapter<ViewHolder>() {

    private var listMessages = emptyList<Message>()
    fun addList(list: List<Message>) {
        listMessages = list
        notifyDataSetChanged()
    }

    class MessagesRemetenteViewHolder(
        private val binding: ItemMessagensRemetenteBinding
    ) : ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.txtRemetente.text = message.message
        }

        companion object {
            fun inflateLayout(parent: ViewGroup): MessagesRemetenteViewHolder {

                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMessagensRemetenteBinding.inflate(
                    inflater, parent, false
                )
                return MessagesRemetenteViewHolder(itemView)


            }
        }

    }

    class MessagesDestinatarioViewHolder(
        private val binding: ItemLayoutDestinatarioBinding
    ) : ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.textDestinatario.text = message.message
        }

        companion object {
            fun inflateLayout(parent: ViewGroup): MessagesDestinatarioViewHolder {

                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemLayoutDestinatarioBinding.inflate(
                    inflater, parent, false
                )
                return MessagesDestinatarioViewHolder(itemView)


            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = listMessages[position]
        val idUser = FirebaseAuth.getInstance().currentUser?.uid.toString()
        return if (idUser == message.idUser) {
            Constants.TIPO_REMETENTE
        } else {
            Constants.TIPO_DESTINATARIO
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if (viewType == Constants.TIPO_REMETENTE)

            return MessagesRemetenteViewHolder.inflateLayout(parent)

        return MessagesDestinatarioViewHolder.inflateLayout(parent)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = listMessages[position]
        when (holder) {
            is MessagesRemetenteViewHolder -> holder.bind(message)
            is MessagesDestinatarioViewHolder -> holder.bind(message)
        }

    }

    override fun getItemCount(): Int {
        return listMessages.size
    }
}