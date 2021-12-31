package com.mlyn.kamenice.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Guest(val age: Int? = null, val id: Int, val name: String?, val surname: String?): Parcelable