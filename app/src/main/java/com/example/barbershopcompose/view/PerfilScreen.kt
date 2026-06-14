package com.example.barbershopcompose.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barbershopcompose.ui.theme.BackgroundBlack
import com.example.barbershopcompose.ui.theme.PrimaryBlue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PerfilScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onGerenciarProfissionaisClick: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val currentUser = auth.currentUser
    val uid = currentUser?.uid

    // Verifica se o usuário logado é o admin
    val isAdmin = currentUser?.email == "admin@barberflow.com"

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var dataNascimento by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (uid != null) {
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        nome = document.getString("nome") ?: ""

                        val emailBanco = document.getString("email")
                        if (!emailBanco.isNullOrEmpty()) {
                            email = emailBanco
                        }

                        dataNascimento = document.getString("dataNascimento") ?: ""
                    }
                    carregando = false
                }
                .addOnFailureListener {
                    carregando = false
                    Toast.makeText(context, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                }
                // Foto pequena removida daqui
            }
            Text("Editar Perfil", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
        }

        if (carregando) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            // Foto grande centralizada removida daqui

            // Mantendo um pequeno espaçamento no topo
            Spacer(modifier = Modifier.height(16.dp))

            FigmaEditField(
                label = "Nome Completo:",
                value = nome,
                onValueChange = { nome = it },
                icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) }
            )

            FigmaEditField(
                label = "Email:",
                value = email,
                onValueChange = { },
                icon = { }
            )

            FigmaEditField(
                label = "Data de nascimento:",
                value = dataNascimento,
                onValueChange = { dataNascimento = it },
                icon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // --- BOTÃO EXCLUSIVO DO ADMIN PARA O CRUD ---
            if (isAdmin) {
                Button(
                    onClick = onGerenciarProfissionaisClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A8BFF)), // LightBlue
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("GERENCIAR PROFISSIONAIS (ADMIN)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Button(
                onClick = {
                    if (uid != null) {
                        val updates = hashMapOf<String, Any>(
                            "nome" to nome,
                            "email" to email,
                            "dataNascimento" to dataNascimento
                        )

                        db.collection("usuarios").document(uid).set(updates, com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener {
                                Toast.makeText(context, "Dados atualizados!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Erro ao atualizar perfil", Toast.LENGTH_SHORT).show()
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("SALVAR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            TextButton(
                onClick = {
                    auth.signOut()
                    onLogoutClick()
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Sair da Conta", color = Color.Red, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun FigmaEditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
            singleLine = true,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.weight(1f)) { innerTextField() }
                    icon()
                }
            }
        )
        HorizontalDivider(color = Color.DarkGray, thickness = 1.dp, modifier = Modifier.padding(top = 6.dp))
    }
}