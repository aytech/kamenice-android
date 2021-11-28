package com.mlyn.kamenice

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.mlyn.kamenice.configuration.AppConstants
import com.mlyn.kamenice.configuration.AppConstants.Companion.USER_REFRESH_TOKEN
import com.mlyn.kamenice.configuration.AppConstants.Companion.USER_TOKEN

class LoginActivity : BaseActivity() {

    private lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        spinner = findViewById(R.id.spinner)
        val usernameText: EditText = findViewById(R.id.username)
        val passwordText: EditText = findViewById(R.id.password)
        val usernameEmpty: TextView = findViewById(R.id.usernameEmpty)
        val passwordEmpty: TextView = findViewById(R.id.passwordEmpty)

        findViewById<Button>(R.id.resetButton).setOnClickListener {
            usernameText.text.clear()
            passwordText.text.clear()
        }

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            val username = usernameText.text
            val password = passwordText.text

            if (username.isBlank()) {
                usernameEmpty.visibility = View.VISIBLE
            } else {
                usernameEmpty.visibility = View.GONE
            }

            if (password.isBlank()) {
                passwordEmpty.visibility = View.VISIBLE
            } else {
                passwordEmpty.visibility = View.GONE
            }

            if (username.isNotBlank() && password.isNotBlank()) {
                spinner.visibility = View.VISIBLE
                try {
                    apolloClient.mutate(
                        LoginMutation(
                            username = username.toString(),
                            password = password.toString()
                        )
                    )
                        .enqueue(
                            object : ApolloCall.Callback<LoginMutation.Data>() {
                                override fun onResponse(response: Response<LoginMutation.Data>) {
                                    val token = response.data?.tokenAuth?.token
                                    val refreshToken = response.data?.tokenAuth?.refreshToken
                                    if (token == null || refreshToken == null) {
                                        AlertDialog.Builder(this@LoginActivity)
                                            .setTitle(resources.getString(R.string.login_failed))
                                            .setMessage(resources.getString(R.string.login_invalid))
                                            .show()
                                    } else {
                                        val sharedPreferences =
                                            applicationContext.getSharedPreferences(
                                                AppConstants.SHARED_PREFERENCES_KEY,
                                                MODE_PRIVATE
                                            )
                                        with(sharedPreferences.edit()) {
                                            putString(USER_TOKEN, token)
                                            putString(USER_REFRESH_TOKEN, refreshToken)
                                            apply()
                                        }
                                        redirectToMain()
                                    }
                                    updateSpinnerVisibility(View.GONE)
                                }

                                override fun onFailure(e: ApolloException) {
                                    Log.d("LoginActivity", e.message.toString())
                                    updateSpinnerVisibility(View.GONE)
                                }
                            }
                        )
                } catch (e: ApolloException) {
                    Log.d("LoginActivity", e.toString())
                }
            }
        }
    }

    fun updateSpinnerVisibility(state: Int) {
        runOnUiThread { spinner.visibility = state }
    }

    fun redirectToMain() {
        runOnUiThread {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }
}