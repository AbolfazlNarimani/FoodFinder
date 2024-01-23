package com.example.foodfinder.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodfinder.R
import com.example.foodfinder.activities.CategoryMealsActivity
import com.example.foodfinder.activities.MainActivity
import com.example.foodfinder.activities.MealActivity
import com.example.foodfinder.adapters.CategoriesAdapter
import com.example.foodfinder.adapters.MostPopularAdapter
import com.example.foodfinder.databinding.FragmentHomeBinding
import com.example.foodfinder.fragments.bottomsheet.MealBottomSheetFragment
import com.example.foodfinder.pojo.Meal
import com.example.foodfinder.util.Resource
import com.example.foodfinder.viewModels.HomeViewModel
import com.example.foodfinder.viewModels.MealViewModel


class HomeFragment : Fragment() {


    private lateinit var binding: FragmentHomeBinding

    // private lateinit var homeMvvm: HomeViewModel
    private lateinit var viewModel : HomeViewModel
    private lateinit var randomMeal: Meal
    private lateinit var popularItemsAdapter: MostPopularAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter
    private val mealViewModel by viewModels<MealViewModel>()
    private var isLoadingFinished = 3

    // companion object for extra intent keys
    companion object {
        const val MEAL_ID = "com.example.foodfinder.fragments.idMeal"
        const val MEAL_NAME = "com.example.foodfinder.fragments.mealName"
        const val MEAL_THUMB = "com.example.foodfinder.fragments.mealThumb"
        const val MEAL_AREA = "com.example.foodfinder.fragments.mealArea"
        const val MEAL_CATEGORY = "com.example.foodfinder.fragments.MealCategory"
        const val MEAL_INSTRUCTION = "com.example.foodfinder.fragments.MealInstruction"
        const val YOUTUBE_LINK = "com.example.foodfinder.fragments.youtubeLink"
        const val CATEGORY_NAME = "com.example.foodfinder.fragments.categoryName"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        popularItemsAdapter = MostPopularAdapter()
        categoriesAdapter = CategoriesAdapter()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        checkLoadingState()

        setupPopularItemsRv()

        viewModel.getRandomMeal()
        observeRandomMeal()
        onRandomMealClick()

        viewModel.getPopularItems()
        observerPopularItemsLiveData()
        onPopularItemClick()

        setupCategoriesRv()
        viewModel.getCategories()
        observeCategoriesLiveData()
        onCategoriesItemClick()

        onPopularItemLongClick()
        onSearchIconClick()

    }



    private fun checkLoadingState() {

        if (isLoadingFinished == 0){
            showContent()
        }
    }



    private fun showContent() {
        binding.homeProgress.visibility = View.GONE
        binding.homeScrollView.visibility = View.VISIBLE
    }


    // observer functions
    private fun observeRandomMeal() {

        viewModel.observeRandomMealLivedata().observe(
            viewLifecycleOwner
        ) { value ->
            when (value){

                is Resource.Success -> {
                    Glide.with(this@HomeFragment).load(value.data?.strMealThumb).into(binding.imgRandomMeal)
                    value.data?.let {  this.randomMeal = value.data }
                   -- isLoadingFinished
                    checkLoadingState()
                }

                is Resource.Error -> {

                }
                else -> Unit
            }
        }
    }

    private fun observerPopularItemsLiveData() {

        viewModel.observePopularItemsLivedata().observe(viewLifecycleOwner, Observer { mealList ->
            when (mealList){
                is Resource.Success -> {
                    popularItemsAdapter.differ.submitList(mealList.data)
                   -- isLoadingFinished
                    checkLoadingState()
                }

                is Resource.Error -> {
                    Toast.makeText(context, "No response received please check your internet connection", Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }


        })
    }

    private fun observeCategoriesLiveData() {

        viewModel.observeCategoriesLiveData().observe(viewLifecycleOwner, Observer { categories ->

            when (categories){
                is Resource.Success -> {
                    categoriesAdapter.differ.submitList(categories.data)
                    -- isLoadingFinished
                    checkLoadingState()
                }

                is Resource.Error -> {
                    Toast.makeText(context, "No response received please check your internet connection", Toast.LENGTH_LONG).show()
                }

                else -> Unit
            }


        })
    }

    // setting up rv
    private fun setupPopularItemsRv() {
        binding.recViewMealsPopular.apply {
            adapter = popularItemsAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupCategoriesRv() {
        binding.recViewCategories.apply {
            adapter = categoriesAdapter
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        }
    }

    //click Listeners
    private fun onPopularItemClick() {

        viewModel.observePopularItemsLivedata()
        popularItemsAdapter.onItemClick = { meal ->
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(MEAL_ID, meal.idMeal)
            intent.putExtra(MEAL_NAME, meal.strMeal)
            intent.putExtra(MEAL_THUMB, meal.strMealThumb)
            startActivity(intent)
        }
    }

    private fun onRandomMealClick() {
        binding.randomMealCard.setOnClickListener {
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(MEAL_ID, randomMeal.idMeal)
            intent.putExtra(MEAL_NAME, randomMeal.strMeal)
            intent.putExtra(MEAL_THUMB, randomMeal.strMealThumb)
            intent.putExtra(MEAL_AREA, randomMeal.strArea)
            intent.putExtra(MEAL_CATEGORY, randomMeal.strCategory)
            intent.putExtra(MEAL_INSTRUCTION, randomMeal.strInstructions)
            intent.putExtra(YOUTUBE_LINK, randomMeal.strYoutube)
            startActivity(intent)
        }
    }

    private fun onCategoriesItemClick() {
        categoriesAdapter.onItemClick = { category ->
            val intent = Intent(activity, CategoryMealsActivity::class.java)
            intent.putExtra(CATEGORY_NAME, category.strCategory)
            startActivity(intent)
        }
    }

    private fun onPopularItemLongClick() {
        popularItemsAdapter.onLongItemClick = {meal ->
            val mealBottomSheetFragment = MealBottomSheetFragment.newInstance(meal.idMeal)
            mealBottomSheetFragment.show(childFragmentManager, "Meal Info")
        }
    }

    private fun onSearchIconClick() {
        binding.imgSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

}