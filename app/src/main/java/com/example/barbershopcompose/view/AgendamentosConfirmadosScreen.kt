package com.example.barbershopcompose.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barbershopcompose.ui.theme.BackgroundBlack
import com.example.barbershopcompose.ui.theme.PrimaryBlue
import com.example.barbershopcompose.ui.theme.SurfaceGray
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendamentosConfirmadosScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser

    val isAdmin = currentUser?.email == "admin@barberflow.com"

    var listaAgendamentos by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    var showEditDialog by remember { mutableStateOf(false) }
    var agendamentoSendoEditado by remember { mutableStateOf<Map<String, Any>?>(null) }
    var novaData by remember { mutableStateOf("") }
    var novoHorario by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val queryBase = db.collection("agendamentos_teste")

        val queryFiltrada = if (isAdmin) {
            queryBase.orderBy("timestamp", Query.Direction.DESCENDING)
        } else {
            queryBase.whereEqualTo("emailCliente", currentUser?.email ?: "")
        }

        queryFiltrada.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener

            if (snapshot != null) {
                val agendamentos = snapshot.documents.map { doc ->
                    val data = doc.data ?: emptyMap()
                    data.toMutableMap().apply { put("id", doc.id) }
                }

                if (!isAdmin) {
                    listaAgendamentos = agendamentos.sortedByDescending { it["timestamp"] as? Long ?: 0L }
                } else {
                    listaAgendamentos = agendamentos
                }
            }
        }
    }

    val calendar = Calendar.getInstance()

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val diaStr = dayOfMonth.toString().padStart(2, '0')
            val mesStr = (month + 1).toString().padStart(2, '0')
            novaData = "$diaStr/$mesStr/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = android.app.TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val horaStr = hourOfDay.toString().padStart(2, '0')
            val minStr = minute.toString().padStart(2, '0')
            novoHorario = "$horaStr:$minStr"
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    if (showEditDialog && agendamentoSendoEditado != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = SurfaceGray,
            title = { Text("Editar Agendamento", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = novaData,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Data", color = Color.LightGray) },
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Selecionar Data", tint = PrimaryBlue)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.Gray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = novoHorario,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Horário", color = Color.LightGray) },
                        trailingIcon = {
                            IconButton(onClick = { timePickerDialog.show() }) {
                                Icon(Icons.Default.Edit, contentDescription = "Selecionar Horário", tint = PrimaryBlue)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.Gray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = agendamentoSendoEditado!!["id"] as String
                        db.collection("agendamentos_teste").document(id)
                            .update(
                                mapOf(
                                    "com/example/barbershopcompose/data" to novaData,
                                    "horario" to novoHorario
                                )
                            ).addOnSuccessListener {
                                Toast.makeText(context, "Atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                                showEditDialog = false
                            }.addOnFailureListener {
                                Toast.makeText(context, "Erro ao atualizar", Toast.LENGTH_SHORT).show()
                            }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Salvar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar", color = Color.LightGray)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agendamentos", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
        }

        if (isAdmin) {
            Text("Modo Administrador Ativo", color = Color.Yellow, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(listaAgendamentos) { agendamento ->
                val id = agendamento["id"] as? String ?: ""
                val dataStr = agendamento["com/example/barbershopcompose/data"] as? String ?: ""
                val partesData = dataStr.split("/")
                val dia = partesData.getOrNull(0) ?: "--"
                val mesNum = partesData.getOrNull(1) ?: "1"

                val servico = agendamento["servico"] as? String ?: "Serviço"
                val profissionalNome = agendamento["profissional"] as? String ?: "Profissional"
                val horario = agendamento["horario"] as? String ?: "--:--"

                // Resgata o e-mail do cliente do Firebase
                val emailCliente = agendamento["emailCliente"] as? String ?: "Email desconhecido"

                CardAgendamentoFigma(
                    id = id,
                    dia = dia,
                    mes = abreviarMes(mesNum),
                    servico = servico,
                    preco = "R$ 50,00",
                    horario = horario,
                    nomeProfissional = profissionalNome,
                    isAdmin = isAdmin,
                    emailCliente = emailCliente, // Passa o e-mail para o Card
                    onEditClick = {
                        agendamentoSendoEditado = agendamento
                        novaData = dataStr
                        novoHorario = horario
                        showEditDialog = true
                    },
                    onDeleteClick = { idParaDeletar ->
                        db.collection("agendamentos_teste").document(idParaDeletar)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Agendamento excluído!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Erro ao excluir", Toast.LENGTH_SHORT).show()
                            }
                    }
                )
            }
        }
    }
}

@Composable
fun CardAgendamentoFigma(
    id: String,
    dia: String,
    mes: String,
    servico: String,
    preco: String,
    horario: String,
    nomeProfissional: String,
    isAdmin: Boolean,
    emailCliente: String, // Novo parâmetro recebendo o e-mail
    onEditClick: () -> Unit,
    onDeleteClick: (String) -> Unit
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

            // Se for Admin, mostra o e-mail do cliente abaixo do serviço
            if (isAdmin) {
                Text(
                    text = "Cliente: $emailCliente",
                    color = Color.Yellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
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

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(dia, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Text(mes, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            if (isAdmin) {
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(32.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Yellow, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { onDeleteClick(id) },
                        modifier = Modifier.size(32.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = Color.Red, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

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