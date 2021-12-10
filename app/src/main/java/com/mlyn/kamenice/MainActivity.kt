package com.mlyn.kamenice

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.mlyn.kamenice.configuration.AppConstants
import com.mlyn.kamenice.configuration.AppConstants.Companion.SHARED_PREFERENCES_KEY
import com.mlyn.kamenice.configuration.AppConstants.Companion.USER_TOKEN
import com.mlyn.kamenice.ui.calendar.CalendarEvent
import com.mlyn.kamenice.ui.calendar.CalendarSection
import com.mlyn.kamenice.ui.calendar.ScheduleCalendar
import com.mlyn.kamenice.ui.calendar.rememberScheduleCalendarState
import com.mlyn.kamenice.ui.theme.*
import org.threeten.bp.LocalDateTime


class MainActivity : BaseActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var sharedPreferences: SharedPreferences

    // https://github.com/AppliKeySolutions/CosmoCalendar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScheduleCalendarTheme {
                Scaffold {
                    Surface {
                        AppCalendar()
                    }
                }
            }
        }
//        setContentView(R.layout.activity_main)

//        sharedPreferences =
//            applicationContext.getSharedPreferences(SHARED_PREFERENCES_KEY, MODE_PRIVATE)
//        if (sharedPreferences.getString(USER_TOKEN, null) == null) {
//            redirectToLogin()
//        }
//
//        try {
//            apolloClient(applicationContext).query(SettingsQuery()).enqueue(
//                object : ApolloCall.Callback<SettingsQuery.Data>() {
//                    override fun onResponse(response: Response<SettingsQuery.Data>) {
//                        Log.d("MainActivity", "Activity response: %s".format(response.toString()))
//                    }
//
//                    override fun onFailure(e: ApolloException) {
//                        redirectToLogin()
//                    }
//                }
//            )
//        } catch (e: ApolloException) {
//            Log.d("MainActivity", e.toString())
//        }
    }

    fun redirectToLogin() {
        runOnUiThread {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }
}

