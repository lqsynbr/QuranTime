package edu.luqsiyana.qurantime

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import edu.luqsiyana.qurantime.model.Room
import edu.luqsiyana.qurantime.model.Task

object FirebaseHelper {

    private val db = FirebaseFirestore.getInstance()

    // Ambil semua room yang user ikut
    fun getRoomsForUser(userId: String, callback: (List<Room>) -> Unit) {
        db.collection("rooms")
            .whereArrayContains("members", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    callback(emptyList())
                    return@addSnapshotListener
                }
                val rooms = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Room::class.java)?.apply {
                        id = doc.id
                    }
                } ?: emptyList()
                callback(rooms)
            }
    }

    // Buat room baru
    fun createRoom(room: Room, callback: (Boolean, String?) -> Unit) {
        val roomRef = db.collection("rooms").document()
        room.id = roomRef.id
        roomRef.set(room)
            .addOnSuccessListener { callback(true, room.id) }
            .addOnFailureListener { callback(false, null) }
    }

    // Update nama room
    fun updateRoomName(roomId: String, newName: String, callback: (Boolean) -> Unit) {
        db.collection("rooms").document(roomId)
            .update("roomName", newName)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    // Hapus room
    fun deleteRoom(roomId: String, callback: (Boolean) -> Unit) {
        db.collection("rooms").document(roomId)
            .delete()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    // Ambil semua task untuk satu room
    fun getTasksForRoom(roomId: String, callback: (List<Task>) -> Unit) {
        db.collection("rooms").document(roomId)
            .collection("tasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    callback(emptyList())
                    return@addSnapshotListener
                }
                val tasks = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Task::class.java)?.apply {
                        id = doc.id
                    }
                } ?: emptyList()
                callback(tasks)
            }
    }

    // Buat task baru
    fun createTask(roomId: String, task: Task, callback: (Boolean) -> Unit) {
        val taskRef = db.collection("rooms").document(roomId)
            .collection("tasks").document()
        task.id = taskRef.id
        taskRef.set(task)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    // Update status task
    fun updateTaskDone(roomId: String, taskId: String, done: Boolean) {
        db.collection("rooms").document(roomId)
            .collection("tasks").document(taskId)
            .update("done", done)
    }

    // Hapus task
    fun deleteTask(roomId: String, taskId: String, callback: (Boolean) -> Unit) {
        db.collection("rooms").document(roomId)
            .collection("tasks").document(taskId)
            .delete()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun addMemberToRoom(roomId: String, userId: String, callback: (Boolean) -> Unit) {
        val roomRef = db.collection("rooms").document(roomId)
        roomRef.update("members", FieldValue.arrayUnion(userId))
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun removeMemberFromRoom(roomId: String, userId: String, callback: (Boolean) -> Unit) {
        val roomRef = db.collection("rooms").document(roomId)
        roomRef.update("members", FieldValue.arrayRemove(userId))
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun assignJuzToMember(roomId: String, userId: String, juzNumber: Int, callback: (Boolean) -> Unit) {
        val roomRef = db.collection("rooms").document(roomId)
        val path = "juzAssignment.$userId"
        roomRef.update(path, juzNumber)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun inviteMemberToRoom(roomId: String, memberId: String, callback: (Boolean) -> Unit) {
        val roomRef = db.collection("rooms").document(roomId)
        roomRef.update("members", FieldValue.arrayUnion(memberId))
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun getRoomById(roomId: String, callback: (Room?) -> Unit) {
        val roomRef = db.collection("rooms").document(roomId)
        roomRef.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val room = doc.toObject(Room::class.java)
                    room?.id = doc.id
                    callback(room)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun assignJuz(roomId: String, memberId: String, juz: Int, callback: (Boolean) -> Unit) {
        db.collection("rooms").document(roomId)
            .collection("assignments").document(memberId)
            .set(mapOf("assignedJuz" to juz))
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun getUserNameById(uid: String, callback: (String?) -> Unit) {
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name")
                callback(name)
            }
            .addOnFailureListener {
                callback(null)
            }
    }




}

