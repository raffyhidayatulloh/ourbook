package com.raffy.ourbook

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.raffy.ourbook.databinding.ActivityAddBinding
import java.io.ByteArrayOutputStream
import java.util.Calendar

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private lateinit var databaseHelper: DatabaseHelper
    private var userId: Int = -1
    private var userPhoto: ByteArray? = null

    companion object {
        const val REQUEST_CODE_CAMERA = 100
        const val REQUEST_STORAGE = 101
        const val REQUEST_CODE_GALLERY = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        if (intent.hasExtra("id")) {
            userId = intent.getIntExtra("id", -1)
            binding.etNama.setText(intent.getStringExtra("nama"))
            binding.etNamaPanggilan.setText(intent.getStringExtra("nama_panggilan"))
            binding.etEmail.setText(intent.getStringExtra("email"))
            binding.etAlamat.setText(intent.getStringExtra("alamat"))
            binding.tvTglLahir.setText(intent.getStringExtra("tgl_lahir"))
            binding.etTelepon.setText(intent.getStringExtra("telepon"))

            // Decode photo if available
            val photo = intent.getByteArrayExtra("poto")
            if (photo != null) {
                val bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.size)
                if (bitmap != null) {
                    binding.ivPoto.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
                userPhoto = photo
            }

            binding.tvTitle.text = "Update Book"
        }

        binding.btnCamera.setOnClickListener {
            checkCameraPermission()
        }

        binding.btnFile.setOnClickListener {
            openGallery()
        }

        binding.btnSave.setOnClickListener {
            if (userId == -1) {
                saveUserBook()
            } else {
                updateUserBook()
            }
        }

        binding.tvTglLahir.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.tvTglLahir.text = date
            }, year, month, day)
            datePicker.show()
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_STORAGE)
            openCamera()
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE_CAMERA)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.ivPoto.setImageBitmap(bitmap)

                    // Simpan gambar ke dalam byte array
                    userPhoto = bitmapToByteArray(bitmap)
                }
                REQUEST_CODE_GALLERY -> {
                    val imageUri = data?.data
                    if (imageUri != null) {
                        // Ubah Uri menjadi Bitmap sebelum menyimpannya dalam userPhoto
                        val inputStream = contentResolver.openInputStream(imageUri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()

                        if (bitmap != null) {
                            binding.ivPoto.setImageBitmap(bitmap)
                            userPhoto = bitmapToByteArray(bitmap) // Ubah ke byte array
                        } else {
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    private fun saveUserBook() {
        val name = binding.etNama.text.toString().trim()
        val nickname = binding.etNamaPanggilan.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val address = binding.etAlamat.text.toString().trim()
        val birthDate = binding.tvTglLahir.text.toString().trim()
        val phone = binding.etTelepon.text.toString().trim()

        if (name.isEmpty() || nickname.isEmpty() || email.isEmpty() || birthDate.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "All fields except address are required!", Toast.LENGTH_SHORT).show()
            return
        }

        val data = userPhoto?.let {
            Book(
                id = 0,
                nama = name,
                telepon = phone,
                email = email,
                nama_panggilan = nickname,
                poto = it,
                alamat = address,
                tgl_lahir = birthDate
            )
        }
        if (data != null) {
            databaseHelper.insertBook(data)
        }
        Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun updateUserBook() {
        val name = binding.etNama.text.toString().trim()
        val nickname = binding.etNamaPanggilan.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val address = binding.etAlamat.text.toString().trim()
        val birthDate = binding.tvTglLahir.text.toString().trim()
        val phone = binding.etTelepon.text.toString().trim()

        if (name.isEmpty() || nickname.isEmpty() || email.isEmpty() || birthDate.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "All fields except address are required!", Toast.LENGTH_SHORT).show()
            return
        }

        val data = userPhoto?.let {
            Book(
                id = userId,
                nama = name,
                telepon = phone,
                email = email,
                nama_panggilan = nickname,
                poto = it,
                alamat = address,
                tgl_lahir = birthDate
            )
        }
        val isSuccess = data?.let { databaseHelper.updateBook(it) }
        if (isSuccess == true) {
            Toast.makeText(this, "User updated successfully!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to update user", Toast.LENGTH_SHORT).show()
        }
    }
}