package com.example.myprojectnathfirebase

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myprojectnathfirebase.ui.theme.MyProjectNathFirebaseTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyProjectNathFirebaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(db)
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun App(db: FirebaseFirestore) {
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "App Firebase Firestore", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth(0.3f)) {
                Text(text = "Nome:")
            }
            Column {
                TextField(
                    value = nome,
                    onValueChange = { nome = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth(0.3f)) {
                Text(text = "Telefone:")
            }
            Column {
                TextField(
                    value = telefone,
                    onValueChange = { telefone = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val pessoa = hashMapOf(
                "nome" to nome,
                "telefone" to telefone
            )

            db.collection("Clientes").add(pessoa)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }) {
            Text(text = "Cadastrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        ClienteList(db = db)
    }
}

@Composable
fun ClienteList(db: FirebaseFirestore) {
    val clientes = remember { mutableStateListOf<HashMap<String, String>>() }

    LaunchedEffect(Unit) {
        db.collection("Clientes")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val cliente = hashMapOf(
                        "nome" to (document.data["nome"] as String? ?: "--"),
                        "telefone" to (document.data["telefone"] as String? ?: "--")
                    )
                    clientes.add(cliente)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(clientes) { cliente ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(0.5f)) {
                    Text(text = cliente["nome"] ?: "--")
                }
                Column(modifier = Modifier.weight(0.5f)) {
                    Text(text = cliente["telefone"] ?: "--")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
