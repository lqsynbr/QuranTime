package edu.luqsiyana.qurantime

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.luqsiyana.qurantime.adapter.TaskAdapter
import edu.luqsiyana.qurantime.databinding.ActivityRoomBinding
import edu.luqsiyana.qurantime.model.Room
import edu.luqsiyana.qurantime.model.Task
import java.util.concurrent.atomic.AtomicInteger

class RoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomBinding
    private lateinit var taskAdapter: TaskAdapter
    private var taskList = mutableListOf<Task>()
    private lateinit var currentRoomId: String
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser = FirebaseAuth.getInstance().currentUser ?: run {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Ambil ROOM_ID dari intent
        currentRoomId = intent.getStringExtra("ROOM_ID") ?: ""
        if (currentRoomId.isEmpty()) {
            Toast.makeText(this, "Room tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // RecyclerView
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(taskList, currentRoomId)
        binding.rvTasks.adapter = taskAdapter

        // Load tasks
        loadTasks(currentRoomId)

        // Tombol tambah task
        binding.btnAddTask.setOnClickListener { showAddTaskDialog() }

        // Tombol invite & assign Juz -> Coming Soon
        binding.btnInvite.setOnClickListener {
            Toast.makeText(this, "Fitur Invite anggota coming soon!", Toast.LENGTH_SHORT).show()
        }

        binding.btnAssignJuz.setOnClickListener {
            Toast.makeText(this, "Fitur Assign Juz coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadTasks(roomId: String) {
        FirebaseHelper.getTasksForRoom(roomId) { tasks ->
            taskList.clear()
            taskList.addAll(tasks)
            taskAdapter.notifyDataSetChanged()
        }
    }

    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tambah Task")
        val input = EditText(this)
        input.hint = "Judul Task"
        builder.setView(input)

        builder.setPositiveButton("Tambah") { dialog, _ ->
            val taskTitle = input.text.toString().trim()
            if (taskTitle.isNotEmpty()) {
                val newTask = Task(title = taskTitle, done = false)
                FirebaseHelper.createTask(currentRoomId, newTask) { success ->
                    if (success) {
                        Toast.makeText(this, "Task berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        loadTasks(currentRoomId)
                    } else {
                        Toast.makeText(this, "Gagal menambahkan task", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Task tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}






