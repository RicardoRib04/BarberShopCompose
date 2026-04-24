package com.example.barbershopcompose.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.barbershopcompose.model.Agendamento
import kotlinx.coroutines.flow.Flow

@Dao
interface AgendamentoDao {
    @Insert
    suspend fun inserir(agendamento: Agendamento)

    @Query("SELECT * FROM agendamentos ORDER BY id DESC")
    fun listarTodos(): Flow<List<Agendamento>>
}