package com.example.barbershopcompose.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

// E-mail do administrador (o barbeiro)
const val EMAIL_ADMIN = "admin@barberflow.com"

/**
 * Lógica para salvar agendamento com verificação de duplicidade e vínculo de e-mail
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

    // Pega o e-mail exato do usuário que está logado no momento do agendamento
    val emailAtual = FirebaseAuth.getInstance().currentUser?.email ?: "sem_email"

    colecao
        .whereEqualTo("profissional", profissional)
        .whereEqualTo("data", data)
        .whereEqualTo("horario", horario)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (querySnapshot.isEmpty) {
                val agendamento = hashMapOf(
                    "emailCliente" to emailAtual, // SALVA O E-MAIL COMO IDENTIFICADOR ÚNICO
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
 * Busca a lista de agendamentos filtrando pelo E-MAIL
 */
fun buscarAgendamentos(
    emailLogado: String,
    onResultado: (List<Map<String, Any>>) -> Unit
) {
    val queryBase = FirebaseFirestore.getInstance().collection("agendamentos_teste")

    if (emailLogado == EMAIL_ADMIN) {
        // ADMIN: Puxa todos os agendamentos do banco
        queryBase.orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { resultado ->
                val lista = resultado.documents.map { it.data ?: emptyMap() }
                onResultado(lista)
            }
    } else {
        // CLIENTE: Puxa APENAS os agendamentos que contêm o e-mail dele
        queryBase.whereEqualTo("emailCliente", emailLogado)
            .get()
            .addOnSuccessListener { resultado ->
                val lista = resultado.documents.map { it.data ?: emptyMap() }
                val listaOrdenada = lista.sortedByDescending { it["timestamp"] as? Long ?: 0L }
                onResultado(listaOrdenada)
            }
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