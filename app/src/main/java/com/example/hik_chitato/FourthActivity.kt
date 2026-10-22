package com.example.hik_chitato

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.hik_chitato.databinding.ActivityFourthBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

open class FourthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFourthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFourthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e("onCreate", "FourthActivity Salon Oscardo dibuat")

        // Toolbar navigation back button
        binding.toolbarFourth.setNavigationOnClickListener {
            finish()
        }

        // Ambil data dari Intent extra (Pertemuan 4)
        val name = intent.getStringExtra("name") ?: "Politeknik Caltex Riau"
        val from = intent.getStringExtra("from") ?: "Rumbai"
        val age = intent.getIntExtra("age", 25)

        binding.tvIntentName.text = "Name: $name"
        binding.tvIntentFrom.text = "From: $from"
        binding.tvIntentAge.text = "Age: $age"

        Log.e("Data Intent", "Name: $name, From: $from, Age: $age")

        // Tombol Snackbar Status Booking
        binding.btnHowSnackbar.setOnClickListener {
            Snackbar.make(binding.root, "Status Booking: Terkonfirmasi di Salon Oscardo!", Snackbar.LENGTH_SHORT)
                .setAction("Tutup") {
                    Log.e("Info Snackbar", "Snackbar ditutup")
                }
                .show()
        }

        // Tombol AlertDialog Pembatalan
        binding.btnHowAlertDialog.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Konfirmasi Pembatalan")
                .setMessage("Apakah Anda yakin ingin membatalkan jadwal booking layanan salon ini?")
                .setPositiveButton("Ya") { dialog, _ ->
                    Log.e("Info Dialog", "User membatalkan booking")
                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    Log.e("Info Dialog", "User menutup dialog")
                    dialog.dismiss()
                }
                .show()
        }

        // Tombol Kembali
        binding.btnBackToMain.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.e("onStart", "FourthActivity Salon Oscardo terlihat di layer")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy", "FourthActivity Salon Oscardo dihapus dari stack")
    }
}