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
import com.example.foodfinder.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query
import kotlin.random.Random

class HomeViewModel(
    private val mealDataBase: MealDataBase
) : ViewModel() {

    private var randomMealLiveData = MutableLiveData<Resource<Meal>>(Resource.Unspecified())
    private var popularItemsLiveData = MutableLiveData<Resource<List<MealsByCategory>>>(Resource.Unspecified())
    private var categoriesLiveData = MutableLiveData<Resource<List<Category>>>(Resource.Unspecified())

    private var favoritesMealLiveData = mealDataBase.MealDao().getAllMeals()
    private var bottomSheetMealLiveData = MutableLiveData<Meal>()
    private var searchedMealsLiveData = MutableLiveData<List<Meal>>()


    var saveStateRandomMeal: Meal? = null

    // functions for getting DATA
    fun getRandomMeal() {

        randomMealLiveData.postValue(Resource.Loading())

        saveStateRandomMeal?.let {

            randomMealLiveData.postValue(Resource.Success(it))
            return
        }

        RetrofitInstance.api.getRandomMeal().enqueue(object : Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {

                response.body()?.let {
                    val randomMeal: Meal = response.body()!!.meals[0]

                    randomMealLiveData.postValue(Resource.Success(randomMeal))
                    saveStateRandomMeal = randomMeal
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {

                Log.d("TEST", t.message.toString())
                randomMealLiveData.postValue(Resource.Error(t.message.toString()))
            }
        })
    }

    fun getPopularItems() {

        popularItemsLiveData.postValue(Resource.Loading())

        RetrofitInstance.api.getPopularItems(randomCategory())
            .enqueue(object : Callback<MealsByCategoryList> {
                override fun onResponse(
                    call: Call<MealsByCategoryList>,
                    response: Response<MealsByCategoryList>
                ) {
                    response.body()?.let {

                        popularItemsLiveData.postValue(Resource.Success(response.body()!!.meals))

                    }
                }

                override fun onFailure(call: Call<MealsByCategoryList>, t: Throwable) {
                    Log.d("HomeViewModel", t.message.toString())
                    popularItemsLiveData.postValue(Resource.Error(t.message.toString()))
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

        categoriesLiveData.postValue(Resource.Loading())

        RetrofitInstance.api.getCategories().enqueue(object : Callback<CategoryList> {
            override fun onResponse(call: Call<CategoryList>, response: Response<CategoryList>) {

                response.body()?.let { categoryList ->

                    categoriesLiveData.postValue(Resource.Success(categoryList.categories))

                }
            }

            override fun onFailure(call: Call<CategoryList>, t: Throwable) {
                Log.d("HomeViewModel", t.message.toString())
                categoriesLiveData.postValue(Resource.Error(t.message.toString()))
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
    fun observeRandomMealLivedata(): LiveData<Resource<Meal>> = randomMealLiveData
    fun observePopularItemsLivedata(): LiveData<Resource<List<MealsByCategory>>> = popularItemsLiveData
    fun observeCategoriesLiveData(): MutableLiveData<Resource<List<Category>>> = categoriesLiveData
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