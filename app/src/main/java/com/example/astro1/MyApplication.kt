package com.example.astro1

import android.app.Application
import org.json.JSONStringer
import java.time.LocalDateTime


class MyApplication : Application() {
    public var latitude: Double = 51.75
    public var longitude: Double = 19.46667
    public var refreshTime: Int = 20
    public var cityName: String = "Lodz"
    public var countryCode: String = ""
    public var weather : String = ""
    public var forecast : String = "";
    public var temp_unit : String = "Celsius"
    public var dist_unit : String = "Kilometer"
    public var lastRefresh : String = "";
    public var shouldRefresh : Boolean = false;
}