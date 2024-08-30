package com.example.chatwithfirebase.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chatwithfirebase.databinding.ItemContactBinding
import com.example.chatwithfirebase.model.Users
import com.squareup.picasso.Picasso

class ContactsAdapter(
    private val onClick: (Users) -> Unit
): Adapter<ContactsAdapter.ContactsViewHolder>() {
    private var listContacs = emptyList<Users>()
    fun addList(list: List<Users>){
        listContacs = list
        notifyDataSetChanged()
    }
    inner class ContactsViewHolder(
        private val binding : ItemContactBinding
    ): ViewHolder(binding.root){

        fun bind(user: Users){

            binding.txtContactName.text = user.name
            val photoUrl = user.photos
            if (photoUrl.isNotEmpty()) {
                Picasso.get()
                    .load(user.photos)
                    .into(binding.imgContact)

                binding.clItemContact.setOnClickListener {
                    onClick(user)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {

       val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemContactBinding.inflate(
             inflater,parent,false
       )
        return ContactsViewHolder(itemView)
    }



    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
val users = listContacs[position]
        holder.bind(users)
    }
    override fun getItemCount(): Int {
        return listContacs.size
    }
}