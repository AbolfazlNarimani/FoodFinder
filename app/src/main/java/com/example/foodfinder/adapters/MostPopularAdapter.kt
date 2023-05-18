package com.example.foodfinder.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodfinder.databinding.PopularItemsBinding
import com.example.foodfinder.pojo.MealsByCategory

class MostPopularAdapter: RecyclerView.Adapter<MostPopularAdapter.PopularMealViewHolder>() {

    class PopularMealViewHolder( val binding: PopularItemsBinding): RecyclerView.ViewHolder(binding.root)

    lateinit var onItemClick:((MealsByCategory) -> Unit)
     var onLongItemClick: ((MealsByCategory) -> Unit)?= null

    private var diffCallback = object : DiffUtil.ItemCallback<MealsByCategory>(){
        override fun areItemsTheSame(oldItem: MealsByCategory, newItem: MealsByCategory): Boolean {
            return oldItem.idMeal == newItem.idMeal
        }

        override fun areContentsTheSame(
            oldItem: MealsByCategory,
            newItem: MealsByCategory
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)


    override fun onBindViewHolder(holder: PopularMealViewHolder, position: Int) {
        val imgView =

            Glide.with(holder.itemView).load(differ.currentList[position].strMealThumb).into(holder.binding.imgPopularMealItem)

        holder.itemView.setOnClickListener {
           onItemClick.invoke(differ.currentList[position])
        }

        holder.itemView.setOnLongClickListener {
            onLongItemClick?.invoke(differ.currentList[position])
            true
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularMealViewHolder {
        return PopularMealViewHolder(
            PopularItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}