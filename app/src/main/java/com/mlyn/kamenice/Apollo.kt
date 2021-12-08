package com.mlyn.kamenice

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.interceptor.ApolloInterceptor
import com.apollographql.apollo.interceptor.ApolloInterceptorChain
import com.apollographql.apollo.request.RequestHeaders
import com.mlyn.kamenice.configuration.AppConstants
import com.mlyn.kamenice.configuration.AppConstants.Companion.SHARED_PREFERENCES_KEY
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executor

// https://github.com/apollographql/apollo-android/issues/2461
internal class ApolloRefreshTokenInterceptor(private val context: Context) : ApolloInterceptor {

    @Volatile
    private var disposed = false

    private var instance: ApolloClient? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun interceptAsync(
        request: ApolloInterceptor.InterceptorRequest,
        chain: ApolloInterceptorChain,
        dispatcher: Executor,
        callBack: ApolloInterceptor.CallBack
    ) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, MODE_PRIVATE)
        val authRequest = request.toBuilder().requestHeaders(getHeaders(request)).build()

        chain.proceedAsync(authRequest, dispatcher, object : ApolloInterceptor.CallBack {

            override fun onFailure(e: ApolloException) {
                e.printStackTrace()
                callBack.onFailure(e)
            }

            override fun onResponse(response: ApolloInterceptor.InterceptorResponse) {

                if (disposed) return

                if (isTokenExpired(response)) {
                    synchronized(this) {
                        runBlocking {
                            refreshToken(callBack, chain, dispatcher, request)
                        }
                    }
                } else {
                    callBack.onResponse(response)
                    callBack.onCompleted()
                }
            }

            override fun onFetch(sourceType: ApolloInterceptor.FetchSourceType?) {
                callBack.onFetch(sourceType)
            }

            override fun onCompleted() {
            }
        })
    }

    override fun dispose() {
        disposed = true
    }

    private fun apolloClient(): ApolloClient {
        if (instance != null) {
            return instance!!
        }

        instance = ApolloClient.builder()
            .serverUrl(AppConstants.GRAPHQL_URL)
            .build()

        return instance!!
    }

    private fun refreshToken(
        callback: ApolloInterceptor.CallBack,
        chain: ApolloInterceptorChain,
        dispatcher: Executor,
        request: ApolloInterceptor.InterceptorRequest
    ) {
        val refreshToken =
            sharedPreferences.getString(AppConstants.USER_REFRESH_TOKEN, "")
        apolloClient().mutate(RefreshTokenMutation(refreshToken!!))
            .enqueue(
                object : ApolloCall.Callback<RefreshTokenMutation.Data>() {
                    override fun onResponse(response: Response<RefreshTokenMutation.Data>) {
                        if (isRefreshTokenInvalid(response)) {
                            callback.onFailure(ApolloException("Unauthorized"))
                            return
                        } else {
                            with(sharedPreferences.edit()) {
                                putString(
                                    AppConstants.USER_TOKEN,
                                    response.data?.refreshToken?.token
                                )
                                putString(
                                    AppConstants.USER_REFRESH_TOKEN,
                                    response.data?.refreshToken?.refreshToken
                                )
                                apply()
                            }
                            return chain.proceedAsync(
                                request
                                    .toBuilder()
                                    .requestHeaders(getHeaders(request))
                                    .fetchFromCache(false)
                                    .build(),
                                dispatcher,
                                callback
                            )
                        }

                    }

                    override fun onFailure(e: ApolloException) {
                        callback.onFailure(e)
                        callback.onCompleted()
                        return
                    }
                }
            )
    }

    private fun getHeaders(request: ApolloInterceptor.InterceptorRequest): RequestHeaders {
        return request.requestHeaders.toBuilder().addHeader(
            "Authorization",
            "JWT %s".format(sharedPreferences.getString(AppConstants.USER_TOKEN, ""))
        ).build()
    }

    private fun isTokenExpired(response: ApolloInterceptor.InterceptorResponse): Boolean {
        return if (response.parsedResponse.isPresent) {
            @Suppress("UNCHECKED_CAST")
            if (response.parsedResponse.get().errors != null) {
                val errors = response.parsedResponse.get().errors as List<Error>
                return errors.find { it.message == "Signature has expired" } != null
            }
            false
        } else false
    }

    private fun isRefreshTokenInvalid(response: Response<RefreshTokenMutation.Data>): Boolean {
        val errors = response.errors
        return if (errors != null) {
            errors.find {
                it.message == "Invalid refresh token"
                        || it.message == "Refresh token is expired"
            } != null
        } else false
    }
}

