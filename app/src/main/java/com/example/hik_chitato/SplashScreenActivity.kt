package com.example.hik_chitato

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.hik_chitato.data.repository.AuthRepository
import com.example.hik_chitato.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            // Delay 2 detik untuk branding Splash Screen Salon Oscardo
            delay(2000)

            // Pengecekan status onboarding & login di SharedPreferences / Supabase
            val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
            val isOnboardingCompleted = sharedPref.getBoolean("is_onboarding_completed", false)
            val isLogin = authRepository.isUserLoggedIn() || sharedPref.getBoolean("isLogin", false)

            val intent = when {
                !isOnboardingCompleted -> Intent(this@SplashScreenActivity, OnboardingActivity::class.java)
                isLogin -> Intent(this@SplashScreenActivity, MainActivity::class.java)
                else -> Intent(this@SplashScreenActivity, AuthActivity::class.java)
            }

            startActivity(intent)
            finish()
        }
    }
}
