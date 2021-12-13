package com.mlyn.kamenice

import android.content.SharedPreferences
import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.mlyn.kamenice.configuration.AppConstants
import com.mlyn.kamenice.configuration.AppConstants.Companion.USER_REFRESH_TOKEN
import com.mlyn.kamenice.configuration.AppConstants.Companion.USER_TOKEN
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class RefreshTokenInterceptor(val preferences: SharedPreferences) : Interceptor {

    private var instance: ApolloClient? = null
    private var token = preferences.getString(USER_TOKEN, "")
    private var refreshToken = preferences.getString(USER_REFRESH_TOKEN, "")

    private fun apolloClient(): ApolloClient {
        if (instance != null) {
            return instance!!
        }

        instance = ApolloClient.Builder()
            .serverUrl(AppConstants.GRAPHQL_URL)
            .build()

        return instance!!
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(getAuthenticatedRequest(chain.request(), token!!))
        val responseBody = response.body!!.string() // After the first read the body is closed

        return runBlocking {
            if (isSignatureExpired(responseBody)) {
                try { // This might fail if the refresh token is expired
                    val refreshResponse =
                        apolloClient().mutation(RefreshTokenMutation(refreshToken!!)).execute()
                    if (refreshResponse.data!!.refreshToken == null) { // Refresh token has expired
                        chain.call().cancel()
                    }
                    token = refreshResponse.data?.refreshToken?.token
                    refreshToken = refreshResponse.data?.refreshToken?.refreshToken
                    with(preferences.edit()) {
                        putString(USER_TOKEN, token)
                        putString(USER_REFRESH_TOKEN, refreshToken)
                        apply()
                    }
                } catch (ex: Exception) {
                    Log.d("Apollo", "Refreshing error: $ex")
                }
                chain.proceed(
                    getAuthenticatedRequest(originalRequest = chain.request(), token = token!!)
                )
            } else {
                chain.proceed(getAuthenticatedRequest(originalRequest = chain.request(), token!!))
            }
        }
    }

    private fun getAuthenticatedRequest(originalRequest: Request, token: String): Request {
        return originalRequest.newBuilder()
            .header("Authorization", "JWT %s".format(token))
            .method(originalRequest.method, originalRequest.body)
            .build()
    }

    private fun isSignatureExpired(body: String): Boolean {
        return body.indexOf("Signature has expired") != -1
    }
}