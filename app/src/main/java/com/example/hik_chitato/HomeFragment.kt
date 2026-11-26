package com.example.hik_chitato

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hik_chitato.data.repository.SalonServiceRepository
import com.example.hik_chitato.databinding.FragmentHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var customAdapter: CustomAdapter
    private val serviceRepository = SalonServiceRepository()
    private var allServices: MutableList<SalonService> = mutableListOf()

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

        binding.btnAddService.setOnClickListener {
            showAddServiceDialog()
        }

        // Load data dari Supabase Database
        loadServicesFromSupabase()
    }

    private fun setupQuickActionButtons() {
        binding.btnOpenWeb.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Website Official Salon Oscardo")
                .setMessage("Layanan Web Official Salon Oscardo sedang dalam pemeliharaan berkala.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        }

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
        customAdapter = CustomAdapter(requireContext(), allServices) { service ->
            showServiceActionOptions(service)
        }
        binding.listViewServices.adapter = customAdapter
    }

    private fun loadServicesFromSupabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            val result = serviceRepository.getAllServices()

            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { supabaseList ->
                        if (supabaseList.isEmpty()) {
                            // Seed data awal jika tabel salon_services di Supabase masih kosong
                            seedDefaultServices()
                        } else {
                            allServices.clear()
                            supabaseList.forEachIndexed { index, item ->
                                val iconRes = getIconForServiceName(item.name)
                                val formattedPrice = "Rp %,d".format(item.price.toLong()).replace(',', '.')
                                allServices.add(
                                    SalonService(
                                        id = index + 1,
                                        dbId = item.id,
                                        name = item.name,
                                        description = item.description,
                                        price = formattedPrice,
                                        iconResId = iconRes
                                    )
                                )
                            }
                            customAdapter.updateData(allServices)
                            setListViewHeightBasedOnChildren(binding.listViewServices)
                        }
                    },
                    onFailure = { error ->
                        Toast.makeText(requireContext(), "Gagal memuat layanan dari Supabase: ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    private fun seedDefaultServices() {
        lifecycleScope.launch(Dispatchers.IO) {
            serviceRepository.createService("Potong Rambut Gentleman", "Guntingan modern dan stylish untuk pria sejati.", 50000.0)
            serviceRepository.createService("Hair Coloring & Highlight", "Teknik pewarnaan rambut terkini untuk warna cerah dan highlights natural.", 150000.0)
            serviceRepository.createService("Creambath Premium", "Perawatan rambut mendalam dengan krim pilihan dan pijatan relaksasi.", 85000.0)

            withContext(Dispatchers.Main) {
                loadServicesFromSupabase()
            }
        }
    }

    private fun getIconForServiceName(name: String): Int {
        val lower = name.lowercase()
        return when {
            lower.contains("color") || lower.contains("pewarnaan") -> R.drawable.ic_hair_color
            lower.contains("cream") || lower.contains("spa") -> R.drawable.ic_creambath
            lower.contains("style") || lower.contains("wash") -> R.drawable.ic_salon_scissors_comb
            else -> R.drawable.ic_scissors
        }
    }

    private fun showAddServiceDialog() {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val edtName = EditText(requireContext()).apply {
            hint = "Nama Layanan"
        }
        val edtDesc = EditText(requireContext()).apply {
            hint = "Deskripsi Layanan"
        }
        val edtPrice = EditText(requireContext()).apply {
            hint = "Harga (contoh: 50000)"
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        layout.addView(edtName)
        layout.addView(edtDesc)
        layout.addView(edtPrice)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Tambah Layanan Salon Baru")
            .setView(layout)
            .setPositiveButton("Simpan (Supabase)") { _, _ ->
                val name = edtName.text.toString().trim()
                val desc = edtDesc.text.toString().trim()
                val priceDouble = edtPrice.text.toString().toDoubleOrNull() ?: 0.0

                if (name.isNotEmpty() && priceDouble > 0.0) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val res = serviceRepository.createService(name, desc, priceDouble)
                        withContext(Dispatchers.Main) {
                            res.fold(
                                onSuccess = {
                                    Toast.makeText(requireContext(), "Layanan berhasil ditambahkan ke Supabase!", Toast.LENGTH_SHORT).show()
                                    loadServicesFromSupabase()
                                },
                                onFailure = { err ->
                                    Toast.makeText(requireContext(), "Gagal menyimpan: ${err.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Nama dan Harga harus diisi dengan benar!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal") { d, _ -> d.dismiss() }
            .show()
    }

    private fun showServiceActionOptions(service: SalonService) {
        val options = arrayOf("Edit Layanan (Update Supabase)", "Hapus Layanan (Delete Supabase)")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(service.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditServiceDialog(service)
                    1 -> confirmDeleteService(service)
                }
            }
            .setNegativeButton("Tutup") { d, _ -> d.dismiss() }
            .show()
    }

    private fun showEditServiceDialog(service: SalonService) {
        val dbId = service.dbId ?: return

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val rawPrice = service.price.replace("[^0-9]".toRegex(), "")

        val edtName = EditText(requireContext()).apply {
            hint = "Nama Layanan"
            setText(service.name)
        }
        val edtDesc = EditText(requireContext()).apply {
            hint = "Deskripsi Layanan"
            setText(service.description)
        }
        val edtPrice = EditText(requireContext()).apply {
            hint = "Harga"
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(rawPrice)
        }

        layout.addView(edtName)
        layout.addView(edtDesc)
        layout.addView(edtPrice)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Layanan Salon")
            .setView(layout)
            .setPositiveButton("Update (Supabase)") { _, _ ->
                val name = edtName.text.toString().trim()
                val desc = edtDesc.text.toString().trim()
                val priceDouble = edtPrice.text.toString().toDoubleOrNull() ?: 0.0

                if (name.isNotEmpty() && priceDouble > 0.0) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val res = serviceRepository.updateService(dbId, name, desc, priceDouble)
                        withContext(Dispatchers.Main) {
                            res.fold(
                                onSuccess = {
                                    Toast.makeText(requireContext(), "Layanan berhasil diperbarui di Supabase!", Toast.LENGTH_SHORT).show()
                                    loadServicesFromSupabase()
                                },
                                onFailure = { err ->
                                    Toast.makeText(requireContext(), "Gagal memperbarui: ${err.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
            .setNegativeButton("Batal") { d, _ -> d.dismiss() }
            .show()
    }

    private fun confirmDeleteService(service: SalonService) {
        val dbId = service.dbId ?: return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Konfirmasi Hapus Layanan")
            .setMessage("Apakah Anda yakin ingin menghapus layanan \"${service.name}\" dari database Supabase?")
            .setPositiveButton("Hapus") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    val res = serviceRepository.deleteService(dbId)
                    withContext(Dispatchers.Main) {
                        res.fold(
                            onSuccess = {
                                Toast.makeText(requireContext(), "Layanan berhasil dihapus dari Supabase!", Toast.LENGTH_SHORT).show()
                                loadServicesFromSupabase()
                            },
                            onFailure = { err ->
                                Toast.makeText(requireContext(), "Gagal menghapus: ${err.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
            .setNegativeButton("Batal") { d, _ -> d.dismiss() }
            .show()
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
