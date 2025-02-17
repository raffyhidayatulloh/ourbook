package com.raffy.ourbook

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.raffy.ourbook.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("id")) {
            binding.tvNama.text = intent.getStringExtra("nama") ?: "Nama tidak tersedia"
            binding.tvNamaPanggilan.text = "Nama Panggilan: ${intent.getStringExtra("nama_panggilan") ?: "Nama panggilan tidak tersedia"}"
            binding.tvEmail.text = "Email: ${intent.getStringExtra("email") ?: "-"}"
            binding.tvTelepon.text = "Telepon: ${intent.getStringExtra("telepon") ?: "-"}"
            binding.tvTanggalLahir.text = "Tanggal Lahir: ${intent.getStringExtra("tgl_lahir") ?: "-"}"
            binding.tvAlamat.text = "Alamat: ${intent.getStringExtra("alamat") ?: "-"}"

            // Decode photo if available
            val photo = intent.getByteArrayExtra("poto")
            if (photo != null) {
                val bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.size)
                binding.ivPoto.setImageBitmap(bitmap)
            } else {
                binding.ivPoto.setImageResource(R.drawable.ic_launcher_background) // Gambar default
            }
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}