package com.mlyn.kamenice

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import com.mlyn.kamenice.ui.theme.AppTheme

class ReservationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Scaffold(
                    bottomBar = {
                        BottomAppBar {

                        }
                    }) {
                    Surface {

                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        redirectTo(MainActivity::class.java)
    }
}