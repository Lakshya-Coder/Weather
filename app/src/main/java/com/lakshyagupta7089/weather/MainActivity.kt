package com.lakshyagupta7089.weather

import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.lakshyagupta7089.weather.adapter.WeatherAdapter
import com.lakshyagupta7089.weather.databinding.ActivityMainBinding
import com.lakshyagupta7089.weather.model.Weather
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding ?= null
    private var list: ArrayList<Weather> ?= null
    private var adapter: WeatherAdapter ?= null
    private var locationManager: LocationManager ?= null
    private var PERMISSION_CODE = 1
    private var cityName: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )

        list = ArrayList()
        adapter = WeatherAdapter(applicationContext, list!!)
        binding!!.weather.adapter = adapter

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION),
            PERMISSION_CODE)
        }

        val location: Location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
         cityName = getCityName(location.longitude, location.latitude)
         getWeatherInfo(cityName!!)

        binding!!.searchImageView.setOnClickListener {
            if (binding!!.cityTextInputEditText.text!!.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter city name",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val city = binding!!.cityTextInputEditText.text!!.toString()
                getWeatherInfo(city)

                hideKeyBoard()
            }
        }
    }

    private fun hideKeyBoard() {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_CODE) {
            if (grantResults.size <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "Please provide the permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getCityName(longitude: Double, latitude: Double): String {
        var cityName = "Not found"

        val gcd = Geocoder(baseContext, Locale.getDefault())

        try {
            val list: List<Address> ?= gcd.getFromLocation(latitude, longitude, 10)

            for (i in 0..list!!.size) {
                if (list[i] != null) {
                    val city: String = list[i].locality

                    if (city != null && city.isNotEmpty()) {
                        cityName = city
                        break
                    } else {
                        Toast.makeText(
                            this,
                            "User city is not found...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }catch (e: Exception) {}

        return cityName
    }

    private fun getWeatherInfo(cityName: String) {
        val url =
            "https://api.weatherapi.com/v1/forecast.json?key=ab4a6e1f4d0e48baa8c111143212507&q=${cityName}&days=1&aqi=yes&alerts=yes"
        binding!!.cityName.text = cityName

        val requestQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            {
                binding!!.progressBar.visibility = View.INVISIBLE
                binding!!.home.visibility = View.VISIBLE
                list!!.clear()

                val temperature = it.getJSONObject("current").getDouble("temp_c")

                binding!!.temperatureTextView.text = temperature.toString() + "°c"

                val isDay = it.getJSONObject("current").getInt("is_day")
                val condition  =
                    it.getJSONObject(
                        "current"
                    ).getJSONObject("condition").getString("text")
                val conditionIcon  = it.getJSONObject("current").getJSONObject("condition").getString("icon")

                Glide
                    .with(this)
                    .load( "https:$conditionIcon")
                    .into(binding!!.icon)
                binding!!.conditionTextView.text = condition

                if (isDay == 1) {
//                    Glide
//                        .with(this)
//                        .load( "https://images.unsplash.com/photo-1610892415063-d89a504ce049?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=334&q=80")
//                        .into(binding!!.backgroundImageView)

                    binding!!.backgroundImageView.setImageResource(R.drawable.light)
                } else {
//                    Glide
//                        .with(this)
//                        .load("https://images.unsplash.com/photo-1507400492013-162706c8c05e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1509&q=80")
//                        .into(binding!!.backgroundImageView)
                    binding!!.backgroundImageView.setImageResource(R.drawable.night)
                }

                val frocast = it.getJSONObject("forecast")
                val forecastday = frocast.getJSONArray("forecastday").getJSONObject(0)
                val hour = forecastday.getJSONArray("hour")
//
                for (i in 0 until hour.length()) {
                    val jsonObject = hour.getJSONObject(i)

                    val time = jsonObject.getString("time")
                    val temperature = jsonObject.getDouble("temp_c")
                    // val text = jsonObject.getJSONObject("condition").getString("text")
                    val wind = jsonObject.getDouble("wind_kph")
                    val icon = jsonObject.getJSONObject("condition").getString("icon")

                    list!!.add(
                        Weather(
                            time,
                            temperature.toString(),
                            icon,
                            wind.toString()
                        )
                    )
                }
//
                adapter!!.notifyDataSetChanged()
            },
            {
                Toast.makeText(
                    this,
                    "Please enter a valid city name...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        requestQueue.add(jsonObjectRequest)
    }
}