package com.example.app_movie_booking_ticket.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.app_movie_booking_ticket.databinding.ParthomeViewholderSliderBinding
import com.example.app_movie_booking_ticket.model.SliderItems

class SliderAdapter(private var sliderItems: MutableList<SliderItems>, private val viewPager2: ViewPager2
): RecyclerView.Adapter<SliderAdapter.SliderViewholder>(){
    private var context: Context? = null
    
    // Runnable logic removed for true infinite scrolling
    inner class SliderViewholder(private val binding: ParthomeViewholderSliderBinding):
    RecyclerView.ViewHolder(binding.root){
        fun bind (sliderItems: SliderItems) {
            context?.let {
                Glide.with(it)
                    .load(sliderItems.image)
                    .apply(
                        RequestOptions().transform(CenterCrop(), RoundedCorners(60))
                    )
                    .into(binding.imageSlide)
            }
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SliderAdapter.SliderViewholder {
        context=parent.context
        val binding = ParthomeViewholderSliderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SliderViewholder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewholder, position: Int) {
        if (sliderItems.isNotEmpty()) {
            holder.bind(sliderItems[position % sliderItems.size])
        }
    }

    override fun getItemCount(): Int = Int.MAX_VALUE
}
