package com.example.chatwithfirebase.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Users(
    val id:String = "",
    val name:String = "",
    val email:String = "",
    val photos:String = ""
): Parcelable
