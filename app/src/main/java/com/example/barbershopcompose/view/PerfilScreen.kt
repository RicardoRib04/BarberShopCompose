package com.example.barbershopcompose.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barbershopcompose.R
import com.example.barbershopcompose.ui.theme.BackgroundBlack
import com.example.barbershopcompose.ui.theme.PrimaryBlue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PerfilScreen(onBackClick: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val uid = auth.currentUser?.uid

    // Estados dinâmicos
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dataNascimento by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(true) }

    // Busca dados reais ao carregar a tela
    LaunchedEffect(Unit) {
        if (uid != null) {
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        nome = document.getString("nome") ?: ""
                        email = document.getString("email") ?: ""
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
        // TOP BAR
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.fotoperfil),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            Text("Editar Perfil", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
        }

        if (carregando) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            // FOTO DE PERFIL GRANDE
            Image(
                painter = painterResource(id = R.drawable.fotoperfil),
                contentDescription = "Foto de Perfil Grande",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(2.dp, PrimaryBlue, CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(32.dp))

            // CAMPOS DE EDIÇÃO
            FigmaEditField(
                label = "Nome Completo:",
                value = nome,
                onValueChange = { nome = it },
                icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) }
            )

            FigmaEditField(
                label = "Email:",
                value = email,
                onValueChange = { /* E-mail fixo por segurança */ },
                icon = { /* Campo apenas leitura */ }
            )

            FigmaEditField(
                label = "Data de nascimento:",
                value = dataNascimento,
                onValueChange = { dataNascimento = it },
                icon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // BOTÃO SALVAR
            Button(
                onClick = {
                    if (uid != null) {
                        val updates = hashMapOf<String, Any>(
                            "nome" to nome,
                            "dataNascimento" to dataNascimento
                        )
                        db.collection("usuarios").document(uid).update(updates)
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

            // BOTÃO DE LOGOUT
            TextButton(
                onClick = {
                    auth.signOut()
                    onBackClick() // Volta para a tela de login
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Sair da Conta", color = Color.Red, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// COMPONENTE AUXILIAR (FORA DA FUNÇÃO PRINCIPAL)
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