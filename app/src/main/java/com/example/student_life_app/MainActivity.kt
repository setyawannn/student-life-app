package com.example.student_life_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.student_life_app.core.navigation.MainAppContainer
import com.example.student_life_app.ui.theme.StudentlifeappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentlifeappTheme {
                MainAppContainer()
            }
        }
    }
}
