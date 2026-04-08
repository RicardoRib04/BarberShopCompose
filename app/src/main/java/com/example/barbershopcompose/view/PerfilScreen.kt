package com.example.barbershopcompose.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barbershopcompose.R
import com.example.barbershopcompose.ui.theme.BackgroundBlack
import com.example.barbershopcompose.ui.theme.PrimaryBlue

@Composable
fun PerfilScreen(onBackClick: () -> Unit) {
    // ESTADOS: Isso permite que o texto mude quando você digita
    var nome by remember { mutableStateOf("Ricardo Oliveira") }
    var email by remember { mutableStateOf("Teste123@gmail.com") }
    var dataNascimento by remember { mutableStateOf("24/05/2005") }
    var senha by remember { mutableStateOf("T3**********2a") } // Campo de senha, deixamos oculto

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Adiciona scroll se a tela for pequena
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TOP BAR IGUAL AO FIGMA (Foto pequena + Titulo + Menu)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Seta de voltar para sair do perfil
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

        // FOTO DE PERFIL GRANDE
        Image(
            painter = painterResource(id = R.drawable.fotoperfil),
            contentDescription = "Foto de Perfil Grande",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(32.dp))

        // CAMPOS ESTILIZADOS COMO NO FIGMA (BASIC TEXT FIELD)
        FigmaEditField(
            label = "Nome Completo:",
            value = nome,
            onValueChange = { nome = it },
            icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) }
        )

        FigmaEditField(
            label = "Email:",
            value = email,
            onValueChange = { email = it },
            icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) }
        )

        FigmaEditField(
            label = "Data de nascimento:",
            value = dataNascimento,
            onValueChange = { dataNascimento = it },
            icon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) }
        )

        // SEÇÃO DE SENHA (como é oculta, deixamos como texto fixo, mas com a linha)
        FigmaPasswordField(label = "Senha:", value = senha)

        Spacer(modifier = Modifier.weight(1f)) // Empurra o botão para baixo

        // BOTÃO SALVAR IGUAL AO FIGMA
        Button(
            onClick = { /* Lógica de salvar */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(12.dp) // Cantos mais arredondados como no Figma
        ) {
            Text("SALVAR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

// COMPONENTE QUE CRIA O CAMPO ESTILIZADO (BASIC TEXT FIELD)
@Composable
fun FigmaEditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)

        // BasicTextField não tem borda nem fundo, por isso usamos ela.
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
                    Box(modifier = Modifier.weight(1f)) {
                        innerTextField()
                    }
                    icon() // Ícone alinhado à direita
                }
            }
        )
        // A linha fina embaixo do campo
        Divider(color = Color.DarkGray, thickness = 1.dp, modifier = Modifier.padding(top = 6.dp))
    }
}

// COMPONENTE PARA O CAMPO DE SENHA (TEXTO FIXO COM LINHA)
@Composable
fun FigmaPasswordField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(value, color = Color.White, fontSize = 16.sp)
            Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Divider(color = Color.DarkGray, thickness = 1.dp, modifier = Modifier.padding(top = 6.dp))
    }
}