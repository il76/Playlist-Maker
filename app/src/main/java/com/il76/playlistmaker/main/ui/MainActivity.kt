package com.il76.playlistmaker.main.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.il76.playlistmaker.R
import com.il76.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding wasn't initiliazed!")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.BLACK))
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainActivity) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            // Android 15 fix
            WindowInsetsCompat.CONSUMED
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            if (nd.label == "fragment_player" || nd.label == "fragment_playlistadd" || nd.label == "fragment_playlist") { //прячем нижнее меню на некоторых экранах
                binding.bottomNavigationView.isVisible = false
                binding.bottomNavigationViewBorder.isVisible = false
            } else {
                binding.bottomNavigationView.isVisible = true
                binding.bottomNavigationViewBorder.isVisible = true
            }
        }
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}