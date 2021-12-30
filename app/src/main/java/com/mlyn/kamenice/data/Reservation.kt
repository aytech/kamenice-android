package com.mlyn.kamenice.data

import android.os.Parcelable
import com.mlyn.kamenice.type.ReservationType
import kotlinx.parcelize.Parcelize

@Parcelize
class Reservation(
    val fromDate: String,
    val guest: Guest,
    val id: String,
    val suite: Suite,
    val toDate: String,
    val type: ReservationType
): Parcelable