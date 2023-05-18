package com.example.foodfinder.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfinder.activities.MainActivity
import com.example.foodfinder.activities.MealActivity
import com.example.foodfinder.adapters.MealsAdapter
import com.example.foodfinder.databinding.FragmentSearchBinding
import com.example.foodfinder.util.VerticalItemDecoration
import com.example.foodfinder.viewModels.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchedMealsAdapter: MealsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRv()
        onClick()
        observeSearchedMealsLiveData()

        var searchJob: Job? = null
        binding.edSearchBox.addTextChangedListener {
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(500)
                viewModel.searchMeals(it.toString())
            }
        }
    }

    private fun observeSearchedMealsLiveData() {
        viewModel.observeSearchedMeals().observe(viewLifecycleOwner, Observer {
            mealsList ->

            searchedMealsAdapter.differ.submitList(mealsList)
            onSearchedItemClick()
        })
    }


    // click function
    private fun searchMeals() {
        val searchQuery = binding.edSearchBox.text.toString()
        if (searchQuery.isNotEmpty()){
            viewModel.searchMeals(searchQuery)
        }
    }
    private fun onSearchedItemClick() {

        viewModel.observePopularItemsLivedata()
        searchedMealsAdapter.onItemClick = { meal ->
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(HomeFragment.MEAL_ID, meal.idMeal)
            intent.putExtra(HomeFragment.MEAL_NAME, meal.strMeal)
            intent.putExtra(HomeFragment.MEAL_THUMB, meal.strMealThumb)
            startActivity(intent)
        }
    }

    // rv functions
    private fun setupRv() {
        searchedMealsAdapter = MealsAdapter()
        binding.rvSearchedMeals.apply {
            adapter = searchedMealsAdapter
            layoutManager = GridLayoutManager(context, 2,GridLayoutManager.VERTICAL,false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
    private fun onClick() {
        binding.searchGoBtn.setOnClickListener {
            searchMeals()
        }
    }
}