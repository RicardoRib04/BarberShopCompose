package com.example.barbershopcompose.view

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barbershopcompose.R
import com.example.barbershopcompose.ui.theme.BackgroundBlack
import com.example.barbershopcompose.ui.theme.PrimaryBlue
import com.example.barbershopcompose.ui.theme.SurfaceGray
import java.util.Calendar

@Composable
fun AgendamentoScreen(onFinalizarClick: () -> Unit, onBackClick: () -> Unit) {

    // --- LÓGICA DE DATA ---
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Estados para armazenar a data selecionada
    var dia by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var mes by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var ano by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }

    val dataFormatada = "$dia/${mes + 1}/$ano"

    // Configuração do Diálogo de Data
    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            ano = selectedYear
            mes = selectedMonth
            dia = selectedDay
        }, ano, mes, dia
    )
    // Impede selecionar datas passadas
    datePickerDialog.datePicker.minDate = calendar.timeInMillis

    // --- RESTANTE DOS ESTADOS ---
    val barbeiros = listOf(
        Pair("Ricardinho", R.drawable.fotoperfil),
        Pair("Oliveira", R.drawable.bigode),
        Pair("Ribeiro", R.drawable.tesoura)
    )
    var barbeiroSelecionado by remember { mutableStateOf(barbeiros[0]) }

    val horariosManha = listOf("8:00", "8:30", "9:00", "9:30", "10:00", "10:30")
    val horariosTarde = listOf("13:00", "13:30", "14:00", "14:30", "15:00", "15:30")

    var horarioSelecionado by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // --- MODAL DE RESUMO ---
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        showSuccessDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) { Text("Confirmar agendamento") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar agendamento", color = Color.Red)
                }
            },
            title = { Text("BARBERSHOP", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Corte kids (0 a 6 anos)", fontWeight = FontWeight.Bold)
                    Text("Barbeiro: ${barbeiroSelecionado.first}")
                    Text("Data: $dataFormatada") // Exibe a data no resumo
                    Text("Horário: $horarioSelecionado")
                    Text("Valor: R$ 50,00", color = Color.Green, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = SurfaceGray,
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    // --- MODAL DE SUCESSO ---
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    onFinalizarClick()
                }) { Text("Ver agendamentos") }
            },
            title = { Text("Seu agendamento foi concluído!!", color = Color.Green, fontSize = 18.sp) },
            text = { Text("Tudo certo para o dia $dataFormatada às $horarioSelecionado!", color = Color.White) },
            containerColor = SurfaceGray
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(16.dp)
    ) {
        // Top Bar clicável para abrir o calendário
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable { datePickerDialog.show() }, // Abre o calendário ao clicar na barra
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }

            // Exibe a data selecionada dinamicamente
            Text(
                text = dataFormatada,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Icon(Icons.Default.DateRange, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(30.dp))
        }

        // SELEÇÃO DE PROFISSIONAL (CARROSSEL)
        Text("Selecione o profissional", color = Color.White, modifier = Modifier.padding(vertical = 8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(barbeiros) { barbeiro ->
                val (nome, foto) = barbeiro
                val selecionado = barbeiroSelecionado == barbeiro

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { barbeiroSelecionado = barbeiro }
                ) {
                    Image(
                        painter = painterResource(id = foto),
                        contentDescription = nome,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (selecionado) 3.dp else 0.dp,
                                color = if (selecionado) PrimaryBlue else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = nome,
                        color = if (selecionado) PrimaryBlue else Color.White,
                        fontWeight = if (selecionado) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Divider(color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))

        Text("Horários disponíveis para $dia/${mes + 1}", color = Color.White, fontWeight = FontWeight.Bold)

        // Grade Manhã
        Text("Manhã", color = Color.White, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(110.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(horariosManha) { hora ->
                HorarioButton(hora, selecionado = horarioSelecionado == hora) {
                    horarioSelecionado = hora
                }
            }
        }

        // Grade Tarde
        Text("Tarde", color = Color.White, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(110.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(horariosTarde) { hora ->
                HorarioButton(hora, selecionado = horarioSelecionado == hora) {
                    horarioSelecionado = hora
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { if (horarioSelecionado.isNotEmpty()) showConfirmDialog = true },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(10.dp),
            enabled = horarioSelecionado.isNotEmpty()
        ) {
            Text("CONFIRMAR AGENDAMENTO", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HorarioButton(hora: String, selecionado: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(45.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selecionado) PrimaryBlue else Color.Transparent
        ),
        border = if (!selecionado) androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue) else null
    ) {
        Text(hora, color = Color.White, fontSize = 14.sp)
    }
}