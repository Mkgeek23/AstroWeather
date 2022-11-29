package com.example.astro1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_city_list.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class CityList : AppCompatActivity() {
    private var cityList : ArrayList<City> = ArrayList()
    var gson : Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_list)

        var arrayList : ArrayList<String> = ArrayList()
        var arrayAdapter : ArrayAdapter<String> = ArrayAdapter(applicationContext,  android.R.layout.simple_list_item_1, arrayList)

        var lv : ListView = list_of_cities
        lv.adapter  = arrayAdapter

        loadDataFromSP()

        for(_city in cityList){
            arrayList.add(_city.cityName)
        }

        lv.setOnItemClickListener { parent, view, position, id ->
            val element = arrayAdapter.getItem(position) // The item that was clicked

            if(!(this.application as MyApplication).cityName.equals(element))
                for(_city in cityList){
                    if(_city.cityName.equals(element)){

                        (this.application as MyApplication).longitude = _city.longitude
                        (this.application as MyApplication).latitude =  _city.latitude

                        (this.application as MyApplication).cityName = element
                        (this.application as MyApplication).countryCode = _city.countryCode
                        (this.application as MyApplication).lastRefresh = _city.lastRefresh
                        (this.application as MyApplication).shouldRefresh = true

                        saveDataInSP("cityname", element)
                        saveDataInSP("temp_unit", (this.application as MyApplication).temp_unit)
                        saveDataInSP("dist_unit", (this.application as MyApplication).dist_unit)
                        saveDataInSP("longitude", (this.application as MyApplication).longitude.toString())
                        saveDataInSP("latitude", (this.application as MyApplication).latitude.toString())

                        break;
                    }
                }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
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
}