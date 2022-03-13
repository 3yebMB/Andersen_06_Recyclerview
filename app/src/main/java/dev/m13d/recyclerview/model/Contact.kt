package dev.m13d.recyclerview.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: Int,
    val photo: String,
    val name: String,
    val surname: String,
    val phone: String,
): Parcelable
