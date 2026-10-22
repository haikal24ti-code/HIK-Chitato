package com.example.hik_chitato

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hik_chitato.databinding.FragmentInteractiveBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class InteractiveFragment : Fragment() {

    private var _binding: FragmentInteractiveBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInteractiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Launch FourthActivity dengan Intent Extra Booking (Image 3)
        binding.btnLaunchFourth.setOnClickListener {
            val intent = Intent(requireContext(), FourthActivity::class.java).apply {
                putExtra("name", "Politeknik Caltex Riau")
                putExtra("from", "Rumbai")
                putExtra("age", 25)
            }
            startActivity(intent)
        }

        // Display Snackbar Status Booking (Image 3)
        binding.btnFragSnackbar.setOnClickListener {
            Snackbar.make(binding.root, "Status Booking: Jadwal Layanan Terkonfirmasi di Salon Oscardo!", Snackbar.LENGTH_SHORT)
                .setAction("Tutup") {
                    Log.e("Info Snackbar", "Snackbar Booking ditutup")
                }
                .show()
        }

        // Display AlertDialog Pembatalan (Image 3)
        binding.btnFragAlert.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Konfirmasi Pembatalan")
                .setMessage("Apakah Anda yakin ingin membatalkan jadwal booking layanan salon ini?")
                .setPositiveButton("Ya") { dialog, _ ->
                    Log.e("Info Dialog", "User membatalkan booking")
                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    Log.e("Info Dialog", "User membatalkan dialog")
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}