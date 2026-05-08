package com.example.barbershopcompose.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

/**
 * Lógica para salvar agendamento com verificação de duplicidade
 */
fun salvarAgendamentoReal(
    profissional: String,
    data: String,
    horario: String,
    servico: String,
    onSucesso: () -> Unit,
    onErro: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val colecao = db.collection("agendamentos_teste")

    colecao
        .whereEqualTo("profissional", profissional)
        .whereEqualTo("data", data)
        .whereEqualTo("horario", horario)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (querySnapshot.isEmpty) {
                val agendamento = hashMapOf(
                    "cliente" to "Cliente Padrão",
                    "profissional" to profissional,
                    "servico" to servico,
                    "data" to data,
                    "horario" to horario,
                    "timestamp" to System.currentTimeMillis()
                )

                colecao.add(agendamento)
                    .addOnSuccessListener {
                        Log.d("FirestoreTest", "Sucesso!")
                        onSucesso()
                    }
                    .addOnFailureListener { e -> onErro("Erro ao salvar: ${e.message}") }
            } else {
                onErro("Este horário já foi reservado com $profissional!")
            }
        }
        .addOnFailureListener { e -> onErro("Erro no banco: ${e.message}") }
}

/**
 * Busca a lista de todos os agendamentos
 */
fun buscarAgendamentos(onResultado: (List<Map<String, Any>>) -> Unit) {
    FirebaseFirestore.getInstance().collection("agendamentos_teste")
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener { resultado ->
            val lista = resultado.documents.map { it.data ?: emptyMap() }
            onResultado(lista)
        }
}

/**
 * Busca horários ocupados para bloquear os botões na tela
 */
fun buscarHorariosOcupados(
    profissional: String,
    data: String,
    onResultado: (List<String>) -> Unit
) {
    FirebaseFirestore.getInstance().collection("agendamentos_teste")
        .whereEqualTo("profissional", profissional)
        .whereEqualTo("data", data)
        .get()
        .addOnSuccessListener { snapshot ->
            val ocupados = snapshot.documents.map { it.getString("horario") ?: "" }
            onResultado(ocupados)
        }
        .addOnFailureListener { onResultado(emptyList()) }
}