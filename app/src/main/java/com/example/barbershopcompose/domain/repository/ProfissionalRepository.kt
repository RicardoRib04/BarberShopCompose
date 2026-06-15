package com.example.barbershopcompose.domain.repository

import com.example.barbershopcompose.model.Profissional

interface ProfissionalRepository {
    fun getProfissionais(onResult: (List<Profissional>) -> Unit)
    fun addProfissional(profissional: Profissional, onSucesso: () -> Unit, onErro: (String) -> Unit)
    fun updateProfissional(profissional: Profissional, onSucesso: () -> Unit, onErro: (String) -> Unit)
    fun deleteProfissional(id: String, onSucesso: () -> Unit, onErro: (String) -> Unit)
}