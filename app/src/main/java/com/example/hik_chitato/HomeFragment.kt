package com.example.hik_chitato

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hik_chitato.databinding.FragmentHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var customAdapter: CustomAdapter
    private lateinit var allServices: List<SalonService>

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

        setupQuickActionButtons()
        setupHomeGridRecyclerView()
        setupServicesCustomAdapter()
        setupSearchFilter()
    }

    private fun setupQuickActionButtons() {
        // Tombol Buka Website Official Salon Oscardo
        binding.btnOpenWeb.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Website Official Salon Oscardo")
                .setMessage("Layanan Web Official Salon Oscardo sedang dalam pemeliharaan berkala.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // Tombol Demo -> Intent ke FourthActivity
        binding.btnOpenFourth.setOnClickListener {
            val intent = Intent(requireContext(), FourthActivity::class.java).apply {
                putExtra("name", "Politeknik Caltex Rumbai")
                putExtra("from", "Salon Oscardo")
                putExtra("age", 25)
            }
            startActivity(intent)
        }
    }

    private fun setupHomeGridRecyclerView() {
        val menuList = listOf(
            HomeMenuItem(
                id = 1,
                title = "Layanan Salon",
                subtitle = "Katalog & perawatan",
                iconResId = R.drawable.ic_scissors
            ),
            HomeMenuItem(
                id = 2,
                title = "Booking",
                subtitle = "Jadwalkan perawatan",
                iconResId = R.drawable.ic_interactive
            ),
            HomeMenuItem(
                id = 3,
                title = "Produk Salon",
                subtitle = "Produk kecantikan",
                iconResId = R.drawable.ic_creambath
            ),
            HomeMenuItem(
                id = 4,
                title = "Informasi Salon",
                subtitle = "Lokasi, jam & kontak",
                iconResId = R.drawable.ic_web
            )
        )

        val gridAdapter = HomeMenuAdapter(menuList) { item ->
            when (item.id) {
                1 -> {
                    Toast.makeText(requireContext(), "Menampilkan Katalog Layanan Salon", Toast.LENGTH_SHORT).show()
                    binding.scrollViewHome.smoothScrollTo(0, binding.tvCatalogHeader.top)
                }
                2 -> {
                    val intent = Intent(requireContext(), FourthActivity::class.java).apply {
                        putExtra("name", "Politeknik Caltex Rumbai")
                        putExtra("from", "Booking Menu")
                        putExtra("age", 25)
                    }
                    startActivity(intent)
                }
                3 -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Produk Salon Oscardo")
                        .setMessage("Katalog Produk Premium Oscardo:\n\n1. Hair Serum Gold Edition - Rp120.000\n2. Oscardo Care Shampoo - Rp75.000\n3. Hair Spa Mask Avocado - Rp95.000")
                        .setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
                4 -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Informasi Salon Oscardo")
                        .setMessage("Salon Oscardo Premium\n📍 Lokasi: Rumbai, Pekanbaru\n⏰ Jam Operasional: 09.00 - 21.00 WIB\n📞 Kontak/WA: +62 812-3456-7890")
                        .setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
            }
        }

        binding.rvHomeGrid.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvHomeGrid.adapter = gridAdapter
    }

    private fun setupServicesCustomAdapter() {
        allServices = listOf(
            SalonService(
                id = 1,
                name = "Potong Rambut Gentleman",
                description = "Guntingan modern dan stylish untuk pria sejati.",
                price = "Rp 50.000",
                iconResId = R.drawable.ic_scissors
            ),
            SalonService(
                id = 2,
                name = "Hair Coloring & Highlight",
                description = "Teknik pewarnaan rambut terkini untuk warna cerah dan highlights natural.",
                price = "Rp 150.000",
                iconResId = R.drawable.ic_hair_color
            ),
            SalonService(
                id = 3,
                name = "Creambath Premium",
                description = "Perawatan rambut mendalam dengan krim pilihan dan pijatan relaksasi.",
                price = "Rp 85.000",
                iconResId = R.drawable.ic_creambath
            ),
            SalonService(
                id = 4,
                name = "Hair Styling & Wash",
                description = "Cuci rambut bersih dan styling profesional sesuai keinginan Anda.",
                price = "Rp 40.000",
                iconResId = R.drawable.ic_salon_scissors_comb
            ),
            SalonService(
                id = 5,
                name = "Facial & Hair Spa Treatment",
                description = "Perawatan wajah dan hair spa komplit untuk kesegaran maksimal.",
                price = "Rp 120.000",
                iconResId = R.drawable.ic_snack_chip
            )
        )

        customAdapter = CustomAdapter(requireContext(), allServices) { service ->
            Toast.makeText(
                requireContext(),
                "Anda memilih: ${service.name} (${service.price})",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.listViewServices.adapter = customAdapter
        binding.listViewServices.post {
            setListViewHeightBasedOnChildren(binding.listViewServices)
        }
    }

    private fun setupSearchFilter() {
        binding.searchViewService.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performFilter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                performFilter(newText)
                return true
            }
        })
    }

    private fun performFilter(query: String?) {
        customAdapter.filter(query)
        setListViewHeightBasedOnChildren(binding.listViewServices)

        val noResults = customAdapter.count == 0
        binding.tvEmptySearch.visibility = if (noResults) View.VISIBLE else View.GONE
    }

    private fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0
        val desiredWidth = View.MeasureSpec.makeMeasureSpec(
            listView.width.coerceAtLeast(1),
            View.MeasureSpec.UNSPECIFIED
        )
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (listAdapter.count - 1).coerceAtLeast(0))
        listView.layoutParams = params
        listView.requestLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
