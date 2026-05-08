package com.example.barbershopcompose.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barbershopcompose.R
import com.example.barbershopcompose.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegistroScreen(onBackClick: () -> Unit, onRegistroSuccess: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dataNascimento by remember { mutableStateOf("") } // NOVO CAMPO
    var senha by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()), // Adicionado scroll para telas menores
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
        }

        Image(
            painter = painterResource(id = R.drawable.logobarbershop),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = "CRIAR CONTA",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // --- CAMPOS DE TEXTO ---
        CampoRegistro(label = "Nome Completo:", value = nome, onValueChange = { nome = it })
        CampoRegistro(label = "Email:", value = email, onValueChange = { email = it })
        CampoRegistro(label = "Data de Nascimento (DD/MM/AAAA):", value = dataNascimento, onValueChange = { dataNascimento = it })

        Text("Senha:", color = Color.White, modifier = Modifier.align(Alignment.Start).padding(start = 12.dp, top = 16.dp))
        TextField(
            value = senha,
            onValueChange = { senha = it },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(25.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceGray,
                unfocusedContainerColor = SurfaceGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (carregando) {
            CircularProgressIndicator(color = PrimaryBlue)
        } else {
            Button(
                onClick = {
                    if (nome.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty() && dataNascimento.isNotEmpty()) {
                        carregando = true

                        // 1. Criar usuário no Firebase Auth
                        auth.createUserWithEmailAndPassword(email, senha)
                            .addOnSuccessListener { resultado ->
                                val userId = resultado.user?.uid

                                // 2. Salvar dados extras no Firestore
                                val usuarioMap = hashMapOf(
                                    "nome" to nome,
                                    "email" to email,
                                    "dataNascimento" to dataNascimento,
                                    "uid" to userId
                                )

                                if (userId != null) {
                                    db.collection("usuarios").document(userId).set(usuarioMap)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Conta criada!", Toast.LENGTH_SHORT).show()
                                            onRegistroSuccess()
                                        }
                                        .addOnFailureListener { e ->
                                            carregando = false
                                            Toast.makeText(context, "Erro no banco: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                            .addOnFailureListener { e ->
                                carregando = false
                                Toast.makeText(context, "Erro no Auth: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("CADASTRAR", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Componente auxiliar para não repetir código
@Composable
fun CampoRegistro(label: String, value: String, onValueChange: (String) -> Unit) {
    Text(label, color = Color.White, modifier = Modifier.padding(start = 12.dp, top = 16.dp, bottom = 4.dp).fillMaxWidth())
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceGray,
            unfocusedContainerColor = SurfaceGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color.White
        )
    )
}