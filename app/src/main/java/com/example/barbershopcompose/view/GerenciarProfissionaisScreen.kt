package com.example.barbershopcompose.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barbershopcompose.data.*
import com.example.barbershopcompose.model.Profissional
import com.example.barbershopcompose.ui.theme.BackgroundBlack
import com.example.barbershopcompose.ui.theme.PrimaryBlue
import com.example.barbershopcompose.ui.theme.SurfaceGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GerenciarProfissionaisScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var lista by remember { mutableStateOf<List<Profissional>>(emptyList()) }
    var nomeInput by remember { mutableStateOf("") }
    var profissionalEditando by remember { mutableStateOf<Profissional?>(null) }

    fun carregarProfissionais() {
        buscarProfissionais { lista = it }
    }

    LaunchedEffect(Unit) { carregarProfissionais() }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundBlack).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)) {
            IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White) }
            Text("Gerenciar Profissionais", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // Formulário Adicionar/Editar
        OutlinedTextField(
            value = nomeInput,
            onValueChange = { nomeInput = it },
            label = { Text("Nome do Profissional", color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (nomeInput.isNotBlank()) {
                    if (profissionalEditando == null) {
                        adicionarProfissional(Profissional(nome = nomeInput), {
                            nomeInput = ""; carregarProfissionais()
                            Toast.makeText(context, "Adicionado!", Toast.LENGTH_SHORT).show()
                        }, {})
                    } else {
                        atualizarProfissional(profissionalEditando!!.copy(nome = nomeInput), {
                            nomeInput = ""; profissionalEditando = null; carregarProfissionais()
                            Toast.makeText(context, "Atualizado!", Toast.LENGTH_SHORT).show()
                        }, {})
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text(if (profissionalEditando == null) "Adicionar" else "Salvar Edição")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(lista) { prof ->
                Row(
                    modifier = Modifier.fillMaxWidth().background(SurfaceGray, RoundedCornerShape(8.dp)).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(prof.nome, color = Color.White, fontWeight = FontWeight.Bold)
                    Row {
                        IconButton(onClick = { nomeInput = prof.nome; profissionalEditando = prof }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Yellow)
                        }
                        IconButton(onClick = {
                            deletarProfissional(prof.id, {
                                carregarProfissionais()
                                Toast.makeText(context, "Deletado!", Toast.LENGTH_SHORT).show()
                            }, {})
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}