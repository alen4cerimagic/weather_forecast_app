package com.codetest.main

import com.codetest.R
import com.codetest.main.api.LocationApiService
import com.codetest.main.model.Location
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.util.*

class LocationHelper {

    companion object {

        fun getLocations(success: (Collection<Location>) -> Unit, error: (Int) -> Unit) {
            val locations: ArrayList<Location> = arrayListOf()
            LocationApiService.getApi().get("locations", {
                val list = it.get("locations").asJsonArray
                for (json in list) {
                    locations.add(Location.from(json.asJsonObject))
                }
                success(locations)
            }, {
                error(getErrorMessage(it))
            })
        }

        fun addLocation(location: Location, callback: (Location) -> Unit, error: (Int) -> Unit) {
            LocationApiService.getApi().post("locations", Location.to(location), {
                callback(Location.from(it))
            }, {
                error(getErrorMessage(it))
            })
        }

        fun deleteLocation(
            locationId: String,
            callback: (ResponseBody?) -> Unit,
            error: (Int) -> Unit
        ) {
            LocationApiService.getApi().delete("locations/${locationId}", {
                callback(it)
            }, {
                error(getErrorMessage(it))
            })
        }

        fun dispose() = LocationApiService.getApi().dispose()

        private fun getErrorMessage(throwable: Throwable?): Int = if (throwable is HttpException) {
            when (throwable.code()) {
                400 -> R.string.error_field_missing_message
                500 -> R.string.server_error_message
                else -> R.string.error_message
            }
        } else R.string.error_message
    }
}