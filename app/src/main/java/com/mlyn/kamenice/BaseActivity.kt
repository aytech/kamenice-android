package com.mlyn.kamenice

import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.mlyn.kamenice.configuration.AppConstants.Companion.GRAPHQL_URL
import com.mlyn.kamenice.configuration.AppConstants.Companion.SHARED_PREFERENCES_KEY
import okhttp3.OkHttpClient

open class BaseActivity : AppCompatActivity() {
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

    fun <T> redirectTo(
        activity: Class<T>,
        extra: Map<String, Parcelable>? = null,
        arrayExtra: Map<String, ArrayList<Parcelable>>? = null
    ) {
        runOnUiThread {
            val intent = Intent(this, activity)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            extra?.map { (key, value) -> intent.putExtra(key, value) }
            arrayExtra?.map { (key, value) -> intent.putParcelableArrayListExtra(key, value) }
            startActivity(intent)
            finish()
        }
    }
}