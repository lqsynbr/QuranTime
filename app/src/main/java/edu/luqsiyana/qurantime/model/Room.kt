package edu.luqsiyana.qurantime.model

data class Room(
    var id: String? = null,
    var roomName: String = "",
    var createdBy: String = "",       // Admin/pembuat room
    var members: List<String> = listOf(), // Anggota room
    var juzAssignment: Map<String, Int> = mapOf() // userId -> juz number
)

