package com.example.barbershopcompose.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barbershopcompose.R
import com.example.barbershopcompose.components.ServicoCard
import com.example.barbershopcompose.model.Servico
import com.example.barbershopcompose.ui.theme.BackgroundBlack
import com.example.barbershopcompose.ui.theme.SurfaceGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAgendarClick: () -> Unit,
    onMenuClick: () -> Unit, // Mudei para bater com o NavGraph
    onAgendamentosClick: () -> Unit
) {
    val listaServicos = listOf(
        Servico("Corte kids (0 a 6 anos)", "R$ 50,00", "90 min", R.drawable.tipocorte),
        Servico("Barba modelada", "R$ 40,00", "45 min", R.drawable.bigode),
        Servico("Corte degradê + barba", "R$ 80,00", "120 min", R.drawable.tesoura),
        Servico("Corte degradê", "R$ 60,00", "60 min", R.drawable.lavagem),
        Servico("Sobrancelha", "R$ 20,00", "20 min", R.drawable.tratamento)
    )

    var searchTexto by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = SurfaceGray) {
                // ITEM INÍCIO
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Já estamos no início */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.White) },
                    label = { Text("Início", color = Color.White) }
                )
                // ITEM AGENDAMENTOS
                NavigationBarItem(
                    selected = false,
                    onClick = onAgendamentosClick, // USANDO A FUNÇÃO CORRETA
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray) },
                    label = { Text("Agendamentos", color = Color.Gray) }
                )
                // ITEM MENU (PERFIL)
                NavigationBarItem(
                    selected = false,
                    onClick = onMenuClick, // AQUI ESTAVA O ERRO! AGORA CHAMA O PERFIL
                    icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray) },
                    label = { Text("Menu", color = Color.Gray) }
                )
            }
        },
        containerColor = BackgroundBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fotoperfil),
                    contentDescription = "Perfil",
                    modifier = Modifier.size(45.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text("BARBERSHOP", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
            }

            Text("Bem vindo, Ricardo", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de Pesquisa
            TextField(
                value = searchTexto,
                onValueChange = { searchTexto = it },
                placeholder = { Text("Pesquisar serviços", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceGray,
                    unfocusedContainerColor = SurfaceGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Todos os serviços", color = Color.White, fontWeight = FontWeight.SemiBold)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(listaServicos) { servico ->
                    ServicoCard(servico = servico, onAgendarClick = onAgendarClick)
                }
            }
        }
    }
}