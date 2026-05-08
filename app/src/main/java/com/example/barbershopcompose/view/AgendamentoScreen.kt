package com.example.barbershopcompose.view

import android.app.DatePickerDialog
import android.widget.Toast
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barbershopcompose.R
import com.example.barbershopcompose.data.buscarHorariosOcupados
import com.example.barbershopcompose.data.salvarAgendamentoReal
import com.example.barbershopcompose.ui.theme.BackgroundBlack
import com.example.barbershopcompose.ui.theme.PrimaryBlue
import com.example.barbershopcompose.ui.theme.SurfaceGray
import java.util.Calendar

@Composable
fun AgendamentoScreen(servicoEscolhido: String, onFinalizarClick: () -> Unit, onBackClick: () -> Unit) {

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // --- ESTADOS DE DATA ---
    var dia by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var mes by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var ano by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    val dataFormatada = "$dia/${mes + 1}/$ano"

    // --- ESTADOS DE PROFISSIONAL E HORÁRIO ---
    val barbeiros = listOf(
        Pair("Ricardinho", R.drawable.fotoperfil),
        Pair("Oliveira", R.drawable.bigode),
        Pair("Ribeiro", R.drawable.tesoura)
    )
    var barbeiroSelecionado by remember { mutableStateOf(barbeiros[0]) }
    var horarioSelecionado by remember { mutableStateOf("") }

    // Lista para controlar visualmente o que já está reservado
    var horariosOcupados by remember { mutableStateOf<List<String>>(emptyList()) }

    // Efeito para buscar horários ocupados sempre que mudar o barbeiro ou a data
    LaunchedEffect(barbeiroSelecionado, dataFormatada) {
        buscarHorariosOcupados(barbeiroSelecionado.first, dataFormatada) { lista ->
            horariosOcupados = lista
        }
    }

    val horariosManha = listOf("8:00", "8:30", "9:00", "9:30", "10:00", "10:30")
    val horariosTarde = listOf("13:00", "13:30", "14:00", "14:30", "15:00", "15:30")

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            ano = selectedYear
            mes = selectedMonth
            dia = selectedDay
            horarioSelecionado = "" // Reseta o horário ao mudar a data
        }, ano, mes, dia
    ).apply { datePicker.minDate = calendar.timeInMillis }

    // --- DIÁLOGOS (MODAIS) ---
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        salvarAgendamentoReal(
                            profissional = barbeiroSelecionado.first,
                            data = dataFormatada,
                            horario = horarioSelecionado,
                            servico = servicoEscolhido,
                            onSucesso = {
                                showConfirmDialog = false
                                showSuccessDialog = true
                            },
                            onErro = { msg ->
                                showConfirmDialog = false
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar", color = Color.Red)
                }
            },
            title = { Text("Resumo do Agendamento", color = Color.White) },
            text = {
                Column {
                    Text("Serviço: $servicoEscolhido", color = Color.White)
                    Text("Profissional: ${barbeiroSelecionado.first}", color = Color.White)
                    Text("Data: $dataFormatada", color = Color.White)
                    Text("Horário: $horarioSelecionado", color = Color.White)
                }
            },
            containerColor = SurfaceGray
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    onFinalizarClick()
                }) { Text("Ver meus agendamentos") }
            },
            title = { Text("Sucesso!", color = Color.Green) },
            text = { Text("Agendamento realizado com sucesso!", color = Color.White) },
            containerColor = SurfaceGray
        )
    }

    // --- LAYOUT DA TELA ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(16.dp)
    ) {
        // Header com Calendário
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable { datePickerDialog.show() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text(text = dataFormatada, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Icon(Icons.Default.DateRange, contentDescription = null, tint = PrimaryBlue)
        }

        Text("Selecione o profissional", color = Color.White, modifier = Modifier.padding(bottom = 8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(barbeiros) { barbeiro ->
                val selecionado = barbeiroSelecionado == barbeiro
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        barbeiroSelecionado = barbeiro
                        horarioSelecionado = ""
                    }
                ) {
                    Image(
                        painter = painterResource(id = barbeiro.second),
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .border(if (selecionado) 3.dp else 0.dp, PrimaryBlue, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text(barbeiro.first, color = if (selecionado) PrimaryBlue else Color.White)
                }
            }
        }

        HorizontalDivider(color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))

        // Grades de Horários
        Text("Manhã", color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.height(110.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(horariosManha) { hora ->
                val ocupado = horariosOcupados.contains(hora)
                HorarioButton(hora, selecionado = horarioSelecionado == hora, bloqueado = ocupado) {
                    if (!ocupado) horarioSelecionado = hora
                }
            }
        }

        Text("Tarde", color = Color.White, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.height(110.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(horariosTarde) { hora ->
                val ocupado = horariosOcupados.contains(hora)
                HorarioButton(hora, selecionado = horarioSelecionado == hora, bloqueado = ocupado) {
                    if (!ocupado) horarioSelecionado = hora
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { showConfirmDialog = true },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            enabled = horarioSelecionado.isNotEmpty(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("CONFIRMAR AGENDAMENTO", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HorarioButton(hora: String, selecionado: Boolean, bloqueado: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !bloqueado,
        modifier = Modifier.height(45.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                bloqueado -> Color.DarkGray
                selecionado -> PrimaryBlue
                else -> Color.Transparent
            }
        ),
        border = if (!selecionado && !bloqueado) androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue) else null
    ) {
        Text(
            text = hora,
            color = if (bloqueado) Color.Gray else Color.White,
            style = TextStyle(textDecoration = if (bloqueado) TextDecoration.LineThrough else TextDecoration.None)
        )
    }
}