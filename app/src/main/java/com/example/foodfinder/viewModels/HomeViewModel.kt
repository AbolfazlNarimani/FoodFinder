package com.example.foodfinder.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodfinder.db.MealDataBase
import com.example.foodfinder.fragments.HomeFragment
import com.example.foodfinder.pojo.Category
import com.example.foodfinder.pojo.CategoryList
import com.example.foodfinder.pojo.MealsByCategoryList
import com.example.foodfinder.pojo.MealsByCategory
import com.example.foodfinder.pojo.Meal
import com.example.foodfinder.pojo.MealList
import com.example.foodfinder.retrofit.RetrofitInstance
import com.example.foodfinder.util.GlobalState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query
import kotlin.random.Random

class HomeViewModel(
    private val mealDataBase: MealDataBase
) : ViewModel() {

    private var randomMealLiveData = MutableLiveData<Meal>()
    private var popularItemsLiveData = MutableLiveData<List<MealsByCategory>>()
    private var categoriesLiveData = MutableLiveData<List<Category>>()
    private var favoritesMealLiveData = mealDataBase.MealDao().getAllMeals()
    private var bottomSheetMealLiveData = MutableLiveData<Meal>()
    private var searchedMealsLiveData = MutableLiveData<List<Meal>>()


    var saveStateRandomMeal: Meal? = null

    // functions for getting DATA
    fun getRandomMeal() {

        saveStateRandomMeal?.let {
            randomMealLiveData.postValue(it)
            return
        }

        RetrofitInstance.api.getRandomMeal().enqueue(object : Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {

                response.body()?.let {
                    val randomMeal: Meal = response.body()!!.meals[0]

                    randomMealLiveData.postValue(randomMeal)
                    saveStateRandomMeal = randomMeal
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.d("TEST", t.message.toString())
            }
        })
    }

    fun getPopularItems() {
        RetrofitInstance.api.getPopularItems(randomCategory())
            .enqueue(object : Callback<MealsByCategoryList> {
                override fun onResponse(
                    call: Call<MealsByCategoryList>,
                    response: Response<MealsByCategoryList>
                ) {
                    response.body()?.let {
                        popularItemsLiveData.postValue(response.body()!!.meals)
                    }
                }

                override fun onFailure(call: Call<MealsByCategoryList>, t: Throwable) {
                    return
                }
            })
    }

    private fun randomCategory(): String {
        val category = listOf<String>(
            "Beef",
            "Chicken",
            "Dessert",
            "Lamb",
            "Miscellaneous",
            "Pasta",
            "Pork",
            "Seafood",
            "Side",
            "Starter",
            "Vegan",
            "Vegetarian",
            "Breakfast",
            "Goat"
        )
        val position = Random.nextInt(0, 13)
        return category[position]
    }

    fun getCategories() {
        RetrofitInstance.api.getCategories().enqueue(object : Callback<CategoryList> {
            override fun onResponse(call: Call<CategoryList>, response: Response<CategoryList>) {
                response.body()?.let { categoryList ->
                    categoriesLiveData.postValue(categoryList.categories)
                    GlobalState.setStateSuccessful()
                }
            }

            override fun onFailure(call: Call<CategoryList>, t: Throwable) {
                return
            }
        })
    }

    fun getMealById(id: String) {
        RetrofitInstance.api.getMealDetails(id).enqueue(object : Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                val meal = response.body()?.meals?.first()
                meal?.let { meal ->
                    bottomSheetMealLiveData.postValue(meal)
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.e("HomeViewModel", t.message.toString())
            }
        })
    }

    fun searchMeals(searchQuery: String) = RetrofitInstance.api.searchMeals(searchQuery).enqueue(
        object : Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                val mealList = response.body()?.meals
                mealList?.let {
                    searchedMealsLiveData.postValue(it)
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.e("HomeViewModel", t.message.toString())
            }
        }
    )


    // observer-functions
    fun observeRandomMealLivedata(): LiveData<Meal> = randomMealLiveData
    fun observePopularItemsLivedata(): LiveData<List<MealsByCategory>> = popularItemsLiveData
    fun observeCategoriesLiveData(): MutableLiveData<List<Category>> = categoriesLiveData
    fun observeFavoritesMealsLiveData(): LiveData<List<Meal>> = favoritesMealLiveData
    fun observeBottomSheetMeal(): LiveData<Meal> = bottomSheetMealLiveData
    fun observeSearchedMeals(): LiveData<List<Meal>> = searchedMealsLiveData

    // Room function
    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            mealDataBase.MealDao().delete(meal)
        }
    }

    fun insertMeal(meal: Meal) {
        viewModelScope.launch {
            mealDataBase.MealDao().upsert(meal)
        }
    }
}