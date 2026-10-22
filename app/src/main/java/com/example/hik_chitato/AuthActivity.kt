package com.example.hik_chitato

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hik_chitato.databinding.ActivityAuthBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar dengan Tombol Back (Image 1 Header)
        binding.toolbarAuth.setNavigationOnClickListener {
            finish()
        }

        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)

        // Pengecekan otomatis jika isLogin sudah true saat membuka AuthActivity
        val isLogin = sharedPref.getBoolean("isLogin", false)
        if (isLogin) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding.btnlogin.setOnClickListener {
            val username = binding.edtUsername.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            // Pengecekan validasi login Salon Oscardo
            if (username.isNotEmpty() && password.isNotEmpty() && (username == password || username.lowercase() == "admin")) {
                val editor = sharedPref.edit()
                editor.putBoolean("isLogin", true)
                editor.putString("username", username)
                editor.apply()

                Toast.makeText(this, "Login Berhasil! Selamat datang di Salon Oscardo, $username", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Tampilkan AlertDialog kegagalan login
                MaterialAlertDialogBuilder(this)
                    .setTitle("Login Gagal")
                    .setMessage("Username dan Password tidak sesuai! Silahkan periksa kembali credential Anda.")
                    .setPositiveButton("Coba Lagi") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }
}