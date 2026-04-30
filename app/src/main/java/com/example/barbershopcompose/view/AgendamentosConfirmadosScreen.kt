package com.example.barbershopcompose.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
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
import com.example.barbershopcompose.data.buscarAgendamentos
import com.example.barbershopcompose.ui.theme.BackgroundBlack
import com.example.barbershopcompose.ui.theme.PrimaryBlue

@Composable
fun AgendamentosConfirmadosScreen(onBackClick: () -> Unit) {
    // 1. Criamos um "estado" para guardar a lista que virá do banco
    var listaAgendamentos by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    // 2. Assim que a tela abre, ele vai no Firebase buscar os dados reais
    LaunchedEffect(Unit) {
        buscarAgendamentos { resultado ->
            listaAgendamentos = resultado
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(16.dp)
    ) {
        // Header conforme o Figma
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                }
                Image(
                    painter = painterResource(id = R.drawable.fotoperfil),
                    contentDescription = null,
                    modifier = Modifier.size(45.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Agendamentos", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
        }

        Text("Filtrar por data", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

        // Campo de Data
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Todos os agendamentos", color = Color.LightGray)
            Icon(painterResource(id = R.drawable.lavagem), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. A LISTA AGORA É DINÂMICA E VEM DO FIREBASE!
        // LISTA DE CARDS AZUIS ATUALIZADA
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(listaAgendamentos) { agendamento ->
                // Pegando os dados do banco
                val dataStr = agendamento["data"] as? String ?: ""
                val partesData = dataStr.split("/") // Quebra "11/6/2026" em partes
                val dia = partesData.getOrNull(0) ?: "--"
                val mesNum = partesData.getOrNull(1) ?: "1"

                val servico = agendamento["servico"] as? String ?: "Serviço"
                val profissionalNome = agendamento["profissional"] as? String ?: "Profissional"
                val horario = agendamento["horario"] as? String ?: "--:--"

                // Chamando a nova função para obter a foto correta
                val fotoCorreta = obterFotoProfissional(profissionalNome)

                // Chamando o Cartão Azul atualizado
                CardAgendamentoFigma(
                    dia = dia,
                    mes = abreviarMes(mesNum),
                    servico = servico,
                    preco = "R$ 50,00",
                    horario = horario,
                    fotoProfissional = fotoCorreta, // Passando a foto correta
                    nomeProfissional = profissionalNome // Passando o nome correto
                )
            }
        }
    }
}

// Atualizamos o cartão para receber o nome do profissional dinamicamente
// Componente de cartão atualizado para receber a foto e o nome dinâmicos
@Composable
fun CardAgendamentoFigma(
    dia: String,
    mes: String,
    servico: String,
    preco: String,
    horario: String,
    fotoProfissional: Int, // Novo parâmetro: ID da foto
    nomeProfissional: String // Novo parâmetro: Nome do barbeiro
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryBlue, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(servico, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Image(
                    // AGORA A FOTO É DINÂMICA
                    painter = painterResource(id = fotoProfissional),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop // Mantém a foto circular
                )
                Spacer(modifier = Modifier.width(8.dp))
                // NOME DO BARBEIRO DINÂMICO
                Text(nomeProfissional, color = Color.LightGray, fontSize = 14.sp)
            }

            Text("A partir de:", color = Color.LightGray, fontSize = 11.sp)
            Text(preco, color = Color(0xFF00FF00), fontWeight = FontWeight.Bold)

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(horario, color = Color.White, fontSize = 11.sp)
            }
        }

        // Bloco da Data Lateral
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(dia, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Text(mes, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}




// Funçãozinha para transformar "6" em "JUN" e ficar bonito igual no Figma
fun abreviarMes(mes: String): String {
    return when (mes) {
        "1", "01" -> "JAN"
        "2", "02" -> "FEV"
        "3", "03" -> "MAR"
        "4", "04" -> "ABR"
        "5", "05" -> "MAI"
        "6", "06" -> "JUN"
        "7", "07" -> "JUL"
        "8", "08" -> "AGO"
        "9", "09" -> "SET"
        "10" -> "OUT"
        "11" -> "NOV"
        "12" -> "DEZ"
        else -> "MÊS"
    }
}
// Função para obter o ID da foto correta baseado no nome do barbeiro
fun obterFotoProfissional(nome: String): Int {
    return when (nome) {
        "Oliveira" -> R.drawable.bigode
        "Ribeiro" -> R.drawable.tesoura
        "Ricardinho" -> R.drawable.fotoperfil
        else -> R.drawable.fotoperfil // Foto padrão caso o nome não corresponda
    }
}