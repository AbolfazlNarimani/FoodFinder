package com.example.foodfinder.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodfinder.databinding.PopularItemsBinding
import com.example.foodfinder.pojo.MealsByCategory

class MostPopularAdapter: RecyclerView.Adapter<MostPopularAdapter.PopularMealViewHolder>() {

    class PopularMealViewHolder( val binding: PopularItemsBinding): RecyclerView.ViewHolder(binding.root)

    lateinit var onItemClick:((MealsByCategory) -> Unit)
     var onLongItemClick: ((MealsByCategory) -> Unit)?= null

    private var mealsList = ArrayList<MealsByCategory>()

    fun setMeals(mealsList: ArrayList<MealsByCategory>){
        this.mealsList = mealsList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: PopularMealViewHolder, position: Int) {
        val imgView =

            Glide.with(holder.itemView).load(mealsList[position].strMealThumb).into(holder.binding.imgPopularMealItem)

        holder.itemView.setOnClickListener {
           onItemClick.invoke(mealsList[position])
        }

        holder.itemView.setOnLongClickListener {
            onLongItemClick?.invoke(mealsList[position])
            true
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularMealViewHolder {
        return PopularMealViewHolder(
            PopularItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }


    override fun getItemCount(): Int {
        return mealsList.size
    }


}