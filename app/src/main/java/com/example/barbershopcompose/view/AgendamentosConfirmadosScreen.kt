package com.example.barbershopcompose.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.barbershopcompose.ui.theme.BackgroundBlack
import com.example.barbershopcompose.ui.theme.PrimaryBlue

@Composable
fun AgendamentosConfirmadosScreen(onBackClick: () -> Unit) {
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

        // Campo de Data Simulado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("11/06/2026", color = Color.LightGray)
            Icon(painterResource(id = R.drawable.lavagem), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // LISTA DE CARDS AZUIS (IGUAL AO FIGMA)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                CardAgendamentoFigma(
                    dia = "11",
                    mes = "ABR",
                    servico = "Corte kids (0 a 6 anos)",
                    preco = "R$ 50,00",
                    horario = "08:00 - 09:30 (90 min)"
                )
            }
            item {
                CardAgendamentoFigma(
                    dia = "15",
                    mes = "ABR",
                    servico = "Corte kids (0 a 6 anos)",
                    preco = "R$ 50,00",
                    horario = "08:00 - 09:30 (90 min)"
                )
            }
        }
    }
}

@Composable
fun CardAgendamentoFigma(dia: String, mes: String, servico: String, preco: String, horario: String) {
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
                    painter = painterResource(id = R.drawable.fotoperfil),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ricardinho", color = Color.LightGray, fontSize = 14.sp)
            }

            Text("A partir de:", color = Color.LightGray, fontSize = 11.sp)
            Text(preco, color = Color(0xFF00FF00), fontWeight = FontWeight.Bold) // Verde neon do Figma

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