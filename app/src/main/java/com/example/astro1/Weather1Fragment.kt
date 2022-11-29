package com.example.astro1

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_weather1.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Weather1Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Weather1Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var url : String = "http://api.openweathermap.org/data/2.5/weather"
    private var appId : String = "e1296b8aa9e146f9d1a02b85dd5d57bf"

    private var df : DecimalFormat = DecimalFormat("#.##")

    private var isCreatedView : Boolean = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var myApplication: MyApplication

        isCreatedView = true

        getWeatherDetails()
    }

    fun update(){
        //do your staff
    }

    fun getWeatherDetails(){
        if(!isCreatedView || tvResult==null) return
        var response : String = (this.activity?.application as MyApplication).weather
        if(isCreatedView && !response.equals("")){
            var output: String = "";
            var jsonResponse: JSONObject = JSONObject(response!!)
            var jsonObjectCoord: JSONObject = jsonResponse.getJSONObject("coord")
            var lon : String = jsonObjectCoord.getString("lon")
            var lat : String = jsonObjectCoord.getString("lat")
            var jsonArray: JSONArray = jsonResponse.getJSONArray("weather")
            var jsonObjectWeather : JSONObject = jsonArray.getJSONObject(0)
            var description : String = jsonObjectWeather.getString("description")
            var icon : String = jsonObjectWeather.getString("icon")
            var iconview : String = "http://openweathermap.org/img/w/" + icon + ".png";
            var jsonObjectMain : JSONObject = jsonResponse.getJSONObject("main")
            var temp : Double = jsonObjectMain.getDouble("temp")
            if((this.activity?.application as MyApplication).temp_unit.equals("Celsius")) temp -= 273.15
            var feelsLike : Double = jsonObjectMain.getDouble("feels_like")
            if((this.activity?.application as MyApplication).temp_unit.equals("Celsius")) feelsLike -= 273.15
            var pressure = jsonObjectMain.getInt("pressure")
            var humidity : Int = jsonObjectMain.getInt("humidity")
            var jsonObjectWind : JSONObject = jsonResponse.getJSONObject("wind")
            var wind : Double = jsonObjectWind.getString("speed").toDouble()
            if((this.activity?.application as MyApplication).dist_unit.equals("Mile")) wind = getMile(wind)
            var windDeg : Double = jsonObjectWind.getDouble("deg")
            var jsonObjectClouds : JSONObject = jsonResponse.getJSONObject("clouds")
            var clouds : String = jsonObjectClouds.getString("all")
            var jsonObjectSys : JSONObject = jsonResponse.getJSONObject("sys")
            var countryName : String = jsonObjectSys.getString("country")
            var cityName : String = jsonResponse.getString("name")
            var visibility : String = jsonResponse.getString("visibility")

            tvResult.setTextColor(Color.rgb(68, 134, 199))
            w4Text.setTextColor(Color.rgb(68, 134, 199))

            var temp_unit :String
            if((this.activity?.application as MyApplication).temp_unit.equals("Celsius")) temp_unit = "C"
            else temp_unit = "K"

            output += "Temp: " + df.format(temp) + "°" + temp_unit +
                    "\nFeels like: " + df.format(feelsLike) + "°" + temp_unit +
                    "\nPressure: " + pressure + " hPa"+
                    "\nDescription: " + description

            var speed_unit :String
            if((this.activity?.application as MyApplication).dist_unit.equals("Mile")) speed_unit = "mi/h"
            else speed_unit = "km/h"

            w1Text.setText(cityName + ", " + countryName)
            w2Text.setText("(" + lat + ", " + lon + ")"+"\nLast refresh: " + (this.activity?.application as MyApplication).lastRefresh)
            if(w3Text != null) w3Text.setText("Additional Informations:")
            w4Text.setText("Wind speed: " + df.format(wind) + " " + speed_unit + "\nWind direction: " + windDeg + "\nHumidity: " + humidity + "\nVisibility: " + visibility)

            Picasso.get().load(iconview).into(weatherImg);
            tvResult.setText(output)
        }
    }

    fun getMile(kilometers : Double) : Double{
        return kilometers * 0.621371192
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //getWeatherDetails()
        return inflater.inflate(R.layout.fragment_weather1, container, false)
    }

    fun toast(text: String){
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(this.activity?.applicationContext, text, duration)
        toast.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Weather1Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                Weather1Fragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}