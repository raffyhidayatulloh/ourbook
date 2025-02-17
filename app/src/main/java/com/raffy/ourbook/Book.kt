package com.raffy.ourbook

data class Book(
    val id: Int,
    val nama: String,
    val nama_panggilan: String,
    val poto: ByteArray,
    val email: String,
    val alamat: String,
    val tgl_lahir: String,
    val telepon: String
)
