package com.raffy.ourbook

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog

class BooksAdapter(private var books: List<Book>, context: Context) : RecyclerView.Adapter<BooksAdapter.BookViewHolder>() {
    private val db: DatabaseHelper = DatabaseHelper(context)

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        val tvNamaPanggilan: TextView = itemView.findViewById(R.id.tvNamaPanggilan)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val ivPoto: ImageView = itemView.findViewById(R.id.ivPoto)
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.tvNama.text = book.nama
        holder.tvNamaPanggilan.text = book.nama_panggilan
        holder.tvTanggal.text = book.tgl_lahir
        holder.ivPoto.setImageBitmap(BitmapFactory.decodeByteArray(book.poto, 0, book.poto.size))

        holder.ivEdit.setOnClickListener {
            val intent = Intent(holder.itemView.context, AddActivity::class.java).apply {
                putExtra("id", book.id)
                putExtra("nama", book.nama)
                putExtra("nama_panggilan", book.nama_panggilan)
                putExtra("email", book.email)
                putExtra("telepon", book.telepon)
                putExtra("poto", book.poto)
                putExtra("tgl_lahir", book.tgl_lahir)
                putExtra("alamat", book.alamat)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.ivDelete.setOnClickListener {
            SweetAlertDialog(holder.itemView.context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Confirm Delete")
                .setContentText("Are you sure to delete this data?")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener { dialog ->
                    db.deleteBook(book.id)
                    refreshData(db.getAllBooks())
                    Toast.makeText(holder.itemView.context, "Book Deleted", Toast.LENGTH_SHORT).show()
                    dialog.dismissWithAnimation()
                }
                .setCancelClickListener { dialog ->
                    dialog.dismissWithAnimation()
                }
                .show()
        }

        holder.tvNama.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java).apply {
                putExtra("id", book.id)
                putExtra("nama", book.nama)
                putExtra("nama_panggilan", book.nama_panggilan)
                putExtra("email", book.email)
                putExtra("telepon", book.telepon)
                putExtra("poto", book.poto)
                putExtra("tgl_lahir", book.tgl_lahir)
                putExtra("alamat", book.alamat)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    fun refreshData(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}