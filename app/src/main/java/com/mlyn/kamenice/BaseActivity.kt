package com.mlyn.kamenice

import android.content.Intent
import androidx.activity.ComponentActivity
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.mlyn.kamenice.configuration.AppConstants.Companion.GRAPHQL_URL
import com.mlyn.kamenice.configuration.AppConstants.Companion.SHARED_PREFERENCES_KEY
import okhttp3.OkHttpClient

open class BaseActivity : ComponentActivity() {
    private var instance: ApolloClient? = null

    fun apolloClient(): ApolloClient {
        if (instance != null) {
            return instance!!
        }

        val okClient = OkHttpClient.Builder()
            .addInterceptor(
                RefreshTokenInterceptor(
                    preferences = applicationContext.getSharedPreferences(
                        SHARED_PREFERENCES_KEY,
                        MODE_PRIVATE
                    )
                )
            )
            .build()
        instance = ApolloClient.Builder()
            .serverUrl(GRAPHQL_URL)
            .okHttpClient(okClient)
            .build()

        return instance!!
    }

    fun <T> redirectTo(activity: Class<T>) {
        runOnUiThread {
            val intent = Intent(this, activity)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }
}