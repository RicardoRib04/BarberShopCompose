package com.example.barbershopcompose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.barbershopcompose.view.LoginScreen
import com.example.barbershopcompose.view.HomeScreen
import com.example.barbershopcompose.view.AgendamentoScreen
import com.example.barbershopcompose.view.PerfilScreen
import com.example.barbershopcompose.view.AgendamentosConfirmadosScreen // Importe a tela nova

@Composable
fun SetupNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(onLoginClick = {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        composable("home") {
            HomeScreen(
                onAgendarClick = { navController.navigate("agendamento") },
                onMenuClick = { navController.navigate("perfil") },
                // AGORA APONTA PARA A TELA DE LISTA:
                onAgendamentosClick = { navController.navigate("meus_agendamentos") }
            )
        }

        composable("agendamento") {
            AgendamentoScreen(
                onFinalizarClick = {
                    // Após sucesso, vai direto para a lista de agendamentos
                    navController.navigate("meus_agendamentos") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("perfil") {
            PerfilScreen(onBackClick = { navController.popBackStack() })
        }

        // NOVA ROTA PARA A TELA QUE VOCÊ MOSTROU NO FIGMA
        composable("meus_agendamentos") {
            AgendamentosConfirmadosScreen(onBackClick = { navController.popBackStack() })
        }
    }
}