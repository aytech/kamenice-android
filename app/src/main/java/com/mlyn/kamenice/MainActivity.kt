package com.mlyn.kamenice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView

    // https://github.com/AppliKeySolutions/CosmoCalendar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarView = findViewById(R.id.reservationsCalendar)
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            Toast.makeText(
                this@MainActivity,
                "Year $year, month $month, day $dayOfMonth clicked",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}