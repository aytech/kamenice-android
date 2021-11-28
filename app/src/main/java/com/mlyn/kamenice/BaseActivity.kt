package com.mlyn.kamenice

import androidx.appcompat.app.AppCompatActivity
import com.apollographql.apollo.ApolloClient

open class BaseActivity : AppCompatActivity() {
    val apolloClient: ApolloClient = ApolloClient.builder()
        .serverUrl("https://kamenice.pythonanywhere.com/api")
        .build()
}