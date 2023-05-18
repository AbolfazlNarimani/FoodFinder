package com.example.foodfinder.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodfinder.db.MealDataBase

class HomeViewModelFactory(
    val mealDataBase: MealDataBase
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(mealDataBase) as T
    }
}
