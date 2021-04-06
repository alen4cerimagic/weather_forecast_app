package com.codetest.main

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.appcompat.app.AppCompatActivity
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.codetest.R
import com.codetest.main.model.Location
import com.codetest.main.model.Status
import com.codetest.main.ui.LocationAdapter
import com.codetest.main.ui.WeatherAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_weather_forecast.*
import kotlinx.android.synthetic.main.activity_weather_forecast.root
import kotlinx.android.synthetic.main.input_form.*
import kotlinx.android.synthetic.main.input_form.cityInput
import kotlinx.android.synthetic.main.input_form.weatherList

class WeatherForecastActivity : AppCompatActivity(), WeatherAdapter.OnWeatherClickListener,
    LocationAdapter.OnLocationClickListener {

    private lateinit var inputFormBehavior: BottomSheetBehavior<RelativeLayout>
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var locationAdapter: LocationAdapter

    private var tempStatus: Status? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_forecast)

        initLocationList()
        initWeatherList()
        initInputForm()
        setClickListeners()

        fetchLocations()
    }

    //region API calls
    private fun fetchLocations() {
        showProgress(true)
        LocationHelper.getLocations({
            showProgress(false)
            locationAdapter.submitList(it)
            mainListView.scheduleLayoutAnimation()
            deleteInstructionText.visibility = View.VISIBLE
        }, {
            showProgress(false)
            Snackbar.make(root, resources.getString(it), Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    fetchLocations()
                }.show()
        })
    }

    private fun addLocation(location: Location) {
        showProgress(true)
        LocationHelper.addLocation(location, {
            showProgress(false)
            locationAdapter.addLocation(it)
            mainListView.smoothScrollToPosition(locationAdapter.getLastPosition())
        }, {
            showProgress(false)
            Snackbar.make(root, resources.getString(it), Snackbar.LENGTH_LONG)
                .setAction(R.string.retry) {
                    addLocation(location)
                }.show()
        })
    }

    private fun deleteLocation(id: String, position: Int) {
        showProgress(true)
        LocationHelper.deleteLocation(id, {
            showProgress(false)
            locationAdapter.deleteLocation(position)
        }, {
            showProgress(false)
            Snackbar.make(root, resources.getString(it), Snackbar.LENGTH_LONG)
                .setAction(R.string.retry) {
                    deleteLocation(id, position)
                }.show()
        })
    }
    //endregion

    //region UI methods
    private fun showProgress(show: Boolean) {
        progress.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun initLocationList() {
        locationAdapter = LocationAdapter(this)
        mainListView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.item_slide)
        mainListView.layoutAnimation = layoutAnimationController

        mainListView.adapter = locationAdapter
    }

    private fun initWeatherList() {
        weatherList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        weatherAdapter = WeatherAdapter(this)
        weatherList.adapter = weatherAdapter
        weatherAdapter.submitList(Status.values())
    }

    private fun initInputForm() {
        inputFormBehavior = BottomSheetBehavior.from(inputForm)
        inputFormBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        inputFormBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(p0: View, state: Int) {
                when (state) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        shade.visibility = View.GONE
                        cityInput.apply {
                            text?.clear()
                            clearFocus()
                        }
                        tempInput.apply {
                            text?.clear()
                            clearFocus()
                        }
                        tempStatus = null
                        weatherAdapter.resetList()
                        weatherList.scrollToPosition(0)
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        shade.visibility = View.VISIBLE
                    }
                    else -> Log.v("behavior_state", state.toString())
                }
            }

            override fun onSlide(p0: View, p1: Float) {
            }
        })
    }

    private fun setClickListeners() {
        addNewButton.setOnClickListener {
            inputFormBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        addLocationButton.setOnClickListener {
            inputFormBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            val cityName = cityInput.text?.toString()
            val temperature = tempInput.text.toString()

            if (TextUtils.isEmpty(cityName) || TextUtils.isEmpty(temperature) || tempStatus == null)
                Snackbar.make(
                    root,
                    resources.getString(R.string.error_field_missing_message),
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            else {
                val location = Location("", cityName, Integer.parseInt(temperature), tempStatus!!)
                addLocation(location)
            }
        }
    }
    //endregion

    override fun onWeatherSelect(position: Int) {
        tempStatus = weatherAdapter.selectItem(position)
    }

    override fun onLocationClickListener(location: Location, position: Int) {
        if (location.id != null) {
            deleteLocation(location.id, position)
            signalize()
        }
    }

    private fun signalize() {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}