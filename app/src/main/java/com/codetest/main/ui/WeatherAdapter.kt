package com.codetest.main.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.codetest.R
import com.codetest.main.model.Status
import kotlinx.android.synthetic.main.weather.view.*

class WeatherAdapter(private val onWeatherClickListener: OnWeatherClickListener) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {
    private val list = arrayListOf<Weather>()

    fun submitList(statusList: Array<Status>) {
        statusList.forEach {
            list.add(Weather(it, false))
        }
        notifyDataSetChanged()
    }

    fun resetList() {
        this.list.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }

    fun selectItem(position: Int): Status {
        for ((index, value) in list.withIndex()) {
            value.isSelected = index == position
        }
        notifyDataSetChanged()
        return list[position].status
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder.create(parent, onWeatherClickListener)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.setup(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class WeatherViewHolder(
        view: View,
        private val onWeatherClickListener: OnWeatherClickListener
    ) :
        RecyclerView.ViewHolder(view) {

        fun setup(data: Weather) {

            val statusName = data.status.name.replace("_", " ")
            itemView.text.text = statusName
            itemView.icon.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    data.status.value
                )
            )

            //distinguish selected
            itemView.holder.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    if (data.isSelected) R.color.orange else R.color.lightGray
                )
            )
            itemView.text.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    if (data.isSelected) R.color.white else R.color.grey
                )
            )
            ImageViewCompat.setImageTintList(
                itemView.icon,
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        itemView.context,
                        if (data.isSelected) R.color.white else R.color.grey
                    )
                )
            )


            itemView.setOnClickListener {
                onWeatherClickListener.onWeatherSelect(adapterPosition)
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                onWeatherClickListener: OnWeatherClickListener
            ): WeatherViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.weather, parent, false)
                return WeatherViewHolder(view, onWeatherClickListener)
            }
        }
    }

    interface OnWeatherClickListener {
        fun onWeatherSelect(position: Int)
    }

    data class Weather(val status: Status, var isSelected: Boolean)
}
