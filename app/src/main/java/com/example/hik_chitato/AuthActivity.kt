package com.example.hik_chitato

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.hik_chitato.data.repository.AuthRepository
import com.example.hik_chitato.databinding.ActivityAuthBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAuth.setNavigationOnClickListener {
            finish()
        }

        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)

        // Pengecekan status login di Supabase Auth / SharedPreferences
        if (authRepository.isUserLoggedIn() || sharedPref.getBoolean("isLogin", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnlogin.setOnClickListener {
            val inputEmail = binding.edtUsername.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (inputEmail.isEmpty()) {
                binding.tilUsername.error = "Email tidak boleh kosong"
                return@setOnClickListener
            } else binding.tilUsername.error = null

            if (password.isEmpty()) {
                binding.tilPassword.error = "Password tidak boleh kosong"
                return@setOnClickListener
            } else binding.tilPassword.error = null

            binding.btnlogin.isEnabled = false
            binding.btnRegister.isEnabled = false

            lifecycleScope.launch(Dispatchers.IO) {
                val result = authRepository.signIn(inputEmail, password)

                withContext(Dispatchers.Main) {
                    binding.btnlogin.isEnabled = true
                    binding.btnRegister.isEnabled = true

                    result.fold(
                        onSuccess = {
                            sharedPref.edit().apply {
                                putBoolean("isLogin", true)
                                putString("username", inputEmail)
                                apply()
                            }
                            Toast.makeText(
                                this@AuthActivity,
                                "Login Berhasil! Selamat datang di Salon Oscardo",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                            finish()
                        },
                        onFailure = { error ->
                            MaterialAlertDialogBuilder(this@AuthActivity)
                                .setTitle("Login Gagal")
                                .setMessage("Email atau Password tidak sesuai dengan data di Supabase Auth! (${error.localizedMessage ?: "Periksa credential & koneksi Anda"})")
                                .setPositiveButton("Coba Lagi") { dialog, _ -> dialog.dismiss() }
                                .show()
                        }
                    )
                }
            }
        }
    }
}
