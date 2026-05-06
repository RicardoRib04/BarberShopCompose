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
import androidx.navigation.NavType
import androidx.navigation.navArgument

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
                // 1. Agora a HomeScreen vai enviar o nome do serviço!
                onAgendarClick = { nomeDoServico ->
                    // A rota será algo como: "agendamento/Corte Kids"
                    navController.navigate("agendamento/$nomeDoServico")
                },
                onMenuClick = { navController.navigate("perfil") },
                onAgendamentosClick = { navController.navigate("meus_agendamentos") }
            )
        }

        // 2. A rota do agendamento agora espera receber a palavra {servico}
        composable(
            route = "agendamento/{servico}",
            arguments = listOf(navArgument("servico") { type = NavType.StringType })
        ) { backStackEntry ->

            // 3. Pega a palavra que veio pela rota
            val servicoClicado = backStackEntry.arguments?.getString("servico") ?: "Serviço Padrão"

            AgendamentoScreen(
                servicoEscolhido = servicoClicado, // 4. Passa a palavra para a sua tela!
                onFinalizarClick = {
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