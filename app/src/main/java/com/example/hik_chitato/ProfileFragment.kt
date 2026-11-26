package com.example.hik_chitato

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.hik_chitato.data.repository.AuthRepository
import com.example.hik_chitato.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val authRepository = AuthRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set Profil Developer
        binding.tvProfileName.text = getString(R.string.dev_name)
        binding.tvProfileNim.text = getString(R.string.dev_nim)

        // Fitur Rating & Ulasan Layanan Salon
        setupRatingAndReview()

        // Logout Feature (Supabase Auth signOut)
        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar dari akun Salon Oscardo dan kembali ke Halaman Login?")
                .setPositiveButton("Ya") { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        authRepository.signOut()

                        withContext(Dispatchers.Main) {
                            val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
                            sharedPref.edit().remove("isLogin").remove("username").apply()

                            Toast.makeText(requireContext(), "Berhasil Logout dari Salon Oscardo", Toast.LENGTH_SHORT).show()

                            val intent = Intent(requireContext(), AuthActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun setupRatingAndReview() {
        val reviewPref = requireContext().getSharedPreferences("review_pref", Context.MODE_PRIVATE)

        val savedRating = reviewPref.getFloat("saved_rating", 0f)
        val savedReview = reviewPref.getString("saved_review", "") ?: ""

        if (savedRating > 0f) {
            binding.ratingBarSalon.rating = savedRating
            binding.edtRatingReview.setText(savedReview)
            displaySavedReview(savedRating, savedReview)
        }

        binding.btnSubmitRating.setOnClickListener {
            val rating = binding.ratingBarSalon.rating
            val reviewText = binding.edtRatingReview.text.toString().trim()

            if (rating <= 0f) {
                Toast.makeText(requireContext(), "Silahkan berikan rating bintang (1-5) terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            reviewPref.edit().apply {
                putFloat("saved_rating", rating)
                putString("saved_review", reviewText)
                apply()
            }

            displaySavedReview(rating, reviewText)
            Toast.makeText(requireContext(), "Terima kasih! Rating & Ulasan Anda berhasil disimpan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displaySavedReview(rating: Float, review: String) {
        binding.layoutSavedReview.visibility = View.VISIBLE
        binding.tvSavedRatingScore.text = "Rating Anda: ⭐ $rating / 5.0"
        binding.tvSavedReviewText.text = if (review.isNotEmpty()) "Ulasan: \"$review\"" else "Ulasan: (Tanpa teks ulasan)"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
