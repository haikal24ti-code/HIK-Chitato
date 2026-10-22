package com.example.hik_chitato

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.hik_chitato.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Default Fragment setup
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // Bottom Navigation View Handler (Images 2, 3, 4)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    replaceFragment(HomeFragment())
                    binding.toolbarMain.title = getString(R.string.title_main)
                    true
                }
                R.id.menu_web -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Website Official Salon Oscardo")
                        .setMessage("Layanan Web Official Salon Oscardo sedang dalam pemeliharaan berkala.")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                    false
                }
                R.id.menu_interactive -> {
                    replaceFragment(InteractiveFragment())
                    binding.toolbarMain.title = getString(R.string.title_booking)
                    true
                }
                R.id.menu_profile -> {
                    replaceFragment(ProfileFragment())
                    binding.toolbarMain.title = getString(R.string.title_profile)
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}