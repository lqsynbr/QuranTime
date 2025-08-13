package edu.luqsiyana.qurantime.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import edu.luqsiyana.qurantime.FirebaseHelper
import edu.luqsiyana.qurantime.databinding.ItemTaskBinding
import edu.luqsiyana.qurantime.model.Task

class TaskAdapter(
    private val tasks: MutableList<Task>, // pastikan MutableList biar bisa hapus
    private val roomId: String
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.tvTaskTitle.text = task.title
        holder.binding.cbDone.isChecked = task.done

        holder.binding.cbDone.setOnCheckedChangeListener { _, isChecked ->
            FirebaseHelper.updateTaskDone(roomId, task.id!!, isChecked)
        }

        holder.binding.btnDeleteTask.setOnClickListener {
            FirebaseHelper.deleteTask(roomId, task.id!!) { success ->
                if (success) {
                    Toast.makeText(holder.itemView.context, "Task dihapus", Toast.LENGTH_SHORT).show()
                    tasks.removeAt(position)
                    notifyItemRemoved(position)
                } else {
                    Toast.makeText(holder.itemView.context, "Gagal hapus task", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount() = tasks.size
}

