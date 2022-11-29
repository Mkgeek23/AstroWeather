package com.example.astro1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_view_pager.*
import java.io.IOException
import java.net.InetAddress
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ViewPager : AppCompatActivity() {

    private var url : String = "http://api.openweathermap.org/data/2.5/"
    private var cnt : Int = 40
    private var appId : String = "e1296b8aa9e146f9d1a02b85dd5d57bf"
    private var cityList : ArrayList<City> = ArrayList()

    private var offline : Boolean = false

    private var myViewPagerAdapter = MyViewPagerAdapter(supportFragmentManager)

    var gson : Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_view_pager)

        viewPager.offscreenPageLimit = 4

        var toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        setClock()

        //Odczyt
        loadDataFromSP()

        localizationText.setText( getLocalization())


            val adapter = myViewPagerAdapter
            adapter.addFragment(SunFragment(), "Sun")
            adapter.addFragment(MoonFragment(), "Moon")
            adapter.addFragment(Weather1Fragment(), "Weather")
            adapter.addFragment(ForecastFragment(), "Forecast")

            viewPager.adapter = adapter
            tabs.setupWithViewPager(viewPager)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.refresh) {
            refresher()
            if (offline && !isInternetAvailable(applicationContext)) toast("Brak połączenia z siecią")
        }
        if(item.itemId == R.id.city_list) {
            val intent = Intent(this, CityList::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refresher(){
        if(isInternetAvailable(applicationContext)) {
            toast ("Odswiezam dane")
            loadWeatherData()
            offline = false
        }else if(!offline){
            toast ("Odswiezam stare dane")
            var readed : Boolean = false
            loadDataFromSP()
            for(_city in cityList){
                if (_city.cityName.equals((this.application as MyApplication).cityName)){
                    readed = true
                    (this.application as MyApplication).weather = _city.weather
                    (this.application as MyApplication).forecast = _city.forecast
                    (this.application as MyApplication).lastRefresh = _city.lastRefresh
                    (myViewPagerAdapter.getItem(2) as Weather1Fragment).getWeatherDetails()
                    (myViewPagerAdapter.getItem(3) as ForecastFragment).getForecastDetails()
                    toast("Wczytano stare dane")
                }
            }
            if(!readed)
                toast("Nie udało się wczytać danych")
            offline = true
        }
    }



    private fun loadWeatherData(){

        if(isInternetAvailable(applicationContext)){

            var tempUrl : String = "";
            var city : String = (this.application as MyApplication).cityName;
            var country : String = (this.application as MyApplication).countryCode;
            if(city.equals("")){
                //Show error message;
            }else {
                if (!country.equals("")) {
                    tempUrl = url + "weather?q=" + city + "," + country + "&appid=" + appId;
                } else {
                    tempUrl = url + "weather?q=" + city + "&appid=" + appId;
                }
                val stringRequest = StringRequest(
                    Request.Method.POST,
                    tempUrl,
                    Response.Listener { response ->
                        (this.application as MyApplication).weather = response
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        (this.application as MyApplication).lastRefresh = current.format(formatter)

                        (myViewPagerAdapter.getItem(2) as Weather1Fragment).getWeatherDetails()
                        toast("Wczytano dane pogodowe")

                        loadDataFromSP()

                        var i : Int = 0
                        for(_city in cityList){
                            if(_city.cityName.equals(city)){
                                cityList.get(i).weather = response
                                cityList.get(i).lastRefresh = current.format(formatter)
                                break
                            }
                            i++
                        }
                        saveDataInSP()

                        loadForecastData()

                    },
                    Response.ErrorListener { error ->
                        toast(error.toString())
                    })
                val requestQueue: RequestQueue = Volley.newRequestQueue(applicationContext)
                requestQueue.add(stringRequest);
            }
        }
    }

    private fun saveDataInSP(){
        var sp : SharedPreferences = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE)

        var editor : SharedPreferences.Editor = sp.edit()

        //Zapis
        try {
            editor.putString("citylist", gson.toJson(cityList))
            editor.commit()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadDataFromSP(){
        var sp : SharedPreferences = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE)
        try {
            var s : String? = sp.getString("citylist", "")
            Log.d("WCZYTANO", s.toString())
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

    private fun loadForecastData(){

        if(isInternetAvailable(applicationContext)) {
            var tempUrl: String = "";
            var city: String = (this.application as MyApplication).cityName;
            var country: String = (this.application as MyApplication).countryCode;
            if (city.equals("")) {
                //Show error message;
            } else {
                if (!country.equals("")) {
                    tempUrl =
                        url + "forecast?q=" + city + "," + country + "&cnt=" + cnt + "&appid=" + appId
                } else {
                    tempUrl = url + "forecast?q=" + city + "&cnt=" + cnt + "&appid=" + appId
                }
                val stringRequest = StringRequest(
                    Request.Method.POST,
                    tempUrl,
                    Response.Listener { response ->
                        (this.application as MyApplication).forecast = response

                        loadDataFromSP()

                        var i : Int = 0
                        for(_city in cityList){
                            if(_city.cityName.equals(city)){
                                cityList.get(i).forecast = response
                                break
                            }
                            i++
                        }

                        toast("Wczytano dane prognozowe")

                        saveDataInSP()

                        (myViewPagerAdapter.getItem(3) as ForecastFragment).getForecastDetails()
                    },
                    Response.ErrorListener { error ->
                        toast(error.toString())
                    })
                val requestQueue: RequestQueue = Volley.newRequestQueue(applicationContext)
                requestQueue.add(stringRequest);
            }
        }
    }

    fun toast(text: String){
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }


    class MyViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager){

        private val fragmentList : MutableList<Fragment> = ArrayList()
        private val titleList : MutableList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return  fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String){
            fragmentList.add(fragment)
            titleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titleList[position]
        }
    }

    private fun getLocalization():String{
        var localization: String = ""

        var latitude: Double = (this.application as MyApplication).latitude
        if(latitude<0.0)
            localization+=String.format("%.2f", -latitude) + "°S "
        else
            localization+=String.format("%.2f", latitude) + "°N "

        var longitude: Double = (this.application as MyApplication).longitude
        if(longitude<0.0)
            localization+=String.format("%.2f", -longitude) + "°W"
        else
            localization+=String.format("%.2f", longitude) + "°E"

        return localization
    }

    private fun setClock(){
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val formatted = current.format(formatter)
        clock.text = formatted.toString()

        refresh()
    }

    private fun refresh(){
        val handler: Handler = Handler()
        val runnable: Runnable = Runnable {
            //handler.removeCallbacksAndMessages(null)
            setClock()

            if(!(this.application as MyApplication).lastRefresh.equals("")) {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val dateTime = LocalDateTime.parse((this.application as MyApplication).lastRefresh, formatter)
                if (LocalDateTime.now().compareTo(dateTime.plusSeconds((this.application as MyApplication).refreshTime.toLong())) == 1 || (this.application as MyApplication).shouldRefresh) {
                    if((this.application as MyApplication).shouldRefresh) (this.application as MyApplication).shouldRefresh = false
                    refresher()
                }
            }else
                refresher()
        }

        handler.postDelayed(runnable, 1000);
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