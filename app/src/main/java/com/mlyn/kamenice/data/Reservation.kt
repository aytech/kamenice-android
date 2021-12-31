package com.mlyn.kamenice.data

import android.os.Parcelable
import com.mlyn.kamenice.type.ReservationMeal
import com.mlyn.kamenice.type.ReservationType
import kotlinx.parcelize.Parcelize

@Parcelize
class Reservation(
    val expired: String? = null,
    val fromDate: String,
    val guest: Guest,
    val id: String,
    val meal: ReservationMeal? = null,
    val notes: String? = null,
    val payingGuest: Guest? = null,
    val priceAccommodation: String? = null,
    val priceMeal: String? = null,
    val priceMunicipality: String? = null,
    val priceTotal: String? = null,
    val purpose: String? = null,
    val roommates: List<Guest>? = null,
    val suite: Suite,
    val toDate: String,
    val type: ReservationType
): Parcelable