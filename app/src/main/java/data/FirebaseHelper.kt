package com.example.barbershopcompose.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

fun salvarAgendamentoReal(profissional: String, data: String, horario: String, servico: String, onSucesso: () -> Unit) {
    val db = FirebaseFirestore.getInstance()

    val agendamento = hashMapOf(
        "cliente" to "Cliente Padrão",
        "profissional" to profissional,
        "servico" to servico, // <--- Agora o serviço não é mais um texto fixo!
        "data" to data,
        "horario" to horario,
        "timestamp" to System.currentTimeMillis()
    )

    db.collection("agendamentos_teste")
        .add(agendamento)
        .addOnSuccessListener {
            Log.d("FirestoreTest", "Sucesso! Documento salvo real")
            onSucesso()
        }
        .addOnFailureListener { e ->
            Log.w("FirestoreTest", "Erro ao salvar no banco!", e)
        }
}
fun buscarAgendamentos(onResultado: (List<Map<String, Any>>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("agendamentos_teste")
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener { resultado ->
            val lista = mutableListOf<Map<String, Any>>()
            for (documento in resultado) {
                lista.add(documento.data)
            }
            onResultado(lista)
        }
        .addOnFailureListener {
            Log.w("FirestoreTest", "Erro ao buscar dados")
        }
}