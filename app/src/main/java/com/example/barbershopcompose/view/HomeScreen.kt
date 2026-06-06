package com.example.barbershopcompose.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch // Importante para abrir e fechar o menu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAgendarClick: (String) -> Unit,
    onMenuClick: () -> Unit,
    onAgendamentosClick: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var nomeUsuario by remember { mutableStateOf("...") }

    // --- ESTADOS DO MENU LATERAL (DRAWER) ---
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Busca o nome no Firestore assim que a tela inicia
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        nomeUsuario = document.getString("nome") ?: "Usuário"
                    }
                }
        }
    }

    val listaServicos = listOf(
        Servico("Corte kids (0 a 6 anos)", "R$ 50,00", "90 min", R.drawable.tipocorte),
        Servico("Barba modelada", "R$ 40,00", "45 min", R.drawable.bigode),
        Servico("Corte degradê + barba", "R$ 80,00", "120 min", R.drawable.tesoura),
        Servico("Corte degradê", "R$ 60,00", "60 min", R.drawable.lavagem),
        Servico("Sobrancelha", "R$ 20,00", "20 min", R.drawable.tratamento)
    )

    var searchTexto by remember { mutableStateOf("") }

    // MUDANÇA: Envolver o Scaffold com ModalNavigationDrawer para criar o Menu Lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = SurfaceGray, // Usando a cor cinza do seu tema
                modifier = Modifier.width(280.dp)
            ) {
                Spacer(Modifier.height(24.dp))

                // Cabeçalho do Menu Lateral
                Column(modifier = Modifier.padding(16.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.fotoperfil),
                        contentDescription = "Perfil",
                        modifier = Modifier.size(80.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(nomeUsuario, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(auth.currentUser?.email ?: "", color = Color.LightGray, fontSize = 14.sp)
                }

                HorizontalDivider(color = Color.DarkGray, thickness = 1.dp)

                Spacer(Modifier.height(16.dp))

                // Opção 1: Meu Perfil
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) },
                    label = { Text("Meu Perfil", color = Color.White, fontSize = 16.sp) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() } // Fecha o menu lateral
                        onMenuClick() // Navega para a tela de Perfil
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                Spacer(Modifier.height(8.dp))

                // Opção 2: Meus Agendamentos
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White) },
                    label = { Text("Meus Agendamentos", color = Color.White, fontSize = 16.sp) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() } // Fecha o menu lateral
                        onAgendamentosClick() // Navega para a tela de Agendamentos
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
            }
        }
    ) {
        // Todo o seu código original continua aqui dentro do Scaffold
        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = SurfaceGray) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { /* Início */ },
                        icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.White) },
                        label = { Text("Início", color = Color.White) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onAgendamentosClick,
                        icon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray) },
                        label = { Text("Agendamentos", color = Color.Gray) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onMenuClick,
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

                    // MUDANÇA AQUI: Transformamos o ícone em um botão funcional!
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } }, // Ação de abrir o Menu Lateral
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu Lateral", tint = Color.White, modifier = Modifier.fillMaxSize())
                    }
                }

                Text("Bem vindo, $nomeUsuario", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

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
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Todos os serviços", color = Color.White, fontWeight = FontWeight.SemiBold)

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(listaServicos) { servico ->
                        ServicoCard(
                            servico = servico,
                            onAgendarClick = { onAgendarClick(servico.nome) }
                        )
                    }
                }
            }
        }
    }
}