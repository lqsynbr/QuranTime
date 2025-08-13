package edu.luqsiyana.qurantime.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import edu.luqsiyana.qurantime.FirebaseHelper
import edu.luqsiyana.qurantime.databinding.ItemRoomBinding
import edu.luqsiyana.qurantime.model.Room

class RoomAdapter(
    private val rooms: MutableList<Room>,
    private val onClick: (Room) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    inner class RoomViewHolder(val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun getItemCount(): Int = rooms.size

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]
        holder.binding.tvRoomName.text = room.roomName

        // Klik room → masuk ke RoomActivity
        holder.binding.root.setOnClickListener {
            onClick(room)
        }

        // Tombol Edit
        holder.binding.btnEditRoom.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Edit Room")
            val input = EditText(context)
            input.setText(room.roomName)
            builder.setView(input)

            builder.setPositiveButton("Simpan") { dialog, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    FirebaseHelper.updateRoomName(room.id!!, newName) { success ->
                        if (success) {
                            Toast.makeText(context, "Room berhasil diupdate", Toast.LENGTH_SHORT).show()
                            room.roomName = newName
                            notifyItemChanged(position)
                        } else {
                            Toast.makeText(context, "Gagal update room", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                dialog.dismiss()
            }

            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        // Tombol Hapus
        holder.binding.btnDeleteRoom.setOnClickListener {
            val context = holder.itemView.context
            AlertDialog.Builder(context)
                .setTitle("Hapus Room")
                .setMessage("Apakah kamu yakin ingin menghapus room ini?")
                .setPositiveButton("Hapus") { dialog, _ ->
                    FirebaseHelper.deleteRoom(room.id!!) { success ->
                        if (success) {
                            Toast.makeText(context, "Room dihapus", Toast.LENGTH_SHORT).show()
                            rooms.removeAt(position)
                            notifyItemRemoved(position)
                        } else {
                            Toast.makeText(context, "Gagal menghapus room", Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}

