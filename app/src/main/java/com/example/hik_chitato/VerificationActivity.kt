package com.example.hik_chitato

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hik_chitato.databinding.ActivityVerificationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class VerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarVerification.setNavigationOnClickListener {
            finish()
        }

        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)

        // Ambil semua data dari SharedPreferences
        val nama = sharedPref.getString("reg_nama", "") ?: ""
        val email = sharedPref.getString("reg_email", "") ?: ""
        val tglLahir = sharedPref.getString("reg_tgl_lahir", "") ?: ""
        val jenisKelamin = sharedPref.getString("reg_jenis_kelamin", "") ?: ""
        val username = sharedPref.getString("reg_username", "") ?: ""
        val password = sharedPref.getString("reg_password", "") ?: ""
        val confirmPassword = sharedPref.getString("reg_confirm_password", "") ?: ""

        // Tampilkan ke TextView
        binding.tvNama.text = nama
        binding.tvEmail.text = email
        binding.tvTglLahir.text = tglLahir
        binding.tvJenisKelamin.text = jenisKelamin
        binding.tvUsername.text = username
        binding.tvPassword.text = "*".repeat(password.length)
        binding.tvConfirmPassword.text = "*".repeat(confirmPassword.length)

        // Tombol Kembali
        binding.btnKembali.setOnClickListener {
            finish()
        }

        // Tombol Submit
        binding.btnSubmit.setOnClickListener {
            if (nama.isEmpty() || email.isEmpty() || tglLahir.isEmpty() ||
                jenisKelamin.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Data Tidak Lengkap")
                    .setMessage("Masih ada data yang kosong. Silahkan kembali dan lengkapi semua isian.")
                    .setPositiveButton("Kembali ke Registrasi") { _, _ ->
                        finish()
                    }
                    .show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Password Tidak Sama")
                    .setMessage("Password dan Konfirmasi Password tidak sama. Silahkan kembali dan perbaiki.")
                    .setPositiveButton("Kembali ke Registrasi") { _, _ ->
                        finish()
                    }
                    .show()
                return@setOnClickListener
            }

            // Registrasi Berhasil
            MaterialAlertDialogBuilder(this)
                .setTitle("Registrasi Berhasil!")
                .setMessage("Akun Anda berhasil didaftarkan. Silahkan login dengan username dan password yang telah dibuat.")
                .setPositiveButton("Login Sekarang") { _, _ ->
                    val intent = Intent(this, AuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
                .show()
        }
    }
}