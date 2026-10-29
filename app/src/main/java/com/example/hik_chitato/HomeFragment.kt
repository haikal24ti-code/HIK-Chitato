package com.example.hik_chitato

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
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

        // Fitur Baru 1: Fitur Pencarian / Filter Katalog Layanan Real-time
        setupSearchFilter()
    }

    private fun setupSearchFilter() {
        binding.searchViewService.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterServices(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterServices(newText)
                return true
            }
        })
    }

    private fun filterServices(query: String?) {
        val q = query?.trim()?.lowercase().orEmpty()

        val service1Text = "potong rambut gentleman guntingan modern dan stylish untuk pria sejati rp 50.000"
        val service2Text = "hair coloring & highlight teknik pewarnaan rambut terkini untuk warna cerah dan highlights natural rp 150.000"
        val service3Text = "creambath premium perawatan rambut mendalam dengan krim pilihan dan pijatan relaksasi rp 85.000"

        val match1 = q.isEmpty() || service1Text.contains(q)
        val match2 = q.isEmpty() || service2Text.contains(q)
        val match3 = q.isEmpty() || service3Text.contains(q)

        binding.cardService1.visibility = if (match1) View.VISIBLE else View.GONE
        binding.cardService2.visibility = if (match2) View.VISIBLE else View.GONE
        binding.cardService3.visibility = if (match3) View.VISIBLE else View.GONE

        val noResults = !match1 && !match2 && !match3
        binding.tvEmptySearch.visibility = if (noResults) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}