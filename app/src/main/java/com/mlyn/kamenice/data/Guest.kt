package com.mlyn.kamenice.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Guest(
    val age: Int? = null,
    val id: String,
    val name: String? = null,
    val surname: String? = null
) : Parcelable