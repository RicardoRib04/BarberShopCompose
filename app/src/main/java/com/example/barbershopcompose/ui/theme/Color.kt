package com.example.barbershopcompose.ui.theme

import androidx.compose.ui.graphics.Color

// --- Cores do Protótipo Figma ---

// Fundo principal e superfícies
val BackgroundBlack = Color(0xFF121212) // Fundo bem escuro
val SurfaceGray = Color(0xFF2C2C2C)    // Cor dos cards e campos de texto
val CardInternalGray = Color(0xFF3D3D3D) // Cinza levemente mais claro para contraste

// Cores de destaque (Azul do Figma)
val PrimaryBlue = Color(0xFF1A5AD7)    // Azul dos botões e seleção
val LightBlue = Color(0xFF4A8BFF)      // Azul para ícones ou textos secundários

// Cores de Texto
val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFFAAAAAA)      // Para legendas e textos menores

// Cores de Feedback
val SuccessGreen = Color(0xFF4CAF50)   // Para o "Agendamento concluído"
val ErrorRed = Color(0xFFE53935)       // Para o botão "Cancelar"

// --- Manter para compatibilidade com o Theme.kt original se necessário ---
val Purple80 = PrimaryBlue
val PurpleGrey80 = SurfaceGray
val Pink80 = TextGray