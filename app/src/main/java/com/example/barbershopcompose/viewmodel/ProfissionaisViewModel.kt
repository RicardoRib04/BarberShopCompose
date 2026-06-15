package com.example.barbershopcompose.viewmodel

import androidx.lifecycle.ViewModel
import com.example.barbershopcompose.domain.usecase.AddProfissionalUseCase
import com.example.barbershopcompose.domain.usecase.DeleteProfissionalUseCase
import com.example.barbershopcompose.domain.usecase.GetProfissionaisUseCase
import com.example.barbershopcompose.domain.usecase.UpdateProfissionalUseCase
import com.example.barbershopcompose.model.Profissional
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfissionaisViewModel @Inject constructor(
    private val getProfissionaisUseCase: GetProfissionaisUseCase,
    private val addProfissionalUseCase: AddProfissionalUseCase,
    private val updateProfissionalUseCase: UpdateProfissionalUseCase,
    private val deleteProfissionalUseCase: DeleteProfissionalUseCase
) : ViewModel() {

    private val _profissionais = MutableStateFlow<List<Profissional>>(emptyList())
    val profissionais: StateFlow<List<Profissional>> = _profissionais

    init {
        carregarProfissionais()
    }

    private fun carregarProfissionais() {
        getProfissionaisUseCase { lista ->
            _profissionais.value = lista
        }
    }

    fun adicionarProfissional(profissional: Profissional, onSucesso: () -> Unit, onErro: (String) -> Unit) {
        addProfissionalUseCase(profissional, {
            carregarProfissionais()
            onSucesso()
        }, onErro)
    }

    fun atualizarProfissional(profissional: Profissional, onSucesso: () -> Unit, onErro: (String) -> Unit) {
        updateProfissionalUseCase(profissional, {
            carregarProfissionais()
            onSucesso()
        }, onErro)
    }

    fun deletarProfissional(id: String, onSucesso: () -> Unit, onErro: (String) -> Unit) {
        deleteProfissionalUseCase(id, {
            carregarProfissionais()
            onSucesso()
        }, onErro)
    }
}