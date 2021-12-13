package com.mlyn.kamenice

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mlyn.kamenice.configuration.AppConstants
import com.mlyn.kamenice.configuration.AppConstants.Companion.USER_REFRESH_TOKEN
import com.mlyn.kamenice.configuration.AppConstants.Companion.USER_TOKEN
import com.mlyn.kamenice.ui.components.LoadingIndicator
import com.mlyn.kamenice.ui.theme.AppTheme
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {

    private val openDialog = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ActivityContent()
        }
    }

    @Composable
    fun ActivityContent() {

        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()
        val dialogMessage = remember { mutableStateOf("") }
        val loading = remember { mutableStateOf(false) }
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        AppTheme {
            Scaffold(
                scaffoldState = scaffoldState
            ) {
                Surface {
                    when {
                        loading.value -> LoadingIndicator()
                        else -> Column(modifier = Modifier.fillMaxHeight()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column {
                                    Text(
                                        text = stringResource(id = R.string.app_name),
                                        fontSize = 20.sp,
                                        fontStyle = FontStyle.Italic,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_logo_100),
                                        contentDescription = stringResource(id = R.string.app_name),
                                        Modifier.padding(top = 10.dp)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column {
                                    OutlinedTextField(
                                        value = username,
                                        onValueChange = { username = it },
                                        label = { Text(stringResource(id = R.string.username)) },
                                        modifier = Modifier.padding(top = 10.dp)
                                    )
                                    OutlinedTextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        label = { Text(stringResource(id = R.string.password)) },
                                        modifier = Modifier.padding(top = 10.dp),
                                        visualTransformation = PasswordVisualTransformation()
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(onClick = {
                                    when {
                                        username.isEmpty() -> {
                                            dialogMessage.value =
                                                resources.getText(R.string.enter_username)
                                                    .toString()
                                            openDialog.value = true
                                        }
                                        password.isEmpty() -> {
                                            dialogMessage.value =
                                                resources.getText(R.string.enter_password)
                                                    .toString()
                                            openDialog.value = true
                                        }
                                        else ->
                                            scope.launch {
                                                loading.value = true
                                                try {
                                                    val data = apolloClient().mutation(
                                                        LoginMutation(
                                                            username = username,
                                                            password = password
                                                        )
                                                    ).execute()
                                                    if (data.data?.tokenAuth == null) {
                                                        dialogMessage.value =
                                                            resources.getText(R.string.login_failed)
                                                                .toString()
                                                        openDialog.value = true
                                                    } else {
                                                        val sharedPreferences =
                                                            applicationContext.getSharedPreferences(
                                                                AppConstants.SHARED_PREFERENCES_KEY,
                                                                MODE_PRIVATE
                                                            )
                                                        with(sharedPreferences.edit()) {
                                                            putString(
                                                                USER_TOKEN,
                                                                data.data?.tokenAuth?.token
                                                            )
                                                            putString(
                                                                USER_REFRESH_TOKEN,
                                                                data.data?.tokenAuth?.refreshToken
                                                            )
                                                            apply()
                                                        }
                                                        redirectToMain()
                                                    }
                                                } catch (exception: Exception) {
                                                    dialogMessage.value =
                                                        resources.getText(R.string.network_error)
                                                            .toString()
                                                    openDialog.value = true
                                                }
                                                loading.value = false
                                            }
                                    }
                                }) {
                                    Text(text = "Submit")
                                }
                            }
                            if (openDialog.value) {
                                AlertDialog(
                                    onDismissRequest = {
                                        // Dismiss the dialog when the user clicks outside the dialog or on the back
                                        // button. If you want to disable that functionality, simply use an empty
                                        // onCloseRequest.
                                        openDialog.value = false
                                    },
                                    text = {
                                        Text(text = dialogMessage.value)
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                openDialog.value = false
                                            }) {
                                            Text(stringResource(id = R.string.close))
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun redirectToMain() {
        runOnUiThread {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }
}