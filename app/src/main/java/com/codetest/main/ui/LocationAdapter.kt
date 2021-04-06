package com.codetest.main.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.codetest.R
import com.codetest.main.model.Location
import kotlinx.android.synthetic.main.location.view.*

class LocationAdapter(private val onLocationClickListener: OnLocationClickListener) :
    RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {
    private val locations = arrayListOf<Location>()

    fun submitList(locations: Collection<Location>) {
        this.locations.addAll(locations)
        notifyDataSetChanged()
    }

    fun addLocation(location: Location) {
        locations.add(location)
        notifyItemInserted(getLastPosition())
    }

    fun deleteLocation(position: Int) {
        notifyItemRemoved(position)
        locations.removeAt(position)
    }

    fun getLastPosition(): Int = locations.size - 1

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): LocationViewHolder {
        return LocationViewHolder.create(p0, onLocationClickListener)
    }

    override fun onBindViewHolder(viewHolder: LocationViewHolder, position: Int) {
        viewHolder.setup(locations[position])
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    class LocationViewHolder(
        itemView: View,
        private val onLocationClickListener: OnLocationClickListener
    ) : RecyclerView.ViewHolder(itemView) {

        companion object {
            fun create(
                parent: ViewGroup,
                onLocationClickListener: OnLocationClickListener
            ): LocationViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.location, parent, false)
                return LocationViewHolder(view, onLocationClickListener)
            }
        }

        fun setup(location: Location) {
            itemView.cityText.text = location.name
            itemView.temperatureText.text = location.temperature.toString()
            itemView.weatherIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    itemView.context,
                    location.status.value
                )
            )
            itemView.weatherText.text = location.status.name.replace("_", " ")

            itemView.setOnLongClickListener {
                onLocationClickListener.onLocationClickListener(location, adapterPosition)
                false
            }
        }
    }

    interface OnLocationClickListener {
        fun onLocationClickListener(location: Location, position: Int)
    }
}