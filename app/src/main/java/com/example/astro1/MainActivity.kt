package com.example.astro1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var cityList : ArrayList<City> = ArrayList()
    var gson : Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnViewPager.setOnClickListener{
            val intent = Intent(this, ViewPager::class.java)
            startActivity(intent)
        }

        btnOptions.setOnClickListener{
            val intent = Intent(this, OptionsActivity::class.java)
            startActivity(intent)
        }

        btnExit.setOnClickListener{
            finish();
        }

        loadDataFromSP()

    }

    private fun loadDataFromSP(){
        var sp : SharedPreferences = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE)
        try {
            var s = sp.getString("cityname", "").toString()
            if(!s.equals("")) (this.application as MyApplication).cityName = s
            Log.d("1", s)
            s = sp.getString("temp_unit", "").toString()
            if(!s.equals("")) (this.application as MyApplication).temp_unit = s
            Log.d("2", s)
            s = sp.getString("dist_unit", "").toString()
            if(!s.equals("")) (this.application as MyApplication).dist_unit = s
            Log.d("3", s)
            s = sp.getString("longitude", "").toString()
            if(!s.equals("")) (this.application as MyApplication).longitude = s.toDouble()
            Log.d("4", s)
            s = sp.getString("latitude", "").toString()
            if(!s.equals("")) (this.application as MyApplication).latitude = s.toDouble()
            Log.d("5", s)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }
}