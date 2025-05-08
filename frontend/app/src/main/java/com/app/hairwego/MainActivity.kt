package com.app.hairwego

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ahmetocak.shoppingapp.presentation.designsystem.theme.HairwegoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HairwegoAppTheme {
                HairWeGoApp()
            }
        }
    }
}



