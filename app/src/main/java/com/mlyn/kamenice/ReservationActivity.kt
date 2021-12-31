package com.mlyn.kamenice

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ModeEdit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.api.Optional
import com.google.android.material.datepicker.MaterialDatePicker
import com.mlyn.kamenice.configuration.AppConstants.Companion.DATE_FORMAT
import com.mlyn.kamenice.configuration.AppConstants.Companion.DATE_ZONE
import com.mlyn.kamenice.configuration.AppConstants.Companion.EXTRA_GUESTS
import com.mlyn.kamenice.configuration.AppConstants.Companion.EXTRA_RESERVATION
import com.mlyn.kamenice.data.Guest
import com.mlyn.kamenice.type.ReservationInput
import com.mlyn.kamenice.ui.components.LoadingIndicator
import com.mlyn.kamenice.data.Reservation
import com.mlyn.kamenice.ui.components.GuestsDropdown
import com.mlyn.kamenice.ui.components.ModalDialog
import com.mlyn.kamenice.ui.theme.AppTheme
import com.mlyn.kamenice.ui.theme.B400
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

class ReservationActivity : BaseActivity() {

    private var reservation: Reservation? = null
    private var guests: List<Guest> = listOf()
    private var selectedGuest: Guest? = null
    private var roommates: List<Guest>? = null
    private val isReservationUpdating = mutableStateOf(false)
    private val openDialog = mutableStateOf(false)
    private val dialogMessage = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        reservation = intent.getParcelableExtra(EXTRA_RESERVATION)
        guests = intent.getParcelableArrayListExtra(EXTRA_GUESTS)!!
        selectedGuest = reservation?.guest
        roommates = reservation?.roommates

        if (reservation == null) {
            redirectTo(MainActivity::class.java)
        }

        setContent {
            AppTheme {
                Scaffold(
                    bottomBar = {
                        BottomAppBar {

                        }
                    }) {
                    Surface {
                        when {
                            isReservationUpdating.value -> LoadingIndicator()
                            else -> Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                ReservationTitle()
                                ReservationForm()
                                ModalDialog(
                                    message = dialogMessage.value,
                                    visible = openDialog.value
                                ) {
                                    openDialog.value = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        redirectTo(MainActivity::class.java)
    }

    @Composable
    private fun ReservationTitle() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, top = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column {
                reservation?.guest?.let { it ->
                    Text(
                        fontSize = 20.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        text = "%s %s".format(
                            it.name,
                            it.surname
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun ReservationForm() {
        val activity = LocalContext.current as AppCompatActivity
        val fromDate = remember {
            mutableStateOf(
                LocalDateTime.parse(reservation?.fromDate).atZone(
                    ZoneId.of(DATE_ZONE)
                )
            )
        }
        val toDate = remember {
            mutableStateOf(
                LocalDateTime.parse(reservation?.toDate).atZone(
                    ZoneId.of(DATE_ZONE)
                )
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.padding(bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(end = 5.dp),
                        text = "%s - %s".format(
                            fromDate.value.format(
                                DateTimeFormatter.ofPattern(DATE_FORMAT)
                            ), toDate.value.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
                        )
                    )
                    IconButton(
                        onClick = {
                            val picker =
                                MaterialDatePicker.Builder.dateRangePicker()
                                    .setTitleText(R.string.select_reservation_range)
                                    .setSelection(
                                        Pair(
                                            fromDate.value.toInstant().toEpochMilli(),
                                            toDate.value.toInstant().toEpochMilli()
                                        )
                                    ).build()
                            picker.addOnPositiveButtonClickListener {
                                fromDate.value = Instant.ofEpochMilli(it.first)
                                    .atZone(ZoneId.of(DATE_ZONE))
                                toDate.value = Instant.ofEpochMilli(it.second)
                                    .atZone(ZoneId.of(DATE_ZONE))
                            }
                            activity.let {
                                picker.show(it.supportFragmentManager, picker.toString())
                            }
                        }
                    ) {
                        Icon(
                            Icons.Outlined.ModeEdit,
                            contentDescription = stringResource(id = R.string.edit)
                        )
                    }
                }
                Row(modifier = Modifier.padding(start = 60.dp, end = 60.dp, bottom = 20.dp)) {
                    GuestsDropdown(guests = guests, onSelect = {
                        selectedGuest = it
                    })
                }
                // Submit button
                Row {
                    OutlinedButton(
                        border = BorderStroke(1.dp, B400),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = B400),
                        onClick = {
                            isReservationUpdating.value = true
                            val from = fromDate.value.withHour(15).toLocalDateTime()
                            val to = toDate.value.withHour(10).toLocalDateTime()
                            Log.d(
                                "ReservationActivity",
                                "ReservationForm: ${reservation?.roommates}"
                            )

                            updateReservation(
                                ReservationInput(
                                    expired = Optional.presentIfNotNull(reservation?.expired),
                                    fromDate = Optional.presentIfNotNull(from.toString()),
                                    guestId = Optional.presentIfNotNull(selectedGuest?.id),
                                    id = Optional.presentIfNotNull(reservation?.id),
                                    meal = Optional.presentIfNotNull(reservation?.meal.let { it?.toString() }),
                                    notes = Optional.presentIfNotNull(reservation?.notes),
                                    payingGuestId = Optional.presentIfNotNull(reservation?.payingGuest?.id),
                                    priceAccommodation = Optional.presentIfNotNull(reservation?.priceAccommodation),
                                    priceMeal = Optional.presentIfNotNull(reservation?.priceMeal),
                                    priceMunicipality = Optional.presentIfNotNull(reservation?.priceMunicipality),
                                    priceTotal = Optional.presentIfNotNull(reservation?.priceTotal),
                                    purpose = Optional.presentIfNotNull(reservation?.purpose),
                                    roommateIds = Optional.presentIfNotNull(roommates?.map { it.id }),
                                    suiteId = Optional.presentIfNotNull(reservation?.suite?.id),
                                    toDate = Optional.presentIfNotNull(to.toString()),
                                    type = Optional.presentIfNotNull(reservation?.type.toString())
                                )
                            )
                        },
                        shape = CircleShape
                    ) {
                        Text(text = stringResource(id = R.string.save))
                    }
                }
            }
        }
    }

    private fun updateReservation(input: ReservationInput) {
        lifecycleScope.launch {
            try {
                val response = apolloClient().mutation(UpdateReservationMutation(input)).execute()
                if (response.errors?.isNotEmpty() == true) {
                    dialogMessage.value = response.errors!![0].message
                    openDialog.value = true
                    Log.d("ReservationActivity", "ReservationForm: ${response.errors!![0]}")
                }
                isReservationUpdating.value = false
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}