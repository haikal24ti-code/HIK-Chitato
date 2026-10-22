package com.example.hik_chitato

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hik_chitato.databinding.ActivityRegisterBinding
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarRegister.setNavigationOnClickListener {
            finish()
        }

        // DatePicker untuk Tanggal Lahir
        binding.edtTglLahir.setOnClickListener {
            showDatePicker()
        }
        binding.tilTglLahir.setEndIconOnClickListener {
            showDatePicker()
        }

        // Tombol Selanjutnya
        binding.btnSelanjutnya.setOnClickListener {
            val nama = binding.edtNama.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val tglLahir = binding.edtTglLahir.text.toString().trim()
            val username = binding.edtRegUsername.text.toString().trim()
            val password = binding.edtRegPassword.text.toString().trim()
            val confirmPassword = binding.edtConfirmPassword.text.toString().trim()
            val jenisKelamin = when (binding.rgJenisKelamin.checkedRadioButtonId) {
                R.id.rb_laki -> "Laki-laki"
                R.id.rb_perempuan -> "Perempuan"
                else -> ""
            }

            // Validasi tidak boleh kosong
            if (nama.isEmpty()) {
                binding.tilNama.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            } else binding.tilNama.error = null

            if (email.isEmpty()) {
                binding.tilEmail.error = "Email tidak boleh kosong"
                return@setOnClickListener
            } else binding.tilEmail.error = null

            if (tglLahir.isEmpty()) {
                binding.tilTglLahir.error = "Tanggal lahir tidak boleh kosong"
                return@setOnClickListener
            } else binding.tilTglLahir.error = null

            if (jenisKelamin.isEmpty()) {
                Toast.makeText(this, "Pilih jenis kelamin terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (username.isEmpty()) {
                binding.tilRegUsername.error = "Username tidak boleh kosong"
                return@setOnClickListener
            } else binding.tilRegUsername.error = null

            if (password.isEmpty()) {
                binding.tilRegPassword.error = "Password tidak boleh kosong"
                return@setOnClickListener
            } else binding.tilRegPassword.error = null

            if (confirmPassword.isEmpty()) {
                binding.tilConfirmPassword.error = "Konfirmasi password tidak boleh kosong"
                return@setOnClickListener
            } else binding.tilConfirmPassword.error = null

            if (password != confirmPassword) {
                binding.tilConfirmPassword.error = "Password tidak sama"
                return@setOnClickListener
            } else binding.tilConfirmPassword.error = null

            // Simpan ke SharedPreferences
            val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
            sharedPref.edit().apply {
                putString("reg_nama", nama)
                putString("reg_email", email)
                putString("reg_tgl_lahir", tglLahir)
                putString("reg_jenis_kelamin", jenisKelamin)
                putString("reg_username", username)
                putString("reg_password", password)
                putString("reg_confirm_password", confirmPassword)
                apply()
            }

            Toast.makeText(this, "Data tersimpan, silahkan verifikasi", Toast.LENGTH_SHORT).show()

            // Pindah ke VerificationActivity
            startActivity(Intent(this, VerificationActivity::class.java))
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formatted = "%02d/%02d/%04d".format(selectedDay, selectedMonth + 1, selectedYear)
            binding.edtTglLahir.setText(formatted)
        }, year, month, day).show()
    }
}