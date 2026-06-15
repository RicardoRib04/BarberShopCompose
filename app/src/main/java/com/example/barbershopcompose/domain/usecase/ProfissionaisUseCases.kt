package com.example.barbershopcompose.domain.usecase

import com.example.barbershopcompose.domain.repository.ProfissionalRepository
import com.example.barbershopcompose.model.Profissional
import javax.inject.Inject

class GetProfissionaisUseCase @Inject constructor(private val repository: ProfissionalRepository) {
    operator fun invoke(onResult: (List<Profissional>) -> Unit) = repository.getProfissionais(onResult)
}

class AddProfissionalUseCase @Inject constructor(private val repository: ProfissionalRepository) {
    operator fun invoke(profissional: Profissional, onSucesso: () -> Unit, onErro: (String) -> Unit) =
        repository.addProfissional(profissional, onSucesso, onErro)
}

class UpdateProfissionalUseCase @Inject constructor(private val repository: ProfissionalRepository) {
    operator fun invoke(profissional: Profissional, onSucesso: () -> Unit, onErro: (String) -> Unit) =
        repository.updateProfissional(profissional, onSucesso, onErro)
}

class DeleteProfissionalUseCase @Inject constructor(private val repository: ProfissionalRepository) {
    operator fun invoke(id: String, onSucesso: () -> Unit, onErro: (String) -> Unit) =
        repository.deleteProfissional(id, onSucesso, onErro)
}