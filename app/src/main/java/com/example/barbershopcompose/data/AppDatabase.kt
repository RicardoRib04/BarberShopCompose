package com.example.barbershopcompose.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.barbershopcompose.model.Agendamento

@Database(entities = [Agendamento::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun agendamentoDao(): AgendamentoDao
}