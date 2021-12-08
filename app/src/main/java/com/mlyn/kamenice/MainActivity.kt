package com.mlyn.kamenice

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.Toast
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.mlyn.kamenice.configuration.AppConstants
import com.mlyn.kamenice.configuration.AppConstants.Companion.SHARED_PREFERENCES_KEY
import com.mlyn.kamenice.configuration.AppConstants.Companion.USER_TOKEN


class MainActivity : BaseActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var sharedPreferences: SharedPreferences

    // https://github.com/AppliKeySolutions/CosmoCalendar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences =
            applicationContext.getSharedPreferences(SHARED_PREFERENCES_KEY, MODE_PRIVATE)
        if (sharedPreferences.getString(USER_TOKEN, null) == null) {
            redirectToLogin()
        }

        try {
            apolloClient(applicationContext).query(SettingsQuery()).enqueue(
                object : ApolloCall.Callback<SettingsQuery.Data>() {
                    override fun onResponse(response: Response<SettingsQuery.Data>) {
                        Log.d("MainActivity", "Activity response: %s".format(response.toString()))
                    }

                    override fun onFailure(e: ApolloException) {
                        redirectToLogin()
                    }
                }
            )
        } catch (e: ApolloException) {
            Log.d("MainActivity", e.toString())
        }
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
