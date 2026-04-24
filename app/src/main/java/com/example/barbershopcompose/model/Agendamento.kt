package com.example.barbershopcompose.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agendamentos")
data class Agendamento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val barbeiroNome: String,
    val servicoNome: String,
    val data: String,
    val horario: String
)