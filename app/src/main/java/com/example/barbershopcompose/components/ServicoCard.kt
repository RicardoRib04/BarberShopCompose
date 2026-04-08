package com.example.barbershopcompose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barbershopcompose.model.Servico
import com.example.barbershopcompose.ui.theme.PrimaryBlue
import com.example.barbershopcompose.ui.theme.SurfaceGray

@Composable
fun ServicoCard(servico: Servico, onAgendarClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGray),
        shape = RoundedCornerShape(15.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagem Redonda do Serviço (TipoCorte.png)
            Image(
                painter = painterResource(id = servico.imagem),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Textos (Nome, Preço, Tempo)
            Column(modifier = Modifier.weight(1f)) {
                Text(servico.nome, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("A partir de: ${servico.preco}", color = Color.Green, fontSize = 12.sp)
                Text("🕒 ${servico.tempo}", color = Color.LightGray, fontSize = 12.sp)
            }

            // Botão Agendar Pequeno
            Button(
                onClick = onAgendarClick,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text("Agendar", fontSize = 12.sp)
            }
        }
    }
}