package com.mlyn.kamenice

import android.content.Context
import androidx.activity.ComponentActivity
import com.apollographql.apollo.ApolloClient
import com.mlyn.kamenice.configuration.AppConstants.Companion.GRAPHQL_URL

open class BaseActivity : ComponentActivity() {
    private var instance: ApolloClient? = null

    fun apolloClient(context: Context): ApolloClient {
        if (instance != null) {
            return instance!!
        }

        instance = ApolloClient.builder()
            .serverUrl(GRAPHQL_URL)
            .addApplicationInterceptor(ApolloRefreshTokenInterceptor(context))
            .build()

        return instance!!
    }
}