@Composable
fun AppCalendar() {
    val viewSpan = remember { mutableStateOf(48 * 3600L) }
    val eventTimesVisible = remember { mutableStateOf(true) }
    Column(modifier = Modifier.fillMaxHeight()) {
        Row {
            IconButton(onClick = {
                viewSpan.value = (viewSpan.value * 2).coerceAtMost(96 * 3600)
            }) {
                Icon(imageVector = Icons.Default.ZoomOut, contentDescription = "increase")
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = {
                viewSpan.value = (viewSpan.value / 2).coerceAtLeast(3 * 3600)
            }) {
                Icon(imageVector = Icons.Default.ZoomIn, contentDescription = "decrease")
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = {
                eventTimesVisible.value = !(eventTimesVisible.value)
            }) {
                Icon(imageVector = Icons.Default.HideImage, contentDescription = "decrease")
            }
        }

        val calendarState = rememberScheduleCalendarState()

        Spacer(modifier = Modifier.height(8.dp))

        ScheduleCalendar(
            state = calendarState,
            now = LocalDateTime.now().plusHours(8),
            eventTimesVisible = eventTimesVisible.value,
            sections = listOf(
                CalendarSection(
                    "Platform Schedule",
                    events = listOf(
                        CalendarEvent(
                            startDate = LocalDateTime.now().minusHours(6),
                            endDate = LocalDateTime.now().plusHours(12),
                            name = "Halil Ozercan",
                            description = "",
                            color = R500
                        ),
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(24),
                            endDate = LocalDateTime.now().plusHours(48),
                            name = "And Ani Calik",
                            description = "",
                            color = G500
                        )
                    )
                ),
                CalendarSection(
                    "Compose Schedule",
                    events = listOf(
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(6),
                            endDate = LocalDateTime.now().plusHours(12),
                            name = "Halil Ozercan",
                            description = "",
                            color = Y500
                        ),
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(17),
                            endDate = LocalDateTime.now().plusHours(27),
                            name = "Taha Kirca",
                            description = "",
                            color = B400
                        )
                    )
                ), CalendarSection(
                    "Compose Schedule",
                    events = listOf(
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(6),
                            endDate = LocalDateTime.now().plusHours(12),
                            name = "Halil Ozercan",
                            description = "",
                            color = Y500
                        ),
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(17),
                            endDate = LocalDateTime.now().plusHours(27),
                            name = "Taha Kirca",
                            description = "",
                            color = B400
                        )
                    )
                ), CalendarSection(
                    "Compose Schedule",
                    events = listOf(
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(6),
                            endDate = LocalDateTime.now().plusHours(12),
                            name = "Halil Ozercan",
                            description = "",
                            color = Y500
                        ),
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(17),
                            endDate = LocalDateTime.now().plusHours(27),
                            name = "Taha Kirca",
                            description = "",
                            color = B400
                        )
                    )
                ), CalendarSection(
                    "Compose Schedule",
                    events = listOf(
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(6),
                            endDate = LocalDateTime.now().plusHours(12),
                            name = "Halil Ozercan",
                            description = "",
                            color = Y500
                        ),
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(17),
                            endDate = LocalDateTime.now().plusHours(27),
                            name = "Taha Kirca",
                            description = "",
                            color = B400
                        )
                    )
                ), CalendarSection(
                    "Compose Schedule",
                    events = listOf(
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(6),
                            endDate = LocalDateTime.now().plusHours(12),
                            name = "Halil Ozercan",
                            description = "",
                            color = Y500
                        ),
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(17),
                            endDate = LocalDateTime.now().plusHours(27),
                            name = "Taha Kirca",
                            description = "",
                            color = B400
                        )
                    )
                ), CalendarSection(
                    "Compose Schedule",
                    events = listOf(
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(6),
                            endDate = LocalDateTime.now().plusHours(12),
                            name = "Halil Ozercan",
                            description = "",
                            color = Y500
                        ),
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(17),
                            endDate = LocalDateTime.now().plusHours(27),
                            name = "Taha Kirca",
                            description = "",
                            color = B400
                        )
                    )
                ), CalendarSection(
                    "Compose Schedule",
                    events = listOf(
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(6),
                            endDate = LocalDateTime.now().plusHours(12),
                            name = "Halil Ozercan",
                            description = "",
                            color = Y500
                        ),
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(17),
                            endDate = LocalDateTime.now().plusHours(27),
                            name = "Taha Kirca",
                            description = "",
                            color = B400
                        )
                    )
                ), CalendarSection(
                    "Compose Schedule",
                    events = listOf(
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(6),
                            endDate = LocalDateTime.now().plusHours(12),
                            name = "Halil Ozercan",
                            description = "",
                            color = Y500
                        ),
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(17),
                            endDate = LocalDateTime.now().plusHours(27),
                            name = "Taha Kirca",
                            description = "",
                            color = B400
                        )
                    )
                ), CalendarSection(
                    "Compose Schedule",
                    events = listOf(
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(6),
                            endDate = LocalDateTime.now().plusHours(12),
                            name = "Halil Ozercan",
                            description = "",
                            color = Y500
                        ),
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(17),
                            endDate = LocalDateTime.now().plusHours(27),
                            name = "Taha Kirca",
                            description = "",
                            color = B400
                        )
                    )
                ), CalendarSection(
                    "Compose Schedule",
                    events = listOf(
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(6),
                            endDate = LocalDateTime.now().plusHours(12),
                            name = "Halil Ozercan",
                            description = "",
                            color = Y500
                        ),
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(17),
                            endDate = LocalDateTime.now().plusHours(27),
                            name = "Taha Kirca",
                            description = "",
                            color = B400
                        )
                    )
                ), CalendarSection(
                    "Compose Schedule",
                    events = listOf(
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(6),
                            endDate = LocalDateTime.now().plusHours(12),
                            name = "Halil Ozercan",
                            description = "",
                            color = Y500
                        ),
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(17),
                            endDate = LocalDateTime.now().plusHours(27),
                            name = "Taha Kirca",
                            description = "",
                            color = B400
                        )
                    )
                ), CalendarSection(
                    "Compose Schedule",
                    events = listOf(
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(6),
                            endDate = LocalDateTime.now().plusHours(12),
                            name = "Halil Ozercan",
                            description = "",
                            color = Y500
                        ),
                        CalendarEvent(
                            startDate = LocalDateTime.now().plusHours(17),
                            endDate = LocalDateTime.now().plusHours(27),
                            name = "Taha Kirca",
                            description = "",
                            color = B400
                        )
                    )
                )
            ),
            viewSpan = viewSpan.value
        )
    }
}
