package com.example.barbershopcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.barbershopcompose.navigation.SetupNavGraph
import com.example.barbershopcompose.ui.theme.BarberShopComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BarberShopComposeTheme {

                SetupNavGraph()
            }
        }
    }
}