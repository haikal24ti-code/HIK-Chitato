package com.example.hik_chitato

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hik_chitato.databinding.FragmentHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Button Buka Website Official Salon (Image 2)
        binding.btnOpenWeb.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Website Official Salon Oscardo")
                .setMessage("Layanan Web Official Salon Oscardo sedang dalam pemeliharaan berkala.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // Button Demo (Image 2) -> Meluncurkan FourthActivity dengan Intent Extras
        binding.btnOpenFourth.setOnClickListener {
            val intent = Intent(requireContext(), FourthActivity::class.java).apply {
                putExtra("name", "Politeknik Caltex Riau")
                putExtra("from", "Rumbai")
                putExtra("age", 25)
            }
            startActivity(intent)
        }

        // Click listeners for Salon Services (Image 2)
        binding.cardService1.setOnClickListener {
            Toast.makeText(requireContext(), "Anda memilih: Potong Rambut Gentleman (Rp 50.000)", Toast.LENGTH_SHORT).show()
        }

        binding.cardService2.setOnClickListener {
            Toast.makeText(requireContext(), "Anda memilih: Hair Coloring & Highlight (Rp 150.000)", Toast.LENGTH_SHORT).show()
        }

        binding.cardService3.setOnClickListener {
            Toast.makeText(requireContext(), "Anda memilih: Creambath Premium (Rp 85.000)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}