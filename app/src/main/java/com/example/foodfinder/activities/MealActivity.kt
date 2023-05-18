package com.example.foodfinder.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.foodfinder.R
import com.example.foodfinder.databinding.ActivityMealBinding
import com.example.foodfinder.databinding.FragmentHomeBinding
import com.example.foodfinder.db.MealDataBase
import com.example.foodfinder.fragments.HomeFragment
import com.example.foodfinder.pojo.Meal
import com.example.foodfinder.viewModels.HomeViewModel
import com.example.foodfinder.viewModels.MealViewModel
import com.example.foodfinder.viewModels.MealViewModelFactory

class MealActivity : AppCompatActivity() {

    private lateinit var mealId: String
    private lateinit var mealName: String
    private lateinit var mealThumb: String
    private lateinit var mealMvvm : MealViewModel
    private lateinit var youtubeLink : String

    private lateinit var binding: ActivityMealBinding





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val mealDataBase = MealDataBase.getInstance(this)
        val viewModelFactory = MealViewModelFactory(mealDataBase)
        mealMvvm = ViewModelProvider(this,viewModelFactory)[MealViewModel::class.java]

        getMealInformationFromIntent()

        setInformationInViews()

        loadingCase()

        mealMvvm.getMealDetail(mealId)

        observerMealDetailsLiveData()

        onYoutubeImageClick()
        onFavoriteClick()


    }

    private fun onFavoriteClick() {
        binding.btnAddToFav.setOnClickListener {
            mealToSave?.let {
                mealMvvm.insertMeal(it)
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun onYoutubeImageClick() {
        binding.youtube.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
            startActivity(intent)
        }
    }

    private var mealToSave: Meal? = null
    private fun observerMealDetailsLiveData() {
        mealMvvm.observeMealDetailLiveData().observe(this
        ) { t ->
            mealToSave = t
            youtubeLink = t!!.strYoutube.toString()
            binding.apply {
                tvCategory.text = "Category : ${t.strCategory}"
                tvArea.text = "Area : ${t.strArea}"
                tvInstructionsSteps.text = t.strInstructions
            }
            onResponseCase()
        }
        /*        mealMvvm.observerMealDetailLiveData().observe(this, object : Observer<Meal>{
                    override fun onChanged(t: Meal?) {
                        val meal = t
                        youtubeLink = meal!!.strYoutube
                        binding.apply {
                            tvCategoryInfo.text = "Category : ${meal!!.strCategory}"
                            tvAreaInfo.text = "Area : ${meal.strArea}"
                            tvContent.text = meal.strInstructions
                        }
                        onResponseCase()
                    }
                })*/
    }

    private fun loadingCase(){
        binding.apply {
            progressBar.visibility = View.VISIBLE
            btnAddToFav.visibility = View.INVISIBLE
            tvCategory.visibility = View.INVISIBLE
            tvArea.visibility = View.INVISIBLE
            tvInstructionsSteps.visibility = View.INVISIBLE
            tvInstructions.visibility = View.INVISIBLE
            youtube.visibility = View.INVISIBLE
        }
    }
    private fun onResponseCase(){
        binding.apply {
            progressBar.visibility = View.INVISIBLE
            btnAddToFav.visibility = View.VISIBLE
            tvCategory.visibility = View.VISIBLE
            tvArea.visibility = View.VISIBLE
            tvInstructions.visibility = View.VISIBLE
            tvInstructionsSteps.visibility = View.VISIBLE
            tvCategory.visibility = View.VISIBLE
            youtube.visibility = View.VISIBLE
        }
    }

    private fun setInformationInViews() {
        Glide.with(applicationContext).load(mealThumb).into(binding.imgMealDetails)
        binding.collapsingToolbar.title = mealName
    }

    private fun getMealInformationFromIntent() {
        val intent = intent
        mealId = intent.getStringExtra(HomeFragment.MEAL_ID)!!
        mealName = intent.getStringExtra(HomeFragment.MEAL_NAME)!!
        mealThumb = intent.getStringExtra(HomeFragment.MEAL_THUMB)!!
    }

}