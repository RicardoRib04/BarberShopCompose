package com.example.barbershopcompose.data.repository

import com.example.barbershopcompose.domain.repository.ProfissionalRepository
import com.example.barbershopcompose.model.Profissional
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ProfissionalRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ProfissionalRepository {

    override fun getProfissionais(onResult: (List<Profissional>) -> Unit) {
        db.collection("profissionais").get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { it.toObject(Profissional::class.java) }
                onResult(lista)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    override fun addProfissional(profissional: Profissional, onSucesso: () -> Unit, onErro: (String) -> Unit) {
        val ref = db.collection("profissionais").document()
        val novoProfissional = profissional.copy(id = ref.id)
        ref.set(novoProfissional)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { e -> onErro(e.message ?: "Erro ao adicionar") }
    }

    override fun updateProfissional(profissional: Profissional, onSucesso: () -> Unit, onErro: (String) -> Unit) {
        db.collection("profissionais").document(profissional.id).set(profissional)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { e -> onErro(e.message ?: "Erro ao atualizar") }
    }

    override fun deleteProfissional(id: String, onSucesso: () -> Unit, onErro: (String) -> Unit) {
        db.collection("profissionais").document(id).delete()
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { e -> onErro(e.message ?: "Erro ao deletar") }
    }
}