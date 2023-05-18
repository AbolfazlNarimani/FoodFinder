package com.example.foodfinder.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodfinder.db.MealDataBase
import com.example.foodfinder.pojo.Meal
import com.example.foodfinder.pojo.MealList
import com.example.foodfinder.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MealViewModel(
    private val mealDataBase: MealDataBase
): ViewModel() {

    private val mealDetailLiveData = MutableLiveData<Meal>()

    // data functions
    fun getMealDetail(id: String){
        RetrofitInstance.api.getMealDetails(id).enqueue(object : Callback<MealList>{
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                response.body()?.let {
                    mealDetailLiveData.postValue(response.body()!!.meals[0])
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.d("MealViewModel", t.message.toString())
            }
        })
    }
    fun insertMeal(meal: Meal){
        viewModelScope.launch {
            mealDataBase.MealDao().upsert(meal)
        }
    }

    fun deleteMeal(meal: Meal){
        viewModelScope.launch {
            mealDataBase.MealDao().delete(meal)
        }
    }

    // observer functions
    fun observeMealDetailLiveData(): LiveData<Meal> = mealDetailLiveData

}