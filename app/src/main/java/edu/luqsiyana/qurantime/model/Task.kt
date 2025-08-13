package edu.luqsiyana.qurantime.model

data class Task(
    var id: String? = null, // <- ini untuk document id
    val title: String = "",
    val assignedTo: String = "",
    val done: Boolean = false
)
