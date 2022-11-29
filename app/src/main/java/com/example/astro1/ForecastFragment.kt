package com.example.astro1

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_forecast.*
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
 * Use the [ForecastFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForecastFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var urlForecast : String = "http://api.openweathermap.org/data/2.5/forecast"
    private var cnt : Int = 40
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forecast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var myApplication: MyApplication

        isCreatedView = true

        getForecastDetails()
    }

    fun getForecastDetails(){
        if(!isCreatedView|| grids==null) return
        var response : String = (this.activity?.application as MyApplication).forecast
        if(isCreatedView && !response.equals("")){
            var jsonResponse: JSONObject = JSONObject(response!!)
            var jsonArray: JSONArray = jsonResponse.getJSONArray("list")
            var i : Int = 0;
            for (i in 0..jsonArray.length()-1){
                var output : String = ""
                var jsonObject : JSONObject = jsonArray.getJSONObject(i)

                var jsonArrayWeather: JSONArray = jsonObject.getJSONArray("weather")
                var jsonObjectWeather : JSONObject = jsonArrayWeather.getJSONObject(0)
                var description : String = jsonObjectWeather.getString("description")
                var icon : String = jsonObjectWeather.getString("icon")
                var iconview : String = "http://openweathermap.org/img/w/" + icon + ".png";

                var jsonObjectMain : JSONObject = jsonObject.getJSONObject("main")
                var temp : Double = jsonObjectMain.getDouble("temp")
                if((this.activity?.application as MyApplication).temp_unit.equals("Celsius")) temp -= 273.15
                var feelsLike : Double = jsonObjectMain.getDouble("feels_like")
                if((this.activity?.application as MyApplication).temp_unit.equals("Celsius")) feelsLike -= 273.15
                var pressure = jsonObjectMain.getInt("pressure")

                var dt_txt : String = jsonObject.getString("dt_txt")

                var temp_unit :String
                if((this.activity?.application as MyApplication).temp_unit.equals("Celsius")) temp_unit = "C"
                else temp_unit = "K"

                output += "Temp: " + df.format(temp) + "°" + temp_unit +
                        "\nFeels like: " + df.format(feelsLike) + "°" + temp_unit +
                        "\nPressure: " + pressure + " hPa"+
                        "\nDescription: " + description

                var dayText = (grids.getChildAt(i) as LinearLayout).getChildAt(0) as TextView
                var myText = (((grids.getChildAt(i) as LinearLayout).getChildAt(1) as GridLayout).getChildAt(1) as LinearLayout).getChildAt(0) as TextView
                var myImg = (((grids.getChildAt(i) as LinearLayout).getChildAt(1) as GridLayout).getChildAt(0) as LinearLayout).getChildAt(0) as ImageView
                myText.setTextColor(Color.rgb(68, 134, 199))
                myText.setText(output)
                dayText.setText(dt_txt+":")

                Picasso.get().load(iconview).into(myImg);
                //tvResult.setText(df.format(temp))
            }
        }
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
         * @return A new instance of fragment ForecastFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ForecastFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}