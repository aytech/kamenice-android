package com.mlyn.kamenice.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Guest(val id: String, val name: String, val surname: String): Parcelable