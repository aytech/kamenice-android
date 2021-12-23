package com.mlyn.kamenice

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mlyn.kamenice.configuration.AppConstants.Companion.SHARED_PREFERENCES_KEY
import com.mlyn.kamenice.configuration.AppConstants.Companion.USER_TOKEN
import com.mlyn.kamenice.type.ReservationType
import com.mlyn.kamenice.ui.calendar.CalendarEvent
import com.mlyn.kamenice.ui.calendar.CalendarSection
import com.mlyn.kamenice.ui.calendar.ScheduleCalendar
import com.mlyn.kamenice.ui.calendar.rememberScheduleCalendarState
import com.mlyn.kamenice.ui.components.LoadingIndicator
import com.mlyn.kamenice.ui.theme.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime


class MainActivity : BaseActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val reservations: MutableList<SuitesReservationsQuery.Reservation?> = mutableListOf()
    private var sections: MutableList<CalendarSection> = mutableListOf()
    private var timelineStart: LocalDateTime = LocalDateTime.now().minusHours(24)
    private var timelineEnd: LocalDateTime = LocalDateTime.now().plusHours(24)
    private val isPageLoading = mutableStateOf(true)
    private val isPageRefreshing = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Scaffold(
                    bottomBar = {
                        BottomAppBar {

                        }
                    }
                ) {
                    Surface {
                        when {
                            isPageLoading.value -> LoadingIndicator()
                            else -> SwipeRefresh(
                                state = rememberSwipeRefreshState(isRefreshing = isPageRefreshing.value),
                                onRefresh = {
                                    loadReservationsSubset(
                                        timelineStart,
                                        timelineEnd
                                    )
                                }) {
                                AppCalendar()
                            }
                        }

                    }
                }
            }
        }

        sharedPreferences =
            applicationContext.getSharedPreferences(SHARED_PREFERENCES_KEY, MODE_PRIVATE)
        if (sharedPreferences.getString(USER_TOKEN, null) == null) {
            redirectTo(LoginActivity::class.java)
        }

        loadReservationsSubset()
    }

    @Composable
    fun AppCalendar() {
        val viewSpan = remember { mutableStateOf(48 * 3600L) }
        val eventTimesVisible = remember { mutableStateOf(true) }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 20.dp)
        ) {
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

            val calendarState = rememberScheduleCalendarState(onScreenSelected = {
                loadReservationsSubset(it.start, it.end)
            })

            Spacer(modifier = Modifier.height(8.dp))

            ScheduleCalendar(
                state = calendarState,
                now = LocalDateTime.now(),
                eventTimesVisible = eventTimesVisible.value,
                sections = sections,
                viewSpan = viewSpan.value,
                onEventSelected = { openReservation(it) }
            )
        }
    }

    private fun openReservation(reservationId: String) {
        val reservation = reservations.find { reservation -> reservation?.id == reservationId }
        if (reservation != null) {
            redirectTo(ReservationActivity::class.java)
        }
    }

    private fun loadReservationsSubset(
        start: LocalDateTime = timelineStart,
        end: LocalDateTime = timelineEnd
    ) {

        val requestStart: LocalDateTime
        val requestEnd: LocalDateTime

        isPageRefreshing.value = true
        when {
            start < timelineStart -> {
                requestStart = start
                requestEnd = timelineStart
            }
            end > timelineEnd -> {
                requestStart = timelineEnd
                requestEnd = end
            }
            else -> {
                requestStart = timelineStart
                requestEnd = timelineEnd
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                try {
                    val reservationsQuery = apolloClient().query(
                        SuitesReservationsQuery(requestStart.toString(), requestEnd.toString())
                    ).execute()
                    val allReservations = reservationsQuery.data?.reservations
                    sections = reservationsQuery.data?.suites?.map { suite ->
                        CalendarSection(
                            suite!!.title,
                            events = getSuiteEvents(allReservations, suite.id)
                        )
                    }!!.toMutableList()
                    isPageLoading.value = false
                    isPageRefreshing.value = false
                    timelineStart = requestStart
                    timelineEnd = requestEnd
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    redirectTo(LoginActivity::class.java)
                }
            }
        }, 1000)


    }

    private fun getSuiteEvents(
        reservationsData: List<SuitesReservationsQuery.Reservation?>?,
        suiteId: String
    ): List<CalendarEvent> {
        val suiteEvents: MutableList<CalendarEvent> = mutableListOf()
        if (reservationsData == null) return suiteEvents
        for (reservation in reservationsData) {
            if (reservations.find { it?.id == reservation?.id } == null) {
                reservations.add(reservation)
            }
        }
        for (reservation in reservations) {
            if (reservation?.suite?.id == suiteId) {
                suiteEvents.add(
                    CalendarEvent(
                        startDate = LocalDateTime.parse(reservation.fromDate.toString()),
                        endDate = LocalDateTime.parse(reservation.toDate.toString()),
                        id = reservation.id,
                        name = "%s %s".format(reservation.guest.name, reservation.guest.surname),
                        description = "",
                        color = getReservationColor(reservation.type)
                    )
                )
            }
        }
        return suiteEvents
    }

    private fun getReservationColor(type: ReservationType): Color {
        return when (type) {
            ReservationType.NONBINDING -> Y200
            ReservationType.BINDING -> T500
            ReservationType.INHABITED -> Y500
            ReservationType.ACCOMMODATED -> P500
            else -> Y300
        }
    }
}

data class ScreenRequest(val start: LocalDateTime, val end: LocalDateTime)