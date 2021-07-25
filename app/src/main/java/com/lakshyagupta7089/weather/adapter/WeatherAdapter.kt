package com.lakshyagupta7089.weather.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lakshyagupta7089.weather.R
import com.lakshyagupta7089.weather.model.Weather
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeatherAdapter
    (
        private val context: Context,
        private val list: ArrayList<Weather>
    ): RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_weather,
            parent,
            false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: Weather = list[position]

        Glide
            .with(context)
            .load("https:" + model.icon)
            .into(holder.condition)

        holder.temperature.text = model.temperature + "°c"
        holder.windSpeed.text = model.windSpeed + "Km/h"

        val input =  SimpleDateFormat("yyyy-MM-dd hh:mm")
        val output =  SimpleDateFormat("hh:mm aa")

        try {
            val i: Date = input.parse(model.time)
            holder.time.text = output.format(i)
        } catch (e: Exception) {}

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.time_text_view)
        val temperature: TextView = itemView.findViewById(R.id.temperature)
        val windSpeed: TextView = itemView.findViewById(R.id.wind_speed)
        val condition: ImageView = itemView.findViewById(R.id.condition)
    }
}