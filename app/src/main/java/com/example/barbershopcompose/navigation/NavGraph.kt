package com.example.barbershopcompose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.barbershopcompose.view.LoginScreen
import com.example.barbershopcompose.view.HomeScreen
import com.example.barbershopcompose.view.AgendamentoScreen
import com.example.barbershopcompose.view.PerfilScreen
import com.example.barbershopcompose.view.AgendamentosConfirmadosScreen
import com.example.barbershopcompose.view.RegistroScreen
import com.example.barbershopcompose.view.GerenciarProfissionaisScreen

@Composable
fun SetupNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginClick = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("registrar") {
            RegistroScreen(
                onBackClick = { navController.popBackStack() },
                onRegistroSuccess = {
                    navController.navigate("login") {
                        popUpTo("registrar") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onAgendarClick = { nomeDoServico ->
                    navController.navigate("agendamento/$nomeDoServico")
                },
                onMenuClick = { navController.navigate("perfil") },
                onAgendamentosClick = { navController.navigate("meus_agendamentos") }
            )
        }

        composable(
            route = "agendamento/{servico}",
            arguments = listOf(navArgument("servico") { type = NavType.StringType })
        ) { backStackEntry ->
            val servicoClicado = backStackEntry.arguments?.getString("servico") ?: "Serviço Padrão"

            AgendamentoScreen(
                servicoEscolhido = servicoClicado,
                onFinalizarClick = {
                    navController.navigate("meus_agendamentos") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("perfil") {
            PerfilScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onGerenciarProfissionaisClick = {
                    navController.navigate("gerenciar_profissionais")
                }
            )
        }

        composable("meus_agendamentos") {
            AgendamentosConfirmadosScreen(onBackClick = { navController.popBackStack() })
        }

        composable("gerenciar_profissionais") {
            GerenciarProfissionaisScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}