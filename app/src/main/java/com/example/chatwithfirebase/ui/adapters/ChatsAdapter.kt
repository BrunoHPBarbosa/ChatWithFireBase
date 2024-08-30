package com.example.chatwithfirebase.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chatwithfirebase.databinding.ItemContactBinding
import com.example.chatwithfirebase.databinding.ItemMessageBinding
import com.example.chatwithfirebase.model.Chats
import com.squareup.picasso.Picasso

class ChatsAdapter(
    private val onClick: (Chats) -> Unit
):Adapter<ChatsAdapter.ChatsViewHolder>() {

    private var listChats = emptyList<Chats>()
    fun addList(list: List<Chats>){
        listChats = list
        notifyDataSetChanged()
    }
    inner class ChatsViewHolder(
        private val binding : ItemMessageBinding
    ): ViewHolder(binding.root){

        fun bind(chat: Chats){

            binding.txtMessageName.text = chat.name
            binding.txtLastMessage.text = chat.lastMessage

            val photoUrl = chat.photo
            if (photoUrl.isNotEmpty()) {
                Picasso.get()
                    .load(chat.photo)
                    .into(binding.imgMessage)

                binding.clItemMessage.setOnClickListener {
                    onClick(chat)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemMessageBinding.inflate(
            inflater,parent,false
        )
        return ChatsViewHolder(itemView)
    }



    override fun onBindViewHolder(holder: ChatsAdapter.ChatsViewHolder, position: Int) {
        val chats = listChats[position]
        holder.bind(chats)
    }
    override fun getItemCount(): Int {
        return listChats.size
    }
}