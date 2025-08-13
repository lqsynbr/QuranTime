package edu.luqsiyana.qurantime

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.luqsiyana.qurantime.adapter.RoomAdapter
import edu.luqsiyana.qurantime.model.Room

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var rvRooms: RecyclerView
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser ?: run {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        rvRooms = findViewById(R.id.rv_rooms)
        rvRooms.layoutManager = LinearLayoutManager(this)

        val btnLogout = findViewById<Button>(R.id.btn_logout)
        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val btnCreateRoom = findViewById<Button>(R.id.btn_create_room)
        btnCreateRoom.setOnClickListener {
            showCreateRoomDialog()
        }

        loadRooms()
    }

    private fun loadRooms() {
        FirebaseHelper.getRoomsForUser(currentUser.uid) { rooms ->
            val roomList = rooms.toMutableList() // ubah jadi MutableList
            val adapter = RoomAdapter(roomList) { selectedRoom ->
                val intent = Intent(this, RoomActivity::class.java)
                intent.putExtra("ROOM_ID", selectedRoom.id)
                startActivity(intent)
            }
            rvRooms.adapter = adapter

            if (roomList.isEmpty()) {
                Toast.makeText(this, "Belum ada room, buat dulu!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showCreateRoomDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Buat Room Baru")

        val input = EditText(this)
        input.hint = "Nama Room"
        builder.setView(input)

        builder.setPositiveButton("Buat") { dialog, _ ->
            val roomName = input.text.toString().trim()
            if (roomName.isNotEmpty()) {
                createRoom(roomName)
            } else {
                Toast.makeText(this, "Nama room tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun createRoom(roomName: String) {
        val newRoom = Room(
            roomName = roomName,
            createdBy = currentUser.uid,
            members = mutableListOf(currentUser.uid)
        )

        FirebaseHelper.createRoom(newRoom) { success, roomId ->
            if (success && roomId != null) {
                Toast.makeText(this, "Room berhasil dibuat", Toast.LENGTH_SHORT).show()
                loadRooms() // reload RecyclerView supaya muncul room baru
            } else {
                Toast.makeText(this, "Gagal membuat room", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


