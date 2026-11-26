package com.example.hik_chitato

import android.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hik_chitato.data.model.Booking
import com.example.hik_chitato.data.model.SalonService
import com.example.hik_chitato.data.repository.AuthRepository
import com.example.hik_chitato.data.repository.BookingRepository
import com.example.hik_chitato.data.repository.SalonServiceRepository
import com.example.hik_chitato.databinding.FragmentInteractiveBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class InteractiveFragment : Fragment() {

    private var _binding: FragmentInteractiveBinding? = null
    private val binding get() = _binding!!

    private val authRepository = AuthRepository()
    private val bookingRepository = BookingRepository()
    private val serviceRepository = SalonServiceRepository()

    private lateinit var bookingAdapter: BookingAdapter
    private var availableServices: List<SalonService> = emptyList()
    private var userBookings: List<Booking> = emptyList()

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

        // Feature 1: Launch FourthActivity via Intent
        binding.btnLaunchFourth.setOnClickListener {
            val intent = Intent(requireContext(), FourthActivity::class.java).apply {
                putExtra("name", "Politeknik Caltex Rumbai")
                putExtra("from", "Booking Menu")
                putExtra("age", 25)
            }
            startActivity(intent)
        }

        // Feature 2: Display Snackbar
        binding.btnFragSnackbar.setOnClickListener {
            Snackbar.make(binding.root, "Status Booking: Jadwal Layanan Terkonfirmasi di Salon Oscardo!", Snackbar.LENGTH_SHORT)
                .setAction("Tutup") {
                    Log.e("Info Snackbar", "Snackbar Booking ditutup")
                }
                .show()
        }

        // Feature 3: Display AlertDialog
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

        // Feature 4: Online Booking Management (Supabase CRUD)
        setupBookingRecyclerView()
        binding.btnAddBooking.setOnClickListener {
            showAddBookingDialog()
        }

        loadDataFromSupabase()
    }

    private fun setupBookingRecyclerView() {
        bookingAdapter = BookingAdapter(
            bookingList = emptyList(),
            serviceMap = emptyMap(),
            onEditClick = { booking -> showEditBookingDialog(booking) },
            onDeleteClick = { booking -> confirmDeleteBooking(booking) }
        )
        binding.rvBookings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBookings.adapter = bookingAdapter
    }

    private fun loadDataFromSupabase() {
        val currentUserId = authRepository.getCurrentUserId()
        if (currentUserId == null) {
            binding.tvEmptyBookings.visibility = View.VISIBLE
            binding.tvEmptyBookings.text = "Silahkan login terlebih dahulu untuk mengelola booking."
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val servicesResult = serviceRepository.getAllServices()
            val bookingsResult = bookingRepository.getBookingsByUser(currentUserId)

            withContext(Dispatchers.Main) {
                availableServices = servicesResult.getOrDefault(emptyList())
                val serviceMap = availableServices.mapNotNull { s -> s.id?.let { id -> id to s } }.toMap()

                bookingsResult.fold(
                    onSuccess = { list ->
                        userBookings = list
                        bookingAdapter.updateData(userBookings, serviceMap)
                        val isEmpty = userBookings.isEmpty()
                        binding.tvEmptyBookings.visibility = if (isEmpty) View.VISIBLE else View.GONE
                        if (isEmpty) {
                            binding.tvEmptyBookings.text = "Belum ada data booking di Supabase. Klik tombol '+ Buat Booking' di atas."
                        }
                    },
                    onFailure = { err ->
                        Toast.makeText(requireContext(), "Gagal memuat booking: ${err.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    private fun showAddBookingDialog() {
        val currentUserId = authRepository.getCurrentUserId()
        if (currentUserId == null) {
            Toast.makeText(requireContext(), "Silahkan login terlebih dahulu!", Toast.LENGTH_SHORT).show()
            return
        }

        if (availableServices.isEmpty()) {
            Toast.makeText(requireContext(), "Memuat layanan salon...", Toast.LENGTH_SHORT).show()
            return
        }

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val tvLabelService = TextView(requireContext()).apply {
            text = "Pilih Layanan Salon:"
            textSize = 14f
        }

        val spinnerService = Spinner(requireContext())
        val serviceNames = availableServices.map { "${it.name} (Rp %,d)".format(it.price.toLong()) }
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, serviceNames)
        spinnerService.adapter = adapter

        val edtDate = EditText(requireContext()).apply {
            hint = "Pilih Tanggal Booking (DD/MM/YYYY)"
            isFocusable = false
        }

        val edtTime = EditText(requireContext()).apply {
            hint = "Pilih Waktu Booking (HH:MM)"
            isFocusable = false
        }

        val edtNotes = EditText(requireContext()).apply {
            hint = "Catatan Tambahan (opsional)"
        }

        edtDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                val formatted = "%02d/%02d/%04d".format(d, m + 1, y)
                edtDate.setText(formatted)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        edtTime.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, hour, min ->
                val formatted = "%02d:%02d".format(hour, min)
                edtTime.setText(formatted)
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        layout.addView(tvLabelService)
        layout.addView(spinnerService)
        layout.addView(edtDate)
        layout.addView(edtTime)
        layout.addView(edtNotes)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Buat Booking Layanan Baru")
            .setView(layout)
            .setPositiveButton("Simpan Booking (Supabase)") { _, _ ->
                val selectedPos = spinnerService.selectedItemPosition
                val selectedService = availableServices.getOrNull(selectedPos)
                val dateStr = edtDate.text.toString().trim()
                val timeStr = edtTime.text.toString().trim()
                val notesStr = edtNotes.text.toString().trim()

                if (selectedService?.id != null && dateStr.isNotEmpty() && timeStr.isNotEmpty()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val res = bookingRepository.createBooking(
                            userId = currentUserId,
                            serviceId = selectedService.id,
                            bookingDate = dateStr,
                            bookingTime = timeStr,
                            notes = notesStr.ifEmpty { null }
                        )

                        withContext(Dispatchers.Main) {
                            res.fold(
                                onSuccess = {
                                    Toast.makeText(requireContext(), "Booking berhasil disimpan ke Supabase!", Toast.LENGTH_SHORT).show()
                                    loadDataFromSupabase()
                                },
                                onFailure = { err ->
                                    Toast.makeText(requireContext(), "Gagal menyimpan booking: ${err.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Layanan, Tanggal, dan Waktu wajib dipilih!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal") { d, _ -> d.dismiss() }
            .show()
    }

    private fun showEditBookingDialog(booking: Booking) {
        val bookingId = booking.id ?: return

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val tvLabelService = TextView(requireContext()).apply {
            text = "Ubah Layanan Salon:"
            textSize = 14f
        }

        val spinnerService = Spinner(requireContext())
        val serviceNames = availableServices.map { "${it.name} (Rp %,d)".format(it.price.toLong()) }
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, serviceNames)
        spinnerService.adapter = adapter

        val currentServiceIdx = availableServices.indexOfFirst { it.id == booking.serviceId }
        if (currentServiceIdx >= 0) spinnerService.setSelection(currentServiceIdx)

        val edtDate = EditText(requireContext()).apply {
            hint = "Pilih Tanggal Booking"
            isFocusable = false
            setText(booking.bookingDate)
        }

        val edtTime = EditText(requireContext()).apply {
            hint = "Pilih Waktu Booking"
            isFocusable = false
            setText(booking.bookingTime)
        }

        val edtNotes = EditText(requireContext()).apply {
            hint = "Catatan Tambahan"
            setText(booking.notes.orEmpty())
        }

        edtDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                val formatted = "%02d/%02d/%04d".format(d, m + 1, y)
                edtDate.setText(formatted)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        edtTime.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, hour, min ->
                val formatted = "%02d:%02d".format(hour, min)
                edtTime.setText(formatted)
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        layout.addView(tvLabelService)
        layout.addView(spinnerService)
        layout.addView(edtDate)
        layout.addView(edtTime)
        layout.addView(edtNotes)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Jadwal Booking")
            .setView(layout)
            .setPositiveButton("Update Booking (Supabase)") { _, _ ->
                val selectedPos = spinnerService.selectedItemPosition
                val selectedService = availableServices.getOrNull(selectedPos)
                val dateStr = edtDate.text.toString().trim()
                val timeStr = edtTime.text.toString().trim()
                val notesStr = edtNotes.text.toString().trim()

                if (selectedService?.id != null && dateStr.isNotEmpty() && timeStr.isNotEmpty()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val res = bookingRepository.updateBooking(
                            id = bookingId,
                            serviceId = selectedService.id,
                            bookingDate = dateStr,
                            bookingTime = timeStr,
                            notes = notesStr.ifEmpty { null }
                        )

                        withContext(Dispatchers.Main) {
                            res.fold(
                                onSuccess = {
                                    Toast.makeText(requireContext(), "Booking berhasil diperbarui di Supabase!", Toast.LENGTH_SHORT).show()
                                    loadDataFromSupabase()
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

    private fun confirmDeleteBooking(booking: Booking) {
        val bookingId = booking.id ?: return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Konfirmasi Pembatalan Booking")
            .setMessage("Apakah Anda yakin ingin membatalkan/menghapus booking ini dari database Supabase?")
            .setPositiveButton("Batal/Hapus") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    val res = bookingRepository.deleteBooking(bookingId)

                    withContext(Dispatchers.Main) {
                        res.fold(
                            onSuccess = {
                                Toast.makeText(requireContext(), "Booking berhasil dibatalkan dari Supabase!", Toast.LENGTH_SHORT).show()
                                loadDataFromSupabase()
                            },
                            onFailure = { err ->
                                Toast.makeText(requireContext(), "Gagal membatalkan: ${err.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
            .setNegativeButton("Kembali") { d, _ -> d.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}