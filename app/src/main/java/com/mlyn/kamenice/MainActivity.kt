package com.mlyn.kamenice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.Toast
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException

class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private val apolloClient: ApolloClient = ApolloClient.builder()
        .serverUrl("https://kamenice.pythonanywhere.com/api")
        .build()

    // https://github.com/AppliKeySolutions/CosmoCalendar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarView = findViewById(R.id.reservationsCalendar)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            Toast.makeText(
                this@MainActivity,
                "Year $year, month $month, day $dayOfMonth clicked",
                Toast.LENGTH_SHORT
            ).show()
        }

        try {
            apolloClient.query(SettingsQuery()).enqueue(
                object : ApolloCall.Callback<SettingsQuery.Data>() {
                    override fun onResponse(response: Response<SettingsQuery.Data>) {
                        Log.d("MainActivity", response.toString())
                    }

                    override fun onFailure(e: ApolloException) {
                        Log.d("MainActivity", e.message.toString())
                    }
                }
            )
        } catch (e: ApolloException) {
            Log.d("MainActivity", e.toString())
        }
    }
}
