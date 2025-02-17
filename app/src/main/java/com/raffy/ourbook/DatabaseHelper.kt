package com.raffy.ourbook

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "ourbook.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "book"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAMA = "nama"
        private const val COLUMN_NAMA_PANGGILAN = "nama_panggilan"
        private const val COLUMN_POTO = "poto"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_ALAMAT = "alamat"
        private const val COLUMN_TGL_LAHIR = "tgl_lahir"
        private const val COLUMN_TELEPON = "telepon"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAMA TEXT NOT NULL, $COLUMN_NAMA_PANGGILAN TEXT NOT NULL, $COLUMN_POTO BLOB NOT NULL, $COLUMN_EMAIL TEXT NOT NULL, $COLUMN_ALAMAT TEXT NOT NULL, $COLUMN_TGL_LAHIR TEXT NOT NULL, $COLUMN_TELEPON TEXT NOT NULL)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertBook(book: Book) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAMA, book.nama)
            put(COLUMN_NAMA_PANGGILAN, book.nama_panggilan)
            put(COLUMN_POTO, book.poto)
            put(COLUMN_EMAIL, book.email)
            put(COLUMN_ALAMAT, book.alamat)
            put(COLUMN_TGL_LAHIR, book.tgl_lahir)
            put(COLUMN_TELEPON, book.telepon)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllBooks(): List<Book> {
        val booksList = mutableListOf<Book>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery (query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA))
            val nama_panggilan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_PANGGILAN))
            val poto = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_POTO))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val alamat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT))
            val tgl_lahir = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TGL_LAHIR))
            val telepon = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEPON))

            val book = Book(id, nama, nama_panggilan, poto, email, alamat, tgl_lahir, telepon)
            booksList.add(book)
        }
        cursor.close()
        db.close()
        return booksList
    }

    fun updateBook(book: Book): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAMA, book.nama)
            put(COLUMN_NAMA_PANGGILAN, book.nama_panggilan)
            put(COLUMN_POTO, book.poto)
            put(COLUMN_EMAIL, book.email)
            put(COLUMN_ALAMAT, book.alamat)
            put(COLUMN_TGL_LAHIR, book.tgl_lahir)
            put(COLUMN_TELEPON, book.telepon)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(book.id.toString())
        val rowsAffected = db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
        return rowsAffected > 0
    }

    fun getBookById(bookId: Int): Book {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $bookId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA))
        val nama_panggilan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_PANGGILAN))
        val poto = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_POTO))
        val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
        val alamat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT))
        val tgl_lahir = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TGL_LAHIR))
        val telepon = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEPON))
        cursor.close()
        db.close()
        return Book(id, nama, nama_panggilan, poto, email, alamat, tgl_lahir, telepon)
    }

    fun deleteBook(bookId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(bookId.toString())
        db.delete (TABLE_NAME, whereClause, whereArgs)
        db.close()
    }
}