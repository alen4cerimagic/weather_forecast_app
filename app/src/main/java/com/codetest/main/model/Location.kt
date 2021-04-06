package com.codetest.main.model

import com.codetest.R
import com.google.gson.JsonObject

enum class Status(val value: Int) {
    CLOUDY(R.drawable.ic_cloud),
    SUNNY(R.drawable.ic_sun),
    MOSTLY_SUNNY(R.drawable.ic_cloud_sun),
    PARTLY_SUNNY(R.drawable.ic_sun),
    PARTLY_SUNNY_RAIN(R.drawable.ic_drizzle_alt_sun),
    THUNDER_CLOUD_AND_RAIN(R.drawable.ic_lightning_rain),
    TORNADO(R.drawable.ic_wind),
    BARELY_SUNNY(R.drawable.ic_wind_sun),
    LIGHTENING(R.drawable.ic_lightning),
    SNOW_CLOUD(R.drawable.ic_snow_alt),
    RAINY(R.drawable.ic_rain_alt);

    companion object {
        fun from(string: String): Status = values().first { it.name == string }
    }
}

class Location(
    val id: String?,
    val name: String?,
    val temperature: Int?,
    val status: Status
) {

    companion object {
        fun from(jsonObject: JsonObject): Location {
            return Location(
                jsonObject.get("id").asString,
                jsonObject.get("name").asString,
                jsonObject.get("temperature").asInt,
                Status.from(jsonObject.get("status").asString)
            )
        }

        fun to(location: Location): JsonObject {
            val jsonObject = JsonObject()
            jsonObject.addProperty("id", location.id)
            jsonObject.addProperty("name", location.name)
            jsonObject.addProperty("temperature", location.temperature)
            jsonObject.addProperty("status", location.status.name)
            return jsonObject
        }
    }
}