package com.example.barbershopcompose.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.barbershopcompose.R
import com.example.barbershopcompose.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController, onLoginClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack) // Usando seu tema BackgroundBlack
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título e Subtítulo
        Text(
            text = "BARBERSHOP",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )
        )
        Text(
            text = "Renove o seu estilo",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logobarbershop),
            contentDescription = "Logo Premium",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo Email
        Text(
            "Email:",
            color = Color.White,
            modifier = Modifier.align(Alignment.Start).padding(start = 12.dp, bottom = 4.dp)
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(25.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceGray,
                unfocusedContainerColor = SurfaceGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Senha
        Text(
            "Senha:",
            color = Color.White,
            modifier = Modifier.align(Alignment.Start).padding(start = 12.dp, bottom = 4.dp)
        )
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
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        // "Não tem conta? Registrar" - AGORA FUNCIONAL
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Text("Não tem conta? ", color = Color.White, fontSize = 14.sp)
            Text(
                text = "Registrar",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    navController.navigate("registrar") // Rota que definimos no NavGraph
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botão Login com Lógica Firebase
        if (carregando) {
            CircularProgressIndicator(color = PrimaryBlue)
        } else {
            Button(
                onClick = {
                    if (email.trim().isNotEmpty() && senha.trim().isNotEmpty()) {
                        carregando = true
                        auth.signInWithEmailAndPassword(email.trim(), senha)
                            .addOnSuccessListener {
                                carregando = false
                                onLoginClick() // Chama a navegação para a Home
                            }
                            .addOnFailureListener { e ->
                                carregando = false
                                Toast.makeText(context, "Erro: E-mail ou senha inválidos", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Esqueceu a senha? - AGORA FUNCIONAL
        Text(
            "Esqueceu a senha?",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                if (email.trim().isNotEmpty()) {
                    auth.sendPasswordResetEmail(email.trim())
                        .addOnSuccessListener {
                            Toast.makeText(context, "E-mail de recuperação enviado para: $email", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Erro ao enviar e-mail. Verifique o endereço digitado.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Digite seu e-mail no campo acima!", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}