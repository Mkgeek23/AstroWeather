package com.example.astro1

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_options.*
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OptionsActivity : AppCompatActivity() {

    private var cityList : ArrayList<City> = ArrayList()
    var gson : Gson = Gson()

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        val spinner: Spinner = findViewById(R.id.temp_spinner)
        ArrayAdapter.createFromResource(
                this,
                R.array.temp_units,
                R.xml.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.xml.spinner_item)
            spinner.adapter = adapter
        }

        val spinner2: Spinner = findViewById(R.id.distance_spinner)
        ArrayAdapter.createFromResource(
                this,
                R.array.dist_units,
                R.xml.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.xml.spinner_item)
            spinner2.adapter = adapter
        }

        loadData()

        btnSaveOptions.setOnClickListener {
            var refreshTime: Int = editRefreshTime.text.toString().toInt()
            var cityName: String = editCity.text.toString()
            var countryName: String = editCountry.text.toString()
            var temp_unit: String = spinner.selectedItem.toString()
            var dist_unit: String = spinner2.selectedItem.toString()

            if(refreshTime==0) ++refreshTime

            (this.application as MyApplication).refreshTime = refreshTime

            (this.application as MyApplication).temp_unit = temp_unit
            (this.application as MyApplication).dist_unit = dist_unit

            if(isInternetAvailable(applicationContext))
                loadLocData(cityName, countryName)
            else
                loadDataOffline(cityName)
        }

    }

    fun loadLocData(city: String, country: String){
        var tempUrl : String = "";
        var url : String = "http://api.openweathermap.org/data/2.5/"
        var appId : String = "e1296b8aa9e146f9d1a02b85dd5d57bf"
        if(city.equals("")){
            //Show error message;
        }else{
            if(!country.equals("")){
                tempUrl = url + "weather?q=" + city +"," + country + "&appid="+appId;
            }else{
                tempUrl = url + "weather?q=" + city + "&appid="+appId;
            }
            val stringRequest = StringRequest(
                    Request.Method.POST,
                    tempUrl,
                    Response.Listener { response ->
                        var jsonResponse: JSONObject = JSONObject(response!!)
                        var jsonObjectCoord: JSONObject = jsonResponse.getJSONObject("coord")
                        var lon : String = jsonObjectCoord.getString("lon")
                        var lat : String = jsonObjectCoord.getString("lat")

                        (this.application as MyApplication).longitude = lon.toDouble()
                        (this.application as MyApplication).latitude = lat.toDouble()

                        (this.application as MyApplication).cityName = city
                        (this.application as MyApplication).countryCode = country

                        loadDataFromSP()

                        var saved = false
                        for (_city in cityList){
                            if(_city.cityName.equals(city)){
                                saved = true
                            }
                        }
                        if(!saved){
                            cityList.add(City((this.application as MyApplication).cityName,
                                (this.application as MyApplication).countryCode,
                                (this.application as MyApplication).weather,
                                (this.application as MyApplication).forecast,
                                (this.application as MyApplication).latitude,
                                (this.application as MyApplication).longitude,
                                (this.application as MyApplication).lastRefresh)
                            )
                            toast("Dodano miasto")
                            saveDataInSP("citylist", gson.toJson(cityList))
                        }

                        saveDataInSP("cityname", city)
                        saveDataInSP("temp_unit", (this.application as MyApplication).temp_unit)
                        saveDataInSP("dist_unit", (this.application as MyApplication).dist_unit)
                        saveDataInSP("longitude", (this.application as MyApplication).longitude.toString())
                        saveDataInSP("latitude", (this.application as MyApplication).latitude.toString())


                        if(!(this.application as MyApplication).lastRefresh.equals("")) {
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                            val dateTime = LocalDateTime.parse((this.application as MyApplication).lastRefresh, formatter)
                            (this.application as MyApplication).lastRefresh = dateTime.minusSeconds((this.application as MyApplication).refreshTime.toLong()).format(formatter)
                        }

                        toast("Zapisano pomyślnie")
                        loadData()
                    },
                    Response.ErrorListener { error ->
                        toast("Nie udało się zapisać")
                    })
            val requestQueue: RequestQueue = Volley.newRequestQueue(applicationContext)
            requestQueue.add(stringRequest);

        }
    }

    private fun loadDataOffline(city : String){
        loadDataFromSP()

        var saved = false
        for (_city in cityList){
            if(_city.cityName.equals(city)){
                saved = true

                (this.application as MyApplication).longitude = _city.longitude
                (this.application as MyApplication).latitude =  _city.latitude

                (this.application as MyApplication).cityName = city
                (this.application as MyApplication).countryCode = _city.countryCode
                (this.application as MyApplication).lastRefresh = _city.lastRefresh

                saveDataInSP("cityname", city)
                saveDataInSP("temp_unit", (this.application as MyApplication).temp_unit)
                saveDataInSP("dist_unit", (this.application as MyApplication).dist_unit)
                saveDataInSP("longitude", (this.application as MyApplication).longitude.toString())
                saveDataInSP("latitude", (this.application as MyApplication).latitude.toString())

            }
        }

        if(!saved){
            toast("Brak internetu, brak zapisanej miejscowosci")
        }else{
            (this.application as MyApplication).shouldRefresh = true
            toast("Brak internetu, pobrano miejscowosc z listy")
        }

        loadData()

    }

    private fun saveDataInSP(name : String, data : String){
        var sp : SharedPreferences = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE)

        var editor : SharedPreferences.Editor = sp.edit()

        //Zapis
        try {
            editor.putString(name, data)
            editor.commit()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadDataFromSP(){
        var sp : SharedPreferences = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE)
        try {
            var s : String? = sp.getString("citylist", "")
            if(!s.equals("")) {
                val myType = object : TypeToken<ArrayList<City>>() {}.type
                cityList = gson.fromJson<ArrayList<City>>(s, myType)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    fun loadData(){
        var s: String = (this.application as MyApplication).cityName.toString()
        editCity.setText(s)

        s = (this.application as MyApplication).countryCode.toString()
        editCountry.setText(s)

        s = (this.application as MyApplication).refreshTime.toString()
        editRefreshTime.setText(s)

        s = (this.application as MyApplication).temp_unit
        if(s.equals("Celsius"))
        temp_spinner.setSelection(1)

        s = (this.application as MyApplication).dist_unit
        if(s.equals("Mile"))
        distance_spinner.setSelection(1)
    }

    fun toast(text: String){
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
}