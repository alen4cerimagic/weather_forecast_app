# Weather Forecast (test)

Simple weather reporting app. Users can track, add, and delete weather reports. 


![see reports](https://user-images.githubusercontent.com/82004242/114083693-9b788e00-98af-11eb-93d2-35b7b3d05a43.png)
![add report](https://user-images.githubusercontent.com/82004242/114083690-9a476100-98af-11eb-8109-433c3f3aff2b.png)

# Technical details

### api -> LocationApiService.kt
+ Methods: GET, POST, DELETE
+ Interceptor: used to set header 
+ API service: retrofit2 builder, compositeDisposable(manual disposing), get post delete functions 

### model -> Location.kt
Data class Location. Contains id, name, temperature, and status. Status is an enum class of weather statuses where value is Int(address to the drawable resource)

### ui -> LocationAdapter.kt
Adapter for showing weather reports. Contains methods: submit list, add location, delete location, get last position. Intetrface OnLocationClickListener handle item clicks.

### ui -> WeatherAdapter.kt
Used to show different weather statuses on the report input form.

### main -> KeyUtil.kt
Class for getting an api Key value from preferences. IMPROVEMENT: Write apiKey in singleton class on app opening and read from there.

### main -> LocationHelper.kt
Contains methods for getting, adding, and deleting reports. Used to convert Json Objects into java objects, and getting error messages.

### main -> WeatherForecastActivity.kt
Handles views and app logic. When the app is started, a small progress bar is shown to indicate that API is retrieving data. If the server fails, a "retry layout" is showed allowing users to retry calls. When the server succeeds, data is shown in the recycler adapter. Clicking "Add new" opens a bottom sheet form allowing users to enter a new input. Successfully added input will automatically appear on the current report list. Users can also delete an input by holding a long press on it. 
