package com.example.hik_chitato

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.hik_chitato.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingItems: List<OnboardingItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOnboardingItems()
        setupViewPager()
        setupIndicators()
        setCurrentIndicator(0)

        binding.btnSkip.setOnClickListener {
            finishOnboarding()
        }

        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPagerOnboarding.currentItem
            if (currentItem < onboardingItems.size - 1) {
                binding.viewPagerOnboarding.currentItem = currentItem + 1
            } else {
                finishOnboarding()
            }
        }

        binding.btnGetStarted.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun setupOnboardingItems() {
        onboardingItems = listOf(
            OnboardingItem(
                title = "Welcome to Salon Oscardo",
                description = "Nikmati layanan kecantikan dan perawatan rambut pria & wanita berkelas premium dengan kenyamanan maksimal.",
                imageResId = R.drawable.ic_salon_scissors_comb,
                tagText = "HALAMAN 1 • SALON OSCARDO"
            ),
            OnboardingItem(
                title = "Our Professional Services",
                description = "Pilihan lengkap Potong Rambut Gentleman, Hair Coloring & Highlight terkini, serta Creambath Premium dari kapster berpengalaman.",
                imageResId = R.drawable.ic_scissors,
                tagText = "HALAMAN 2 • SALON SERVICES"
            ),
            OnboardingItem(
                title = "Easy Booking Experience",
                description = "Kemudahan reservasi jadwal perawatan salon dan akses katalog layanan favorit Anda kapan saja dan di mana saja.",
                imageResId = R.drawable.ic_interactive,
                tagText = "HALAMAN 3 • EASY BOOKING"
            )
        )
    }

    private fun setupViewPager() {
        val adapter = OnboardingPagerAdapter(onboardingItems)
        binding.viewPagerOnboarding.adapter = adapter
        binding.viewPagerOnboarding.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                updateButtons(position)
            }
        })
    }

    private fun setupIndicators() {
        binding.layoutIndicators.removeAllViews()
        val indicators = arrayOfNulls<ImageView>(onboardingItems.size)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 0, 8, 0)
        }

        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.bg_onboarding_indicator_inactive
                )
            )
            indicators[i]?.layoutParams = layoutParams
            binding.layoutIndicators.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.layoutIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = binding.layoutIndicators.getChildAt(i) as ImageView
            val params = imageView.layoutParams as LinearLayout.LayoutParams
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.bg_onboarding_indicator_active
                    )
                )
                params.width = 36
                params.height = 12
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.bg_onboarding_indicator_inactive
                    )
                )
                params.width = 12
                params.height = 12
            }
            imageView.layoutParams = params
        }
    }

    private fun updateButtons(position: Int) {
        if (position == onboardingItems.size - 1) {
            binding.btnNext.visibility = View.GONE
            binding.btnSkip.visibility = View.GONE
            binding.btnGetStarted.visibility = View.VISIBLE
        } else {
            binding.btnNext.visibility = View.VISIBLE
            binding.btnSkip.visibility = View.VISIBLE
            binding.btnGetStarted.visibility = View.GONE
        }
    }

    private fun finishOnboarding() {
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        sharedPref.edit().apply {
            putBoolean("is_onboarding_completed", true)
            apply()
        }

        val isLogin = sharedPref.getBoolean("isLogin", false)
        val nextIntent = if (isLogin) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, AuthActivity::class.java)
        }

        startActivity(nextIntent)
        finish()
    }
}
