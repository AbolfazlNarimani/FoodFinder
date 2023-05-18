package com.example.foodfinder.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foodfinder.R
import com.example.foodfinder.adapters.CategoryMealsAdapter
import com.example.foodfinder.databinding.ActivityCategoryMealsBinding
import com.example.foodfinder.fragments.HomeFragment
import com.example.foodfinder.viewModels.CategoryMealsViewModel

class CategoryMealsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryMealsBinding
    val categoryMealsViewModel by viewModels<CategoryMealsViewModel>()
    private lateinit var categoryMealsAdapter : CategoryMealsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryMealsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        categoryMealsViewModel.getMealsByCategory(intent.getStringExtra(HomeFragment.CATEGORY_NAME)!!)
        observeMealsLiveData()
        setupRv()
        onMealClick()
    }

    private fun setupRv() {
        categoryMealsAdapter = CategoryMealsAdapter()
        binding.rvMeals.apply {
            adapter = categoryMealsAdapter
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL,false)
        }
    }

    private fun observeMealsLiveData() {
        categoryMealsViewModel.observeMealsByCategory().observe(this, Observer {
            mealsList->
            categoryMealsAdapter.setMealsList(mealsList)
            binding.tvCategoryCount.text = mealsList.size.toString()

        })
    }
    private fun onMealClick() {
        categoryMealsAdapter.onItemClick = {meal ->
            val intent = Intent(this, MealActivity::class.java)
            intent.putExtra(HomeFragment.MEAL_ID,meal.idMeal)
            intent.putExtra(HomeFragment.MEAL_NAME,meal.strMeal)
            intent.putExtra(HomeFragment.MEAL_THUMB,meal.strMealThumb)
            startActivity(intent)
        }
    }
}