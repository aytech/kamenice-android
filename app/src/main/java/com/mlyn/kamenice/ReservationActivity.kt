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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import com.mlyn.kamenice.configuration.AppConstants.Companion.DATE_FORMAT
import com.mlyn.kamenice.configuration.AppConstants.Companion.DATE_ZONE
import com.mlyn.kamenice.ui.components.Reservation
import com.mlyn.kamenice.ui.theme.AppTheme
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

class ReservationActivity : BaseActivity() {

    private var reservation: Reservation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        reservation = intent.getParcelableExtra("extra")

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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            ReservationTitle()
                            ReservationForm()
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
                Row {
                    OutlinedButton(
                        border = BorderStroke(1.dp, Color.Gray),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        onClick = {
                            Log.d("ReservationActivity", "ReservationForm: Saving")
                        },
                        shape = CircleShape
                    ) {
                        Text(text = stringResource(id = R.string.save))
                    }
                }
            }
        }
    }
}