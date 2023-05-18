package com.example.foodfinder.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.foodfinder.R
import com.example.foodfinder.databinding.ActivityMainBinding
import com.example.foodfinder.db.MealDataBase
import com.example.foodfinder.viewModels.HomeViewModel
import com.example.foodfinder.viewModels.HomeViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

     val viewModel: HomeViewModel by lazy {
         val mealDatabase = MealDataBase.getInstance(this)
         val homeViewModelProviderFactory = HomeViewModelFactory(mealDatabase)
         ViewModelProvider(this, homeViewModelProviderFactory)[HomeViewModel::class.java]
     }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bottomNavigation = binding.btmNav
        val navController = Navigation.findNavController(this, R.id.host_fragment)
        NavigationUI.setupWithNavController(bottomNavigation,navController)
    }
}