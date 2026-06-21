package com.example.barbershopcompose.view

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barbershopcompose.data.EMAIL_ADMIN
import com.example.barbershopcompose.data.buscarHorariosOcupados
import com.example.barbershopcompose.data.buscarProfissionais
import com.example.barbershopcompose.data.salvarAgendamentoReal
import com.example.barbershopcompose.model.Profissional
import com.example.barbershopcompose.ui.theme.BackgroundBlack
import com.example.barbershopcompose.ui.theme.PrimaryBlue
import com.example.barbershopcompose.ui.theme.SurfaceGray
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

@Composable
fun AgendamentoScreen(servicoEscolhido: String, onFinalizarClick: () -> Unit, onBackClick: () -> Unit) {

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val auth = FirebaseAuth.getInstance()

    // Verifica se quem está agendando é o Admin
    val isAdmin = auth.currentUser?.email == EMAIL_ADMIN

    // --- ESTADOS DE DATA ---
    var dia by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var mes by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var ano by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    val dataFormatada = "$dia/${mes + 1}/$ano"

    // --- ESTADOS DE PROFISSIONAL, HORÁRIO E E-MAIL MANUAL ---
    var barbeiros by remember { mutableStateOf<List<Profissional>>(emptyList()) }
    var barbeiroSelecionado by remember { mutableStateOf<Profissional?>(null) }
    var horarioSelecionado by remember { mutableStateOf("") }
    var horariosOcupados by remember { mutableStateOf<List<String>>(emptyList()) }
    var emailClienteInput by remember { mutableStateOf("") } // <-- Campo do Admin

    LaunchedEffect(Unit) {
        buscarProfissionais { lista ->
            barbeiros = lista
            if (lista.isNotEmpty()) {
                barbeiroSelecionado = lista[0]
            }
        }
    }

    LaunchedEffect(barbeiroSelecionado, dataFormatada) {
        barbeiroSelecionado?.let { prof ->
            buscarHorariosOcupados(prof.nome, dataFormatada) { lista ->
                horariosOcupados = lista
            }
        } ?: run {
            horariosOcupados = emptyList()
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
            horarioSelecionado = ""
        }, ano, mes, dia
    ).apply { datePicker.minDate = calendar.timeInMillis }

    // Validação para ativar o botão confirmar
    val isFormularioValido = if (isAdmin) {
        horarioSelecionado.isNotEmpty() && emailClienteInput.isNotBlank()
    } else {
        horarioSelecionado.isNotEmpty()
    }

    // --- DIÁLOGOS ---
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        salvarAgendamentoReal(
                            profissional = barbeiroSelecionado?.nome ?: "",
                            data = dataFormatada,
                            horario = horarioSelecionado,
                            servico = servicoEscolhido,
                            emailClienteManual = if (isAdmin) emailClienteInput else null, // Passa o e-mail customizado
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
                    if (isAdmin) {
                        Text("Cliente: $emailClienteInput", color = Color.Cyan)
                    }
                    Text("Serviço: $servicoEscolhido", color = Color.White)
                    Text("Profissional: ${barbeiroSelecionado?.nome ?: ""}", color = Color.White)
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

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selecionado) PrimaryBlue else Color.DarkGray)
                        .clickable {
                            barbeiroSelecionado = barbeiro
                            horarioSelecionado = ""
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = barbeiro.nome, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // SE FOR ADMIN, EXIBE O CAMPO DE E-MAIL DO CLIENTE AQUI:
        if (isAdmin) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("E-mail do Cliente (Agendamento Presencial)", color = Color.Cyan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = emailClienteInput,
                onValueChange = { emailClienteInput = it },
                placeholder = { Text("cliente@gmail.com", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceGray,
                    unfocusedContainerColor = SurfaceGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
        }

        HorizontalDivider(color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))

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
            enabled = isFormularioValido, // Aplica a regra de validação
